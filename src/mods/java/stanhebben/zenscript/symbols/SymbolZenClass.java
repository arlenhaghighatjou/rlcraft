package stanhebben.zenscript.symbols;

import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.expression.partial.PartialZSClass;
import stanhebben.zenscript.type.ZenTypeZenClass;
import stanhebben.zenscript.util.ZenPosition;

public class SymbolZenClass implements IZenSymbol {
   private final ZenTypeZenClass type;

   public SymbolZenClass(ZenTypeZenClass type) {
      this.type = type;
   }

   public IPartialExpression instance(ZenPosition position) {
      return new PartialZSClass(this.type);
   }
}
