package org.orecruncher.lib.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Compiler {
   private static final LazyVariant PARAMS_START = () -> {
      return null;
   };
   private final OperatorTable operators;
   private final FunctionTable functions;
   private final VariableTable variables;

   public Compiler(@Nullable OperatorTable operators, @Nullable FunctionTable functions, @Nullable VariableTable variables) {
      this.operators = operators != null ? operators : new OperatorTable();
      this.functions = functions != null ? functions : new FunctionTable();
      this.variables = variables != null ? variables : new VariableTable();
   }

   public Compiler.Result compile(@Nonnull String expression) {
      Stack<LazyVariant> stack = new Stack();
      List<String> rpn = this.getRPN(expression);

      for(String token : rpn) {
         if (this.operators.containsKey(token)) {
            Operator op = (Operator)this.operators.get(token);
            LazyVariant result;
            if (op.isUnary()) {
               LazyVariant v1 = (LazyVariant)stack.pop();
               result = () -> {
                  return op.eval(v1);
               };
            } else {
               LazyVariant v1 = (LazyVariant)stack.pop();
               LazyVariant v2 = (LazyVariant)stack.pop();
               result = () -> {
                  return op.eval(v2, v1);
               };
            }

            stack.push(result);
         } else if (this.variables.containsKey(token)) {
            stack.push(this.variables.get(token));
         } else if (!this.functions.containsKey(token)) {
            if ("(".equals(token)) {
               stack.push(PARAMS_START);
            } else if (token.charAt(0) == '\'') {
               String s = token.substring(1, token.length() - 1);
               stack.push(new StringValue(s));
            } else {
               float val = Float.parseFloat(token);
               stack.push(new NumberValue(val));
            }
         } else {
            LazyFunction f = (LazyFunction)this.functions.get(token);
            ArrayList<LazyVariant> p = new ArrayList(!f.numParamsVaries() ? f.getNumParams() : 0);

            while(!stack.isEmpty() && stack.peek() != PARAMS_START) {
               p.add(0, stack.pop());
            }

            if (stack.peek() == PARAMS_START) {
               stack.pop();
            }

            LazyVariant[] parms = new LazyVariant[p.size()];
            p.toArray(parms);
            LazyVariant fResult = () -> {
               return f.lazyEval(parms).eval();
            };
            stack.push(fResult);
         }
      }

      return new Compiler.Result((LazyVariant)stack.pop(), rpn);
   }

   private static boolean isNumber(String st) {
      if (st.charAt(0) == '-' && st.length() == 1) {
         return false;
      } else if (st.charAt(0) == '+' && st.length() == 1) {
         return false;
      } else if (st.charAt(0) != 'e' && st.charAt(0) != 'E') {
         for(char ch : st.toCharArray()) {
            if (!Character.isDigit(ch) && ch != '-' && ch != '.' && ch != 'e' && ch != 'E' && ch != '+') {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private List<String> shuntingYard(String expression) {
      List<String> outputQueue = new ArrayList();
      Stack<String> stack = new Stack();
      Tokenizer tokenizer = new Tokenizer(expression, this.operators.keySet());
      String lastFunction = null;

      String token;
      for(String previousToken = null; tokenizer.hasNext(); previousToken = token) {
         token = tokenizer.next();
         if (isNumber(token)) {
            outputQueue.add(token);
         } else if (token.charAt(0) == '\'') {
            outputQueue.add(token);
         } else if (this.variables.containsKey(token)) {
            outputQueue.add(token);
         } else if (this.functions.containsKey(token)) {
            stack.push(token);
            lastFunction = token;
         } else if (Character.isLetter(token.charAt(0))) {
            stack.push(token);
         } else if (",".equals(token)) {
            if (this.operators.containsKey(previousToken)) {
               throw new ExpressionException("Missing parameter(s) for operator " + previousToken + " at character position " + (tokenizer.getPos() - 1 - previousToken.length()));
            }

            while(!stack.isEmpty() && !"(".equals(stack.peek())) {
               outputQueue.add(stack.pop());
            }

            if (stack.isEmpty()) {
               throw new ExpressionException("Parse error for function '" + lastFunction + "'");
            }
         } else if (this.operators.containsKey(token)) {
            if (",".equals(previousToken) || "(".equals(previousToken)) {
               throw new ExpressionException("Missing parameter(s) for operator " + token + " at character position " + (tokenizer.getPos() - token.length()));
            }

            Operator o1 = (Operator)this.operators.get(token);

            for(String token2 = stack.isEmpty() ? null : (String)stack.peek(); token2 != null && this.operators.containsKey(token2) && (o1.isLeftAssoc() && o1.getPrecedence() <= ((Operator)this.operators.get(token2)).getPrecedence() || o1.getPrecedence() < ((Operator)this.operators.get(token2)).getPrecedence()); token2 = stack.isEmpty() ? null : (String)stack.peek()) {
               outputQueue.add(stack.pop());
            }

            stack.push(token);
         } else if ("(".equals(token)) {
            if (previousToken != null) {
               if (isNumber(previousToken)) {
                  throw new ExpressionException("Missing operator at character position " + tokenizer.getPos());
               }

               if (this.functions.containsKey(previousToken)) {
                  outputQueue.add(token);
               }
            }

            stack.push(token);
         } else if (")".equals(token)) {
            if (this.operators.containsKey(previousToken)) {
               throw new ExpressionException("Missing parameter(s) for operator " + previousToken + " at character position " + (tokenizer.getPos() - 1 - previousToken.length()));
            }

            while(!stack.isEmpty() && !"(".equals(stack.peek())) {
               outputQueue.add(stack.pop());
            }

            if (stack.isEmpty()) {
               throw new ExpressionException("Mismatched parentheses");
            }

            stack.pop();
            if (!stack.isEmpty() && this.functions.containsKey(stack.peek())) {
               outputQueue.add(stack.pop());
            }
         }
      }

      while(!stack.isEmpty()) {
         token = (String)stack.pop();
         if ("(".equals(token) || ")".equals(token)) {
            throw new ExpressionException("Mismatched parentheses");
         }

         if (!this.operators.containsKey(token)) {
            throw new ExpressionException("Unknown operator or function: " + token);
         }

         outputQueue.add(token);
      }

      return outputQueue;
   }

   private List<String> getRPN(@Nonnull String expression) {
      List<String> rpn = this.shuntingYard(expression);
      this.validate(rpn);
      return rpn;
   }

   private void validate(List<String> rpn) {
      Stack<Integer> stack = new Stack();
      stack.push(Integer.valueOf(0));

      for(String token : rpn) {
         if (this.operators.containsKey(token)) {
            if (((Operator)this.operators.get(token)).isUnary()) {
               if (((Integer)stack.peek()).intValue() < 1) {
                  throw new ExpressionException("Missing parameter(s) for operator " + token);
               }

               stack.set(stack.size() - 1, Integer.valueOf(((Integer)stack.peek()).intValue() - 1 + 1));
            } else {
               if (((Integer)stack.peek()).intValue() < 2) {
                  throw new ExpressionException("Missing parameter(s) for operator " + token);
               }

               stack.set(stack.size() - 1, Integer.valueOf(((Integer)stack.peek()).intValue() - 2 + 1));
            }
         } else if (this.variables.containsKey(token)) {
            stack.set(stack.size() - 1, Integer.valueOf(((Integer)stack.peek()).intValue() + 1));
         } else if (this.functions.containsKey(token)) {
            LazyFunction f = (LazyFunction)this.functions.get(token);
            int numParams = ((Integer)stack.pop()).intValue();
            if (!f.numParamsVaries() && numParams != f.getNumParams()) {
               throw new ExpressionException("Function " + token + " expected " + f.getNumParams() + " parameters, got " + numParams);
            }

            if (stack.size() <= 0) {
               throw new ExpressionException("Too many function calls, maximum scope exceeded");
            }

            stack.set(stack.size() - 1, Integer.valueOf(((Integer)stack.peek()).intValue() + 1));
         } else if ("(".equals(token)) {
            stack.push(Integer.valueOf(0));
         } else {
            stack.set(stack.size() - 1, Integer.valueOf(((Integer)stack.peek()).intValue() + 1));
         }
      }

      if (stack.size() > 1) {
         throw new ExpressionException("Too many unhandled function parameter lists");
      } else if (((Integer)stack.peek()).intValue() > 1) {
         throw new ExpressionException("Too many numbers or variables");
      } else if (((Integer)stack.peek()).intValue() < 1) {
         throw new ExpressionException("Empty expression");
      }
   }

   public static class Result {
      public final LazyVariant expression;
      public final List<String> rpn;

      Result(@Nonnull LazyVariant exp, @Nonnull List<String> rpn) {
         this.expression = exp;
         this.rpn = rpn;
      }
   }
}
