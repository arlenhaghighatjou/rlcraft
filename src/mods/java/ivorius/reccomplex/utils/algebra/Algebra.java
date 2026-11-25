package ivorius.reccomplex.utils.algebra;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import ivorius.ivtoolkit.tools.Pairs;
import ivorius.ivtoolkit.tools.Ranges;
import ivorius.ivtoolkit.tools.Visitor;
import ivorius.ivtoolkit.util.IvFunctions;
import ivorius.reccomplex.utils.PrecedenceSet;
import ivorius.reccomplex.utils.PrecedenceSets;
import ivorius.reccomplex.utils.tokenizer.SymbolTokenizer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

public class Algebra<T> {
   protected final Set<Algebra.Operator<T>> operators;
   @Nonnull
   protected SymbolTokenizer.CharacterRules characterRules;

   public Algebra() {
      this(new SymbolTokenizer.SimpleCharacterRules());
   }

   public Algebra(@Nonnull SymbolTokenizer.CharacterRules characterRules) {
      this.operators = new HashSet();
      this.characterRules = characterRules;
   }

   @SafeVarargs
   public Algebra(Algebra.Operator<T>... operators) {
      this(new SymbolTokenizer.SimpleCharacterRules(), operators);
   }

   @SafeVarargs
   public Algebra(@Nonnull SymbolTokenizer.CharacterRules characterRules, Algebra.Operator<T>... operators) {
      this.operators = new HashSet();
      this.characterRules = characterRules;
      Collections.addAll(this.operators, operators);
   }

   protected static <T> Algebra.ExpressionToken<T> reduceExpressions(List<SymbolTokenizer.Token> tokens, NavigableSet<PrecedenceSet<Algebra.Operator<T>>> operators, int stringIndex) throws ParseException {
      if (tokens.size() == 0) {
         throw new ParseException("Expected Expression", stringIndex);
      } else {
         SymbolTokenizer.Token startToken;
         if (tokens.size() == 1 && (startToken = (SymbolTokenizer.Token)tokens.get(0)) instanceof Algebra.ConstantToken) {
            tokens.remove(0);
            Algebra.ExpressionToken<T> exp = new Algebra.ExpressionToken<T>(startToken.startIndex, startToken.endIndex, new Algebra.UnparsedVariable(startToken.startIndex, ((Algebra.ConstantToken)startToken).identifier));
            tokens.add(exp);
            return exp;
         } else {
            for(PrecedenceSet<Algebra.Operator<T>> curOperators : operators) {
               Stack<Algebra.BuildingExpression<T>> expressionStack = new Stack();
               expressionStack.push(new Algebra.BuildingExpression((Algebra.Operator)null, stringIndex, ((SymbolTokenizer.Token)tokens.get(tokens.size() - 1)).endIndex, 0, -1));

               for(int t = 0; t < tokens.size(); ++t) {
                  SymbolTokenizer.Token token = (SymbolTokenizer.Token)tokens.get(t);
                  if (token instanceof Algebra.AmbiguousOperatorToken) {
                     throw new IllegalArgumentException("Ambiguous operators are not supported at this point");
                  }

                  if (token instanceof Algebra.OperatorToken) {
                     Algebra.OperatorToken<T> operatorToken = (Algebra.OperatorToken)token;
                     Algebra.Operator<T> operator = operatorToken.operator;
                     if (operator.precedence < curOperators.getPrecedence()) {
                        throw new ParseException("Internal Error (Operator Sorting)", operatorToken.startIndex);
                     }

                     if (curOperators.contains(operator)) {
                        if (((Algebra.BuildingExpression)expressionStack.peek()).isAtLastSymbol() && ((Algebra.BuildingExpression)expressionStack.peek()).operator.hasRightArgument() && operator.hasLeftArgument()) {
                           t -= reduceBuildingExpression((Algebra.BuildingExpression)expressionStack.pop(), tokens, operators.tailSet(curOperators, false), t);
                        }

                        if (operatorToken.symbolIndex != 0 && !((Algebra.BuildingExpression)expressionStack.peek()).isNext(operatorToken.symbolIndex)) {
                           throw new ParseException(String.format("Unexpected Token '%s'", operator.getSymbols()[operatorToken.symbolIndex]), operatorToken.startIndex);
                        }

                        if (operatorToken.symbolIndex > 0 || operator.hasLeftArgument()) {
                           Integer lastTokenIndex = ((Algebra.BuildingExpression)expressionStack.peek()).currentTokenIndex;
                           reduceExpressions(tokens.subList(lastTokenIndex.intValue(), t), operators.tailSet(curOperators, false), operatorToken.startIndex);
                           t -= t - lastTokenIndex.intValue() - 1;
                        }

                        if (operatorToken.symbolIndex == 0) {
                           expressionStack.push(new Algebra.BuildingExpression(operator, operatorToken.startIndex, operatorToken.endIndex, t));
                        } else {
                           ((Algebra.BuildingExpression)expressionStack.peek()).advance(operatorToken.startIndex, operatorToken.endIndex, t, operatorToken.symbolIndex);
                        }

                        if (((Algebra.BuildingExpression)expressionStack.peek()).isAtLastSymbol() && !operator.hasRightArgument()) {
                           Algebra.BuildingExpression<T> curExp = (Algebra.BuildingExpression)expressionStack.pop();
                           int numberOfArguments = operator.getNumberOfArguments();
                           reduceOperator(tokens.subList(t - numberOfArguments, t), curExp.startStringIndex, curExp.endStringIndex, operator);
                           t -= numberOfArguments - 1;
                        }

                        tokens.remove(t--);
                     }
                  }
               }

               while(((Algebra.BuildingExpression)expressionStack.peek()).isAtLastSymbol() && ((Algebra.BuildingExpression)expressionStack.peek()).operator.hasRightArgument()) {
                  reduceBuildingExpression((Algebra.BuildingExpression)expressionStack.pop(), tokens, operators.tailSet(curOperators, false), tokens.size());
               }

               if (expressionStack.size() > 1) {
                  Algebra.BuildingExpression<T> curExp = (Algebra.BuildingExpression)expressionStack.peek();
                  String[] symbols = curExp.operator.getSymbols();
                  throw new ParseException(String.format("Expected Token '%s'", symbols[curExp.expectedSymbolIndex()]), curExp.currentStringIndex + symbols[curExp.currentSymbolIndex].length());
               }
            }

            if (tokens.size() <= 1 && tokens.get(0) instanceof Algebra.ExpressionToken) {
               return (Algebra.ExpressionToken)tokens.get(0);
            } else {
               throw new ParseException("Expected Operator", ((SymbolTokenizer.Token)tokens.get(1)).startIndex);
            }
         }
      }
   }

