package c4.corpsecomplex.common;

import java.util.function.Consumer;
import net.minecraftforge.common.config.ConfigCategory;

public abstract class Submodule extends Module {
   protected Module parentModule;

   public abstract void loadModuleConfig();

   public Submodule(Module parentModule, String childCategory) {
      this.parentModule = parentModule;
      if (childCategory != null) {
         this.configCategory = new ConfigCategory(childCategory, parentModule.configCategory);
      } else {
         this.configCategory = parentModule.configCategory;
      }

   }

   public void setEnabled() {
      this.enabled = this.parentModule.enabled;
   }

   public boolean hasEvents() {
      return false;
   }

   public void loadSubmodules() {
   }

   public void initPropOrder() {
   }

   protected void addSubmodule(Class<? extends Submodule> submodule) {
   }

   protected void addSubmodule(String modid, Class<? extends Submodule> submodule) {
   }

   public void forEachSubmodule(Consumer<Submodule> submodule) {
   }
}
