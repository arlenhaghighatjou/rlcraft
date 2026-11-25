package reborncore.common.modules;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Module {
   private String moduleID;
   private String[] modsRequired;
   private String[] modulesRequired;
   public boolean isActive = true;

   public Module(String id) {
      this.moduleID = id;
   }

   public void preInit(FMLPreInitializationEvent event) {
   }

   public void init(FMLInitializationEvent event) {
   }

   public void postInit(FMLPostInitializationEvent event) {
   }

   public String getModuleID() {
      return this.moduleID;
   }

   public String[] getModsRequired() {
      return this.modsRequired;
   }

   public void setModsRequired(String... modIds) {
      this.modsRequired = modIds;
   }

   public String[] getModulesRequired() {
      return this.modulesRequired;
   }

   public void setModulesRequired(String... moduleIDs) {
      this.modulesRequired = moduleIDs;
   }
}