   protected static <T> void reduceOperator(List<SymbolTokenizer.Token> tokens, int startIndex, int endIndex, Algebra.Operator<T> operator) throws ParseException {
      if (tokens.size() < 1) {
         throw new ParseException("Internal Error (Missing Arguments)", startIndex);
      } else {
         Algebra.Expression<T, ?>[] expressions = new Algebra.Expression[tokens.size()];

         for(int i = 0; i < tokens.size(); ++i) {
            SymbolTokenizer.Token token = (SymbolTokenizer.Token)tokens.get(i);
            if (!(token instanceof Algebra.ExpressionToken)) {
               throw new ParseException("Internal Error (Unevaluated Token)", startIndex);
            }

            expressions[i] = ((Algebra.ExpressionToken)token).expression;
         }

         tokens.clear();
         tokens.add(new Algebra.ExpressionToken(startIndex, endIndex, new Algebra.Operation(startIndex, operator, expressions)));
      }
   }

   protected static <T> int reduceBuildingExpression(Algebra.BuildingExpression<T> buildingExpression, List<SymbolTokenizer.Token> tokens, NavigableSet<PrecedenceSet<Algebra.Operator<T>>> followingOperators, int currentTokenIndex) throws ParseException {
      Algebra.Operator<T> endedOperator = buildingExpression.operator;
      int numberOfArguments = endedOperator.getNumberOfArguments();
      Integer lastTokenIndex = buildingExpression.currentTokenIndex;
      reduceExpressions(tokens.subList(lastTokenIndex.intValue(), currentTokenIndex), followingOperators, buildingExpression.endStringIndex);
      int difference = currentTokenIndex - lastTokenIndex.intValue() - 1;
      currentTokenIndex = currentTokenIndex - difference;
      reduceOperator(tokens.subList(currentTokenIndex - numberOfArguments, currentTokenIndex), buildingExpression.startStringIndex, buildingExpression.endStringIndex, endedOperator);
      return difference + numberOfArguments - 1;
   }

   public boolean addOperators(Collection<Algebra.Operator<T>> operators) {
      return this.operators.addAll(operators);
   }

   public boolean addOperator(Algebra.Operator<T> operator) {
      return this.operators.add(operator);
   }

