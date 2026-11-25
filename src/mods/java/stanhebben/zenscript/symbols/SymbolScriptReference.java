package stanhebben.zenscript.symbols;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.expression.partial.PartialScriptReference;
import stanhebben.zenscript.util.ZenPosition;

public class SymbolScriptReference implements IZenSymbol {
   private final PartialScriptReference instance;

   private SymbolScriptReference() {
      this.instance = new PartialScriptReference();
   }

   public SymbolScriptReference(PartialScriptReference instance) {
      this.instance = instance;
   }

   public PartialScriptReference instance(ZenPosition position) {
      return this.instance;
   }

   public static PartialScriptReference getOrCreateReference(IEnvironmentGlobal environmentGlobal) {
      IPartialExpression reference = environmentGlobal.getValue("scripts", (ZenPosition)null);
      if (reference == null) {
         environmentGlobal.putValue("scripts", new SymbolScriptReference(), (ZenPosition)null);
         reference = environmentGlobal.getValue("scripts", (ZenPosition)null);
      }

      return (PartialScriptReference)reference;
   }
}
