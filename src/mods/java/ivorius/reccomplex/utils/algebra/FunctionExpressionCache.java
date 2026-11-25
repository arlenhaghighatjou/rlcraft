package ivorius.reccomplex.utils.algebra;

import ivorius.ivtoolkit.tools.Visitor;
import ivorius.ivtoolkit.util.IvLists;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.text.TextFormatting;

public class FunctionExpressionCache<T, A, U> extends ExpressionCache<T> {
   protected final SortedSet<FunctionExpressionCache.VariableType<T, ? super A, ? super U>> types = new TreeSet();

   public FunctionExpressionCache(Algebra<T> algebra) {
      super(algebra);
   }

   public FunctionExpressionCache(Algebra<T> algebra, T emptyResult, String emptyResultRepresentation) {
      super(algebra, emptyResult, emptyResultRepresentation);
   }

   public void addType(FunctionExpressionCache.VariableType<T, ? super A, ? super U> type) {
      this.types.add(type);
   }

   public void addTypes(Collection<FunctionExpressionCache.VariableType<T, ? super A, ? super U>> types) {
      this.types.addAll(types);
   }

   @SafeVarargs
   public final void addTypes(FunctionExpressionCache.VariableType<T, ? super A, ? super U> type, Function<FunctionExpressionCache.VariableType<T, ? super A, ? super U>, FunctionExpressionCache.VariableType<T, ? super A, ? super U>>... functions) {
      this.addType(type);
      this.addTypes(IvLists.enumerate(type, functions));
   }

   public void removeType(FunctionExpressionCache.VariableType<T, A, U> type) {
      this.types.remove(type);
   }

   public Set<FunctionExpressionCache.VariableType<T, ? super A, ? super U>> types() {
      return Collections.unmodifiableSet(this.types);
   }

   @Nullable
   public FunctionExpressionCache.VariableType<T, ? super A, ? super U> type(String var) {
      return (FunctionExpressionCache.VariableType)this.types.stream().filter((input) -> {
         return var.startsWith(input.prefix) && var.endsWith(input.suffix);
      }).findFirst().orElseGet(() -> {
         return null;
      });
   }

   public FunctionExpressionCache.Validity variableValidity(String var, U u) {
      FunctionExpressionCache.VariableType<T, ? super A, ? super U> type = this.type(var);
      return type == null ? FunctionExpressionCache.Validity.ERROR : type.validity(var.substring(type.prefix.length()), u);
   }

   public FunctionExpressionCache.Validity validity(U u) {
      FunctionExpressionCache.Validity[] worst = new FunctionExpressionCache.Validity[]{FunctionExpressionCache.Validity.KNOWN};
      if (this.parsedExpression != null) {
         this.parsedExpression.walkVariables((s) -> {
            FunctionExpressionCache.Validity validity = this.variableValidity(s.identifier, u);
            if (validity.ordinal() > worst[0].ordinal()) {
               worst[0] = validity;
            }

            return validity.ordinal() < FunctionExpressionCache.Validity.values().length - 1;
         });
         return worst[0];
      } else {
         return FunctionExpressionCache.Validity.ERROR;
      }
   }

   protected Algebra.VariableParser<Function<? extends SupplierCache<? super A>, T>> variableParser() {
      return (var) -> {
         FunctionExpressionCache.VariableType<T, ? super A, ? super U> type = this.type(var);
         if (type != null) {
            return type.parse(var.substring(type.prefix.length()));
         } else {
            throw new ParseException(String.format("Type of '%s' unknown", var), 0);
         }
      };
   }

   public T evaluate(SupplierCache<A> a) {
      Algebra.Expression<T, Function<SupplierCache<? super A>, T>> expression = this.parsedExpression;
      return (T)(this.parsedExpression != null ? expression.evaluate((fun) -> {
         return (T)fun.apply(a);
      }) : this.emptyExpressionResult);
   }

   public T evaluate(Supplier<A> a) {
      return (T)this.evaluate(SupplierCache.of(a));
   }

   public T evaluate(A a) {
      return (T)this.evaluate(SupplierCache.direct(a));
   }

   @Nonnull
   public String getDisplayString(U u) {
      return this.parsedExpression != null ? this.parsedExpression.toString((input) -> {
         return this.variableDisplayString(input, u);
      }) : TextFormatting.RED + this.expression;
   }

   public String variableDisplayString(String variable, U u) {
      FunctionExpressionCache.VariableType<T, ? super A, ? super U> type = this.type(variable);
      return type != null ? type.getRepresentation(variable.substring(type.prefix.length()), type.prefix, type.suffix, u) : TextFormatting.RED + variable;
   }

   public static class AliasType<T, A, U, V extends FunctionExpressionCache.VariableType<T, A, U>> extends FunctionExpressionCache.VariableType<T, A, U> {
      public V parent;

      public AliasType(String prefix, String suffix, V parent) {
         super(prefix, suffix);
         this.parent = parent;
      }

      public Function<SupplierCache<A>, T> parse(String var) throws ParseException {
         return this.parent.parse(var);
      }

      public FunctionExpressionCache.Validity validity(String var, U u) {
         return this.parent.validity(var, u);
      }

      public String getRepresentation(String var, String prefix, String suffix, U u) {
         return this.parent.getRepresentation(var, prefix, suffix, u);
      }
   }

   public static enum Validity {
      KNOWN,
      UNKNOWN,
      ERROR;

      private Validity() {
      }
   }

   public abstract static class VariableType<T, A, U> implements Comparable<FunctionExpressionCache.VariableType> {
      protected String prefix;
      protected String suffix;

      public VariableType(String prefix, String suffix) {
         this.prefix = prefix;
         this.suffix = suffix;
      }

      public String getPrefix() {
         return this.prefix;
      }

      public String getSuffix() {
         return this.suffix;
      }

      public abstract Function<SupplierCache<A>, T> parse(String var1) throws ParseException;

      public abstract FunctionExpressionCache.Validity validity(String var1, U var2);

      public TextFormatting getRepresentation(FunctionExpressionCache.Validity validity) {
         return validity == FunctionExpressionCache.Validity.KNOWN ? TextFormatting.GREEN : (validity == FunctionExpressionCache.Validity.UNKNOWN ? TextFormatting.YELLOW : TextFormatting.RED);
      }

      public String getRepresentation(String var, String prefix, String suffix, U u) {
         return TextFormatting.BLUE + prefix + TextFormatting.RESET + this.getVarRepresentation(var, u) + TextFormatting.RESET + TextFormatting.BLUE + suffix + TextFormatting.RESET;
      }

      protected String getVarRepresentation(String var, U u) {
         return this.getRepresentation(this.validity(var, u)) + var;
      }

      public int compareTo(@Nonnull FunctionExpressionCache.VariableType o) {
         return o.prefix.compareTo(this.prefix);
      }

      public FunctionExpressionCache.AliasType<T, A, U, FunctionExpressionCache.VariableType<T, A, U>> alias(String prefix, String suffix) {
         return new FunctionExpressionCache.AliasType<T, A, U, FunctionExpressionCache.VariableType<T, A, U>>(prefix, suffix, this);
      }
   }
}
