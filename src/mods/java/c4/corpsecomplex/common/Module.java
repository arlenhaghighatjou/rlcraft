package c4.corpsecomplex.common;

import c4.corpsecomplex.CorpseComplex;
import c4.corpsecomplex.common.helpers.ConfigHelper;
import c4.corpsecomplex.common.helpers.ModuleHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.fml.common.Loader;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public abstract class Module {
   public boolean enabled;
   public boolean prevEnabled;
   protected ConfigCategory configCategory;
   protected List<String> propOrder;
   protected ArrayList<Class<? extends Submodule>> submoduleClasses;
   private Map<Class<? extends Submodule>, Submodule> subInstances = new HashMap();

   public abstract void loadModuleConfig();

   public abstract void initPropOrder();

   public abstract void setEnabled();

   public Module() {
      this.prevEnabled = false;
   }

   public Module(String category, String description) {
      this.configCategory = new ConfigCategory(category);
      this.configCategory.setComment(description);
      this.prevEnabled = false;
   }

   public void setPropOrder() {
      this.initPropOrder();
      if (this.propOrder != null) {
         ModuleHelper.cfg.getCategory(this.configCategory.getQualifiedName()).setPropertyOrder(this.propOrder);
      }

      this.forEachSubmodule(Module::setPropOrder);
   }

   public boolean hasEvents() {
      return true;
   }

   public void loadSubmodules() {
      if (this.submoduleClasses != null && !this.submoduleClasses.isEmpty()) {
         this.submoduleClasses.forEach((submodule) -> {
            try {
               this.subInstances.put(submodule, submodule.getDeclaredConstructor(Module.class).newInstance(this));
            } catch (Exception var3) {
               CorpseComplex.logger.log(Level.ERROR, "Failed to initialize submodule " + submodule, var3);
            }

         });
      }
   }

   protected void addSubmodule(Class<? extends Submodule> submodule) {
      if (!this.submoduleClasses.contains(submodule)) {
         this.submoduleClasses.add(submodule);
      }

   }

   protected void addSubmodule(String modid, Class<? extends Submodule> submodule) {
      if (Loader.isModLoaded(modid) && !this.submoduleClasses.contains(submodule)) {
         this.submoduleClasses.add(submodule);
      }

   }

   public void forEachSubmodule(Consumer<Submodule> submodule) {
      this.subInstances.values().forEach(submodule);
   }

   protected void setCategoryComment() {
      ModuleHelper.cfg.addCustomCategoryComment(this.configCategory.getQualifiedName(), this.configCategory.getComment());
   }

   protected int getInt(String name, int defaultInt, int min, int max, String comment, boolean requiresRestart) {
      return ConfigHelper.getInt(name, this.configCategory.getQualifiedName(), defaultInt, min, max, comment, requiresRestart);
   }

   protected double getDouble(String name, double defaultDouble, float min, float max, String comment, boolean requiresRestart) {
      return ConfigHelper.getDouble(name, this.configCategory.getQualifiedName(), defaultDouble, min, max, comment, requiresRestart);
   }

   protected boolean getBool(String name, boolean defaultBool, String comment, boolean requiresRestart) {
      return ConfigHelper.getBool(name, this.configCategory.getQualifiedName(), defaultBool, comment, requiresRestart);
   }

   protected String getString(String name, String defaultString, String comment, String[] validValues, boolean requiresRestart) {
      return ConfigHelper.getString(name, this.configCategory.getQualifiedName(), defaultString, comment, validValues, requiresRestart);
   }

   protected String[] getStringList(String name, String[] defaultStringList, String comment, boolean requiresRestart) {
      return ConfigHelper.getStringList(name, this.configCategory.getQualifiedName(), defaultStringList, comment, requiresRestart);
   }
}
