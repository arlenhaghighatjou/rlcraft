package c4.corpsecomplex.common.modules.compatibility.toolbelt;

import c4.corpsecomplex.common.Module;
import c4.corpsecomplex.common.Submodule;

public class ToolbeltModule extends Submodule {
   static boolean keepToolbelt;

   public ToolbeltModule(Module parentModule) {
      super(parentModule, (String)null);
   }

   public void loadModuleConfig() {
      keepToolbelt = this.getBool("Keep Toolbelt", false, "Set to true to keep Toolbelt on death", false);
   }
}