   public boolean removeOperators(Collection<Algebra.Operator<T>> operators) {
      return this.operators.removeAll(operators);
   }

   public boolean removeOperator(Algebra.Operator<T> operator) {
      return this.operators.remove(operator);
   }

   public Set<Algebra.Operator<T>> operators() {
      return Collections.unmodifiableSet(this.operators);
   }

   @Nonnull
   public SymbolTokenizer.CharacterRules getCharacterRules() {
      return this.characterRules;
   }

   public void setCharacterRules(@Nonnull SymbolTokenizer.CharacterRules characterRules) {
      this.characterRules = characterRules;
   }

   public SymbolTokenizer.TokenFactory getTokenFactory() {
      return new SymbolTokenizer.TokenFactory() {
         protected boolean hasAt(String string, int index, String symbol) {
            return string.regionMatches(index, symbol, 0, symbol.length());
         }

         @Nullable
         public SymbolTokenizer.Token tryConstructSymbolTokenAt(int index, @Nonnull String string) {
            for(List<Pair<Algebra.Operator<T>, Integer>> symbolPairList : this.sortedSymbols()) {
               String symbol = this.getStringSymbol((Pair)symbolPairList.get(0));
               if (this.hasAt(string, index, symbol)) {
                  return (SymbolTokenizer.Token)(symbolPairList.size() > 1 ? new Algebra.AmbiguousOperatorToken(index, index + symbol.length(), symbolPairList) : new Algebra.OperatorToken(index, index + symbol.length(), (Algebra.Operator)((Pair)symbolPairList.get(0)).getLeft(), ((Integer)((Pair)symbolPairList.get(0)).getRight()).intValue()));
               }
            }

            return null;
         }

         @Nonnull
         protected TreeSet<List<Pair<Algebra.Operator<T>, Integer>>> sortedSymbols() {
            ArrayList<Pair<Algebra.Operator<T>, Integer>> symbols = Lists.newArrayList(Iterables.concat((Iterable)Algebra.this.operators.stream().map((operator) -> {
               return Pairs.pairLeft(operator, Ranges.toIterable(operator.symbols.length));
            }).collect(Collectors.toList())));
            TreeSet<List<Pair<Algebra.Operator<T>, Integer>>> sortedSymbols = new TreeSet((o1, o2) -> {
               return this.getStringSymbol((Pair)o2.get(0)).compareTo(this.getStringSymbol((Pair)o1.get(0)));
            });
            sortedSymbols.addAll((Collection)IvFunctions.group(symbols, this::getStringSymbol).stream().map(Lists::newArrayList).collect(Collectors.toList()));
            return sortedSymbols;
         }

         private String getStringSymbol(Pair<Algebra.Operator<T>, Integer> symbol) {
            return ((Algebra.Operator)symbol.getLeft()).getSymbols()[((Integer)symbol.getRight()).intValue()];
         }

         @Nonnull
         public SymbolTokenizer.Token constructStringToken(int index, @Nonnull String string) {
            return new Algebra.ConstantToken(index, index + string.length(), string);
         }
      };
   }

   @Nonnull
   public <V> Algebra.Expression<T, V> parse(String string, Algebra.VariableParser<V> variableParser) throws ParseException {
      return this.constructExpression((new SymbolTokenizer(this.characterRules, this.getTokenFactory())).tokenize(string), variableParser);
   }

   public <A, V> Algebra.Expression<T, V> constructExpression(List<SymbolTokenizer.Token> tokens, Algebra.VariableParser<V> variableParser) throws ParseException {
      Algebra.Expression<T, V> expression = reduceExpressions(Lists.newArrayList(tokens), new TreeSet(PrecedenceSets.group(this.operators)), 0).expression;
      return expression.parse(variableParser);
   }

   protected static class AmbiguousOperatorToken<T> extends SymbolTokenizer.Token {
      final List<Pair<Algebra.Operator<T>, Integer>> operators;

      public AmbiguousOperatorToken(int startIndex, int endIndex, List<Pair<Algebra.Operator<T>, Integer>> operators) {
         super(startIndex, endIndex);
         this.operators = Collections.unmodifiableList(operators);
      }

      public Algebra.OperatorToken<T> toOperatorToken(Pair<Algebra.Operator<T>, Integer> pair) {
         return new Algebra.OperatorToken<T>(this.startIndex, this.endIndex, (Algebra.Operator)pair.getKey(), ((Integer)pair.getValue()).intValue());
      }
   }

