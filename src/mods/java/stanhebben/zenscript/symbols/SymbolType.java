package stanhebben.zenscript.symbols;

import java.util.List;
import stanhebben.zenscript.dump.IDumpable;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.expression.partial.PartialType;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class SymbolType implements IZenSymbol {
   private final ZenType type;

   public SymbolType(ZenType type) {
      this.type = type;
   }

   public IPartialExpression instance(ZenPosition position) {
      return new PartialType(position, this.type);
   }

   public String toString() {
      return "SymbolType: " + this.type.toString();
   }

   public ZenType getType() {
      return this.type;
   }

   public List<? extends IDumpable> asDumpedObject() {
      return this.type.asDumpedObject();
   }
}
