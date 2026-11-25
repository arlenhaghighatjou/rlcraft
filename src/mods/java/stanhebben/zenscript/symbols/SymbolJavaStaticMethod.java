package stanhebben.zenscript.symbols;

import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.expression.partial.PartialStaticMethod;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.util.ZenPosition;

public class SymbolJavaStaticMethod implements IZenSymbol {
   private final IJavaMethod method;

   public SymbolJavaStaticMethod(IJavaMethod method) {
      this.method = method;
   }

   public IPartialExpression instance(ZenPosition position) {
      return new PartialStaticMethod(position, this.method);
   }

   public String toString() {
      return "SymbolJavaStaticMethod: " + this.method.toString();
   }
}