   protected static class BuildingExpression<T> {
      public int startStringIndex;
      public int currentStringIndex;
      public int endStringIndex;
      public int currentTokenIndex;
      public Algebra.Operator<T> operator;
      public int currentSymbolIndex;

      public BuildingExpression(Algebra.Operator<T> operator, int startStringIndex, int endStringIndex, int currentTokenIndex) {
         this.operator = operator;
         this.startStringIndex = startStringIndex;
         this.currentStringIndex = startStringIndex;
         this.endStringIndex = endStringIndex;
         this.currentTokenIndex = currentTokenIndex;
         this.currentSymbolIndex = 0;
      }

      public BuildingExpression(Algebra.Operator<T> operator, int startStringIndex, int endStringIndex, int currentTokenIndex, int currentSymbolIndex) {
         this.operator = operator;
         this.startStringIndex = startStringIndex;
         this.currentStringIndex = startStringIndex;
         this.endStringIndex = endStringIndex;
         this.currentTokenIndex = currentTokenIndex;
         this.currentSymbolIndex = currentSymbolIndex;
      }

      public void advance(int stringStartIndex, int stringEndIndex, int tokenIndex, int symbolIndex) {
         this.currentStringIndex = stringStartIndex;
         this.endStringIndex = stringEndIndex;
         this.currentTokenIndex = tokenIndex;
         this.currentSymbolIndex = symbolIndex;
      }

      public int expectedSymbolIndex() {
         return this.currentSymbolIndex + 1;
      }

      public boolean isNext(int symbolIndex) {
         return this.currentSymbolIndex + 1 == symbolIndex;
      }

      public boolean isAtLastSymbol() {
         return this.operator != null && this.operator.getSymbols().length - 1 == this.currentSymbolIndex;
      }
   }

   public static class Constant<T, V> extends Algebra.Expression<T, V> {
      public String representation;
      public T value;

      public Constant(int index, T value, String representation) {
         super(index);
         this.value = value;
         this.representation = representation;
      }

      public T evaluate(@Nullable Function<V, T> input) {
         return this.value;
      }

      public boolean walkVariables(Visitor<Algebra.Variable<T, V>> visitor) {
         return true;
      }

      public String toString(Function<String, String> stringMapper) {
         return this.representation;
      }
   }

   protected static class ConstantToken extends SymbolTokenizer.Token {
      public String identifier;

      public ConstantToken(int startIndex, int endIndex, String identifier) {
         super(startIndex, endIndex);
         this.identifier = identifier;
      }
   }

   public abstract static class Expression<T, V> {
      public final int index;

      public Expression(int index) {
         this.index = index;
      }

      public abstract T evaluate(@Nullable Function<V, T> var1);

      public abstract boolean walkVariables(Visitor<Algebra.Variable<T, V>> var1);

      public abstract String toString(Function<String, String> var1);

      public Algebra.Expression<T, V> parse(Algebra.VariableParser<V> parser) throws ParseException {
         return this;
      }
   }

   protected static class ExpressionToken<T> extends SymbolTokenizer.Token {
      public Algebra.Expression<T, ?> expression;

      public ExpressionToken(int startIndex, int endIndex, Algebra.Expression<T, ?> expression) {
         super(startIndex, endIndex);
         this.expression = expression;
      }
   }

   public static class Operation<T, V> extends Algebra.Expression<T, V> {
      protected Algebra.Operator<T> operator;
      protected Algebra.Expression<T, V>[] expressions;

      @SafeVarargs
      public Operation(int index, Algebra.Operator<T> operator, Algebra.Expression<T, V>... expressions) {
         super(index);
         this.operator = operator;
         this.expressions = expressions;
      }

      public Algebra.Expression getExpression(int index) {
         return this.expressions[index];
      }

      public void setExpression(int index, Algebra.Expression<T, V> expression) {
         this.expressions[index] = expression;
      }

      public T evaluate(@Nullable Function<V, T> input) {
         return this.operator.evaluate(input, this.expressions);
      }

      public boolean walkVariables(Visitor<Algebra.Variable<T, V>> visitor) {
         for(Algebra.Expression<T, V> expression : this.expressions) {
            if (!expression.walkVariables(visitor)) {
               return false;
            }
         }

         return true;
      }

      public Algebra.Expression<T, V> parse(Algebra.VariableParser<V> parser) throws ParseException {
         for(int i = 0; i < this.expressions.length; ++i) {
            this.expressions[i] = this.expressions[i].parse(parser);
         }

         return this;
      }

