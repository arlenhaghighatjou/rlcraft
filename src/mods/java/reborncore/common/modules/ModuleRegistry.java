package reborncore.common.modules;

import java.util.HashMap;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModuleRegistry {
   public HashMap<Module, String> modules = new HashMap();

   public ModuleRegistry() {
   }

   public void addModules() {
      this.checkRequirements();
   }

   protected void checkRequirements() {
      for(Module i : this.modules.keySet()) {
         this.hasRequirements(i);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      this.addModules();

      for(Module i : this.modules.keySet()) {
         if (i.isActive) {
            i.preInit(event);
         }
      }

   }

   public void init(FMLInitializationEvent event) {
      for(Module i : this.modules.keySet()) {
         if (i.isActive) {
            i.init(event);
         }
      }

   }

   public void postInit(FMLPostInitializationEvent event) {
      for(Module i : this.modules.keySet()) {
         if (i.isActive) {
            i.postInit(event);
         }
      }

   }

   public boolean hasRequirements(Module module) {
      String[] reqMods = module.getModsRequired();
      String[] reqModules = module.getModulesRequired();
      if (reqMods != null) {
         for(String i : reqMods) {
            if (!Loader.isModLoaded(i)) {
               module.isActive = false;
               return false;
            }
         }
      }

      if (reqModules != null) {
         for(String i : reqModules) {
            if (!this.modules.values().contains(i)) {
               module.isActive = false;
               return false;
            }
         }
      }

      return true;
   }
}
