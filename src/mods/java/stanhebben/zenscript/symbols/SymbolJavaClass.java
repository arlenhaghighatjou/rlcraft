package stanhebben.zenscript.symbols;

import java.lang.reflect.Type;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.expression.partial.PartialJavaClass;
import stanhebben.zenscript.util.ZenPosition;

public class SymbolJavaClass implements IZenSymbol {
   private final Type cls;

   public SymbolJavaClass(Type cls) {
      this.cls = cls;
   }

   public IPartialExpression instance(ZenPosition position) {
      return new PartialJavaClass(position, this.cls);
   }

   public String toString() {
      return "SymbolJavaClass: " + this.cls.toString();
   }
}