      public String toString(Function<String, String> stringMapper) {
         StringBuilder builder = new StringBuilder();
         int idx = 0;
         String[] symbols = this.operator.getSymbols();
         boolean hasSpaceRightOfFirst = this.operator.hasLeftArgument();
         boolean hasSpaceLeftOfLast = this.operator.hasRightArgument();
         if (this.operator.hasLeftArgument()) {
            builder.append(this.expressions[idx++].toString(stringMapper));
         }

         for(int i = 0; i < symbols.length; ++i) {
            if ((i > 0 || this.operator.hasLeftArgument()) && (i != symbols.length - 1 || hasSpaceLeftOfLast)) {
               builder.append(' ');
            }

            builder.append(symbols[i]);
            if ((i < symbols.length - 1 || this.operator.hasRightArgument()) && (i != 0 || hasSpaceRightOfFirst)) {
               builder.append(' ');
            }

            if (i < symbols.length - 1 || this.operator.hasRightArgument()) {
               builder.append(this.expressions[idx++].toString(stringMapper));
            }
         }

         return builder.toString();
      }
   }

   public abstract static class Operator<T> implements PrecedenceSet.NativeEntry {
      protected float precedence;
      protected boolean hasLeftArgument;
      protected boolean hasRightArgument;
      protected String[] symbols;

      public Operator(float precedence, boolean hasLeftArgument, boolean hasRightArgument, String... symbols) {
         this.precedence = precedence;
         this.hasLeftArgument = hasLeftArgument;
         this.hasRightArgument = hasRightArgument;
         this.symbols = symbols;
      }

      public float getPrecedence() {
         return this.precedence;
      }

      public void setPrecedence(float precedence) {
         this.precedence = precedence;
      }

      public boolean hasLeftArgument() {
         return this.hasLeftArgument;
      }

      public void setHasLeftArgument(boolean hasLeftArgument) {
         this.hasLeftArgument = hasLeftArgument;
      }

      public boolean hasRightArgument() {
         return this.hasRightArgument;
      }

      public void setHasRightArgument(boolean hasRightArgument) {
         this.hasRightArgument = hasRightArgument;
      }

      public String[] getSymbols() {
         return this.symbols;
      }

      public void setSymbols(String[] symbols) {
         this.symbols = symbols;
      }

      public int getNumberOfArguments() {
         return this.symbols.length - 1 + (this.hasLeftArgument() ? 1 : 0) + (this.hasRightArgument() ? 1 : 0);
      }

      public abstract <V> T evaluate(Function<V, T> var1, Algebra.Expression<T, V>[] var2);
   }

   protected static class OperatorToken<T> extends SymbolTokenizer.Token {
      public final Algebra.Operator<T> operator;
      public final int symbolIndex;

      public OperatorToken(int startIndex, int endIndex, Algebra.Operator<T> operator, int symbolIndex) {
         super(startIndex, endIndex);
         this.operator = operator;
         this.symbolIndex = symbolIndex;
      }
   }

   public static class UnparsedVariable<T, V> extends Algebra.Expression<T, V> {
      public final String identifier;

      public UnparsedVariable(int index, String identifier) {
         super(index);
         this.identifier = identifier;
      }

      public T evaluate(@Nullable Function<V, T> input) {
         throw new IllegalStateException();
      }

      public boolean walkVariables(Visitor<Algebra.Variable<T, V>> visitor) {
         throw new IllegalStateException();
      }

      public String toString(Function<String, String> stringMapper) {
         return (String)stringMapper.apply(this.identifier);
      }

      public Algebra.Expression<T, V> parse(Algebra.VariableParser<V> parser) throws ParseException {
         return new Algebra.Variable<T, V>(this.index, this.identifier, parser.apply(this.identifier));
      }
   }

   public static class Variable<T, V> extends Algebra.Expression<T, V> {
      public final String identifier;
      public final V value;

      public Variable(int index, String identifier, V value) {
         super(index);
         this.identifier = identifier;
         this.value = value;
      }

      public T evaluate(@Nullable Function<V, T> input) {
         Preconditions.checkNotNull(input);
         return (T)input.apply(this.value);
      }

      public boolean walkVariables(Visitor<Algebra.Variable<T, V>> visitor) {
         return visitor.visit(this);
      }

      public String toString(Function<String, String> stringMapper) {
         return (String)stringMapper.apply(this.identifier);
      }
   }

   interface VariableParser<T> {
      T apply(String var1) throws ParseException;
   }
}
