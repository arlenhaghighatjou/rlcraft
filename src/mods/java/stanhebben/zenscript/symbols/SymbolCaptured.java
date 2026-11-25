package stanhebben.zenscript.symbols;

import java.util.Locale;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class SymbolCaptured implements IZenSymbol {
   private final String fieldName;
   private final String lambdaClassName;
   private final Expression evaluated;

   public SymbolCaptured(Expression original, String fieldName, String clsName) {
      this.evaluated = original;
      this.fieldName = fieldName;
      this.lambdaClassName = clsName;
   }

   public ZenType getType() {
      return this.evaluated.getType();
   }

   public IPartialExpression instance(ZenPosition position) {
      return new Expression(position) {
         public void compile(boolean result, IEnvironmentMethod environment) {
            if (result) {
               MethodOutput output = environment.getOutput();
               if (SymbolCaptured.this.lambdaClassName != null && SymbolCaptured.this.fieldName != null && SymbolCaptured.this.evaluated != null) {
                  output.loadObject(0);
                  output.getField(SymbolCaptured.this.lambdaClassName, SymbolCaptured.this.fieldName, this.getType().toASMType().getDescriptor());
               } else {
                  throw new IllegalStateException(String.format(Locale.ENGLISH, "Captured variable with name %s in class %s and evaluated obj %s has at least one null info", SymbolCaptured.this.fieldName, SymbolCaptured.this.lambdaClassName, SymbolCaptured.this.evaluated));
               }
            }
         }

         public ZenType getType() {
            return SymbolCaptured.this.getType();
         }
      };
   }

   public Expression getEvaluated() {
      return this.evaluated;
   }

   public String getFieldName() {
      return this.fieldName;
   }
}
