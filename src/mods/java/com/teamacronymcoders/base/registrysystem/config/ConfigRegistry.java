package com.teamacronymcoders.base.registrysystem.config;

import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.registrysystem.Registry;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

public class ConfigRegistry extends Registry<ConfigEntry> {
   private List<IConfigListener> listeners = new ArrayList();
   private File tacFolder;
   private File modFolder;
   private boolean useModFolder;
   private Map<String, Configuration> configurationFiles;

   public ConfigRegistry(IBaseMod mod, File suggestConfigFile, boolean useModFolder) {
      super("CONFIG", mod);
      this.useModFolder = useModFolder;
      this.configurationFiles = new HashMap();
      if (suggestConfigFile.isDirectory()) {
         String configFileName = this.mod.getID();
         this.tacFolder = new File(suggestConfigFile, "acronym");
         boolean folderExists = this.getTacFolder().exists();
         if (!folderExists) {
            folderExists = this.getTacFolder().mkdir();
         }

         if (folderExists && useModFolder) {
            this.modFolder = new File(this.getTacFolder(), this.mod.getConfigFolderName());
            if (!this.modFolder.exists()) {
               folderExists = this.modFolder.mkdir();
            }

            configFileName = "General";
         }

         if (folderExists) {
            this.addNewConfigFile(configFileName);
         } else {
            this.mod.getLogger().fatal("FAILED TO CREATE REQUIRED FOLDERS FOR CONFIG");
         }
      } else {
         this.configurationFiles.put(mod.getID(), new Configuration(suggestConfigFile));
      }

   }

   public void alertTheListeners(String name, ConfigEntry configEntry) {
      for(IConfigListener configListener : this.listeners) {
         configListener.onConfigChange(name, configEntry);
      }

   }

   public boolean addNewConfigFile(String fileName) {
      File parentFile = this.useModFolder ? this.modFolder : this.getTacFolder();
      return this.addNewConfigFile(parentFile, fileName);
   }

   public boolean addNewConfigFile(File parent, String fileName) {
      File newConfig = new File(parent, fileName + ".cfg");
      boolean exists = newConfig.exists();
      if (!exists) {
         try {
            exists = newConfig.createNewFile();
         } catch (IOException var6) {
            this.mod.getLogger().getLogger().error("Failed to Create Config File: " + fileName, var6);
         }
      }

      if (exists) {
         this.configurationFiles.put(fileName, new Configuration(newConfig));
      } else {
         this.mod.getLogger().fatal(fileName + " configuration file was not created.");
      }

      return exists;
   }

   public void addCategoryComment(String name, String comment) {
      this.addCategoryComment(name, comment, this.mod.getID());
   }

   public void addCategoryComment(String name, String comment, String configName) {
      ((Configuration)this.configurationFiles.get(configName)).addCustomCategoryComment(name, comment);
   }

   public void addEntry(ConfigEntry entry) {
      this.addEntry(entry.getPropertyName(), entry);
   }

   public void addEntry(String name, ConfigEntry entry) {
      String configName = this.useModFolder ? "General" : this.mod.getID();
      this.addEntry(name, entry, configName);
   }

   public void addEntry(String name, ConfigEntry entry, String configName) {
      this.entries.put(new ResourceLocation(this.mod.getName(), name), entry);
      entry.saveProperty((Configuration)this.configurationFiles.get(configName));
      ((Configuration)this.configurationFiles.get(configName)).save();
   }

   public void updateEntry(String name, String value) {
      ConfigEntry configEntry = (ConfigEntry)this.entries.get(new ResourceLocation(this.mod.getName(), name));
      if (configEntry != null) {
         configEntry.setValue(value);
         this.alertTheListeners(name, configEntry);
      } else {
         this.mod.getLogger().error("Config Entry for " + name + " not found");
      }

   }

   public ConfigEntry getEntry(String name) {
      return (ConfigEntry)this.entries.get(new ResourceLocation(this.mod.getName(), name));
   }

   public boolean getBoolean(String name, boolean defaultValue) {
      boolean returnValue = defaultValue;
      ConfigEntry configEntry = this.getEntry(name);
      if (configEntry != null) {
         returnValue = configEntry.getBoolean(defaultValue);
      }

      return returnValue;
   }

   public int getInt(String name, int defaultValue) {
      int returnValue = defaultValue;
      ConfigEntry configEntry = this.getEntry(name);
      if (configEntry != null) {
         returnValue = configEntry.getInt(defaultValue);
      }

      return returnValue;
   }

   public double getDouble(String name, double defaultValue) {
      double returnValue = defaultValue;
      ConfigEntry configEntry = this.getEntry(name);
      if (configEntry != null) {
         returnValue = configEntry.getDouble(defaultValue);
      }

      return returnValue;
   }

   public String getString(String name, String defaultValue) {
      String returnValue = defaultValue;
      ConfigEntry configEntry = this.getEntry(name);
      if (configEntry != null) {
         returnValue = configEntry.getString();
      }

      return returnValue;
   }

   public boolean requiresBeforeRegister() {
      return false;
   }

   public void register(ResourceLocation name, ConfigEntry entry) {
      this.addEntry(name.getResourcePath(), entry);
   }

   public void register(String name, ConfigEntry entry) {
      this.addEntry(name, entry);
   }

   public void addListener(IConfigListener listener) {
      this.listeners.add(listener);
   }

   public File getConfigFolder() {
      return this.useModFolder ? this.modFolder : this.getTacFolder();
   }

   public File getTacFolder() {
      return this.tacFolder;
   }
}
