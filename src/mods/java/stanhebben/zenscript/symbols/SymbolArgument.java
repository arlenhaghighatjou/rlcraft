package stanhebben.zenscript.symbols;

import stanhebben.zenscript.expression.ExpressionArgument;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class SymbolArgument implements IZenSymbol {
   private final int id;
   private final ZenType type;

   public SymbolArgument(int id, ZenType type) {
      this.id = id;
      this.type = type;
   }

   public IPartialExpression instance(ZenPosition position) {
      return new ExpressionArgument(position, this.id, this.type);
   }
}
