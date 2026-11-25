package c4.corpsecomplex.common.modules.compatibility.galacticraftcore;

import c4.corpsecomplex.common.Module;
import c4.corpsecomplex.common.Submodule;

public class GalacticraftModule extends Submodule {
   static boolean keepGalacticraft;

   public GalacticraftModule(Module parentModule) {
      super(parentModule, (String)null);
   }

   public void loadModuleConfig() {
      keepGalacticraft = this.getBool("Keep Galacticraft", false, "Set to true to keep Galacticraft inventory on death", false);
   }
}
