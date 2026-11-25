package c4.corpsecomplex.common.modules.compatibility.camping;

import c4.corpsecomplex.common.Module;
import c4.corpsecomplex.common.Submodule;

public class CampingModule extends Submodule {
   static boolean keepCamping;

   public CampingModule(Module parentModule) {
      super(parentModule, (String)null);
   }

   public void loadModuleConfig() {
      keepCamping = this.getBool("Keep Camping", false, "Set to true to keep Camping inventory on death", false);
   }
}
