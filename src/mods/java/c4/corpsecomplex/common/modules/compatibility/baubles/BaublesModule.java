package c4.corpsecomplex.common.modules.compatibility.baubles;

import c4.corpsecomplex.common.Module;
import c4.corpsecomplex.common.Submodule;

public class BaublesModule extends Submodule {
   static boolean keepBaubles;

   public BaublesModule(Module parentModule) {
      super(parentModule, (String)null);
   }

   public void loadModuleConfig() {
      keepBaubles = this.getBool("Keep Baubles", false, "Set to true to keep Baubles on death", false);
   }
}
