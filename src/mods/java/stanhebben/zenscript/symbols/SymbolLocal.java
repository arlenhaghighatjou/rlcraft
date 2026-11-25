package stanhebben.zenscript.symbols;

import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.expression.partial.PartialLocal;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class SymbolLocal implements IZenSymbol {
   private final ZenType type;
   private final boolean isFinal;

   public SymbolLocal(ZenType type, boolean isFinal) {
      this.type = type;
      this.isFinal = isFinal;
   }

   public ZenType getType() {
      return this.type;
   }

   public boolean isFinal() {
      return this.isFinal;
   }

   public IPartialExpression instance(ZenPosition position) {
      return new PartialLocal(position, this);
   }
}
