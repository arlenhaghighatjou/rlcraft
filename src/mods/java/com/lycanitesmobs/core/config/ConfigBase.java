package com.lycanitesmobs.core.config;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.ItemDrop;
import com.lycanitesmobs.core.info.ModInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigBase {
   public static Map<String, ConfigBase> configs = new HashMap();
   public Configuration config;
   public ModInfo group;
   public String configName;
   public String fileName;
   public List<IConfigListener> updateListeners = new ArrayList();

   public static void versionCheck(String minVersion, String currentVersion) {
      ConfigBase versionConfig = getConfig(LycanitesMobs.modInfo, "version");
      String configVersion = versionConfig.getString("Version", "Config Version", "0.0.0.0", "The version that this config was last read from, manually update this if you do not want your config to be cleared, although it is recommended not to unless you are aware of the changes.");
      String[] minVersions = minVersion.split("\\.");
      String[] configVersions = configVersion.split("\\.");
      if (configVersions.length != 4) {
         configVersions = "0.0.0.0".split("\\.");
      }

      boolean oldVersion = false;

      for(int i = 0; i < 4; ++i) {
         int minVerNum = 0;
         int currentVerNum = 0;

         try {
            minVerNum = Integer.parseInt(minVersions[i].replaceAll("[^\\d.]", ""));
            currentVerNum = Integer.parseInt(configVersions[i].replaceAll("[^\\d.]", ""));
         } catch (NumberFormatException var12) {
            ;
         }

         if (currentVerNum < minVerNum) {
            oldVersion = true;
            break;
         }

         if (currentVerNum > minVerNum) {
            break;
         }
      }

      if (oldVersion) {
         String configDirPath = "config/" + "lycanitesmobs";
         VFile2 configDir = new VFile2(configDirPath);
         LycanitesMobs.logWarning("", "[Config] The current configs are too old, clearing all configs now...");
         List<VFile2> configFiles = configDir.listFiles(true);

         for(int i = 0, l = configFiles.size(); i < l; ++i) {
            configFiles.get(i).delete();
         }
      }

      currentVersion = currentVersion.replace(" ", "");
      currentVersion = currentVersion.split("-")[0];
      if (!configVersion.equals(currentVersion)) {
         versionConfig.setString("Version", "Config Version", currentVersion);
      }

   }

   public static void registerConfig(ConfigBase config) {
      if (config != null) {
         configs.put(config.fileName, config);
      }
   }

   public static ConfigBase getConfig(ModInfo group, String configName) {
      String configFileName = configName.toLowerCase();
      if (!"lycanitesmobs".equalsIgnoreCase(group.modid)) {
         configFileName = group.modid + "-" + configFileName;
      }

      if (!configs.containsKey(configFileName)) {
         registerConfig(new ConfigBase(group, configName, configFileName));
      }

      return (ConfigBase)configs.get(configFileName);
   }

   public ConfigBase(ModInfo group, String name, String filename) {
      this.group = group;
      this.configName = name;
      this.fileName = filename;
      this.init();
   }

   public void init() {
      String configDirPath = "config/" + "lycanitesmobs";
      VFile2 configFile = new VFile2(configDirPath + "/" + this.fileName + ".cfg");
      LycanitesMobs.logInfo("", "Config " + this.fileName + " created successfully.");

      this.config = new Configuration(configFile);
      this.config.load();
   }

   public void update() {
      this.config.save();

      for(IConfigListener updateListener : this.updateListeners) {
         if (updateListener != null) {
            updateListener.onConfigUpdate(this);
         }
      }

   }

   public void addListener(IConfigListener listener) {
      if (!this.updateListeners.contains(listener)) {
         this.updateListeners.add(listener);
      }

   }

   public void setCategoryComment(String category, String comment) {
      category = category.toLowerCase();
      this.config.addCustomCategoryComment(category, comment);
   }

   public boolean setCategoryReplacedComment(String category, String newCategory) {
      category = category.toLowerCase();
      if (this.config.hasCategory(category)) {
         this.config.addCustomCategoryComment(category, "This category has been replaced with the " + newCategory + " category, your settings have been copied to the new category for you and this category can now safely be deleted.");
         return true;
      } else {
         return false;
      }
   }

   public boolean getBool(String category, String key) {
      return this.getBool(category, key, false);
   }

   public boolean getBool(String category, String key, boolean defaultValue) {
      return this.getBool(category, key, defaultValue, (String)null);
   }

   public boolean getBool(String category, String key, boolean defaultValue, String comment) {
      category = category.toLowerCase();
      boolean newEntry = !this.config.getCategory(category).containsKey(key);
      Property property = this.config.get(category, key, defaultValue);
      if (comment != null) {
         property.setComment(comment);
      }

      if (newEntry) {
         this.config.save();
      }

      return property.getBoolean(defaultValue);
   }

   public int getInt(String category, String key) {
      return this.getInt(category, key, 0);
   }

   public int getInt(String category, String key, int defaultValue) {
      return this.getInt(category, key, defaultValue, (String)null);
   }

   public int getInt(String category, String key, int defaultValue, String comment) {
      category = category.toLowerCase();
      boolean newEntry = !this.config.getCategory(category).containsKey(key);
      Property property = this.config.get(category, key, defaultValue);
      if (comment != null) {
         property.setComment(comment);
      }

      if (newEntry) {
         this.config.save();
      }

      return property.getInt(defaultValue);
   }

   public double getDouble(String category, String key) {
      return this.getDouble(category, key, 0.0D);
   }

   public double getDouble(String category, String key, double defaultValue) {
      return this.getDouble(category, key, defaultValue, (String)null);
   }

   public double getDouble(String category, String key, double defaultValue, String comment) {
      category = category.toLowerCase();
      boolean newEntry = !this.config.getCategory(category).containsKey(key);
      Property property = this.config.get(category, key, defaultValue);
      if (comment != null) {
         property.setComment(comment);
      }

      if (newEntry) {
         this.config.save();
      }

      return property.getDouble(defaultValue);
   }

   public String getString(String category, String key) {
      return this.getString(category, key, "");
   }

   public String getString(String category, String key, String defaultValue) {
      return this.getString(category, key, defaultValue, (String)null);
   }

   public String getString(String category, String key, String defaultValue, String comment) {
      category = category.toLowerCase();
      boolean newEntry = !this.config.getCategory(category).containsKey(key);
      Property property = this.config.get(category, key, defaultValue);
      if (comment != null) {
         property.setComment(comment);
      }

      if (newEntry) {
         this.config.save();
      }

      return property.getString();
   }

   public ItemDrop getItemDrop(String category, String key) {
      return this.getItemDrop(category, key, (ItemDrop)null);
   }

   public ItemDrop getItemDrop(String category, String key, ItemDrop defaultValue) {
      return this.getItemDrop(category, key, defaultValue, (String)null);
   }

   public ItemDrop getItemDrop(String category, String key, ItemDrop defaultValue, String comment) {
      String defaultValueString = "";
      if (defaultValue != null) {
         defaultValueString = defaultValue.toConfigString();
      }

      String itemDropString = this.getString(category, key, defaultValueString, comment).replace(" ", "");
      return ItemDrop.createFromConfigString(itemDropString);
   }

   public boolean[] getBoolList(String category, String key) {
      return this.getBoolList(category, key, new boolean[]{false});
   }

   public boolean[] getBoolList(String category, String key, boolean[] defaultValue) {
      return this.getBoolList(category, key, defaultValue, (String)null);
   }

   public boolean[] getBoolList(String category, String key, boolean[] defaultValue, String comment) {
      category = category.toLowerCase();
      boolean newEntry = !this.config.getCategory(category).containsKey(key);
      Property property = this.config.get(category, key, defaultValue);
      if (comment != null) {
         property.setComment(comment);
      }

      if (newEntry) {
         this.config.save();
      }

      return property.getBooleanList();
   }

   public int[] getIntList(String category, String key) {
      return this.getIntList(category, key, new int[]{0});
   }

   public int[] getIntList(String category, String key, int[] defaultValue) {
      return this.getIntList(category, key, defaultValue, (String)null);
   }

   public int[] getIntList(String category, String key, int[] defaultValue, String comment) {
      category = category.toLowerCase();
      boolean newEntry = !this.config.getCategory(category).containsKey(key);
      Property property = this.config.get(category, key, defaultValue);
      if (comment != null) {
         property.setComment(comment);
      }

      if (newEntry) {
         this.config.save();
      }

      return property.getIntList();
   }

   public double[] getDoubleList(String category, String key) {
      return this.getDoubleList(category, key, new double[]{0.0D});
   }

   public double[] getDoubleList(String category, String key, double[] defaultValue) {
      return this.getDoubleList(category, key, defaultValue, (String)null);
   }

   public double[] getDoubleList(String category, String key, double[] defaultValue, String comment) {
      category = category.toLowerCase();
      boolean newEntry = !this.config.getCategory(category).containsKey(key);
      Property property = this.config.get(category, key, defaultValue);
      if (comment != null) {
         property.setComment(comment);
      }

      if (newEntry) {
         this.config.save();
      }

      return property.getDoubleList();
   }

   public String[] getStringList(String category, String key) {
      return this.getStringList(category, key, new String[]{""});
   }

   public String[] getStringList(String category, String key, String[] defaultValue) {
      return this.getStringList(category, key, defaultValue, (String)null);
   }

   public String[] getStringList(String category, String key, String[] defaultValue, String comment) {
      category = category.toLowerCase();
      boolean newEntry = !this.config.getCategory(category).containsKey(key);
      Property property = this.config.get(category, key, defaultValue);
      if (comment != null) {
         property.setComment(comment);
      }

      if (newEntry) {
         this.config.save();
      }

      return property.getStringList();
   }

   public void setBool(String category, String key, boolean value) {
      this.setBool(category, key, value, (String)null);
   }

   public void setBool(String category, String key, boolean value, String comment) {
      category = category.toLowerCase();
      Property property = this.config.get(category, key, value);
      if (comment != null) {
         property.setComment(comment);
      }

      property.set(value);
      this.update();
   }

   public void setInt(String category, String key, int value) {
      this.setInt(category, key, value, (String)null);
   }

   public void setInt(String category, String key, int value, String comment) {
      category = category.toLowerCase();
      Property property = this.config.get(category, key, value);
      if (comment != null) {
         property.setComment(comment);
      }

      property.set(value);
      this.update();
   }

   public void setDouble(String category, String key, double value) {
      this.setDouble(category, key, value, (String)null);
   }

   public void setDouble(String category, String key, double value, String comment) {
      category = category.toLowerCase();
      Property property = this.config.get(category, key, value);
      if (comment != null) {
         property.setComment(comment);
      }

      property.set(value);
      this.update();
   }

   public void setString(String category, String key, String value) {
      this.setString(category, key, value, (String)null);
   }

   public void setString(String category, String key, String value, String comment) {
      category = category.toLowerCase();
      Property property = this.config.get(category, key, value);
      if (comment != null) {
         property.setComment(comment);
      }

      property.set(value);
      this.update();
   }

   public void setList(String category, String key, Object[] objValues) {
      this.setList(category, key, objValues, (String)null);
   }

   public void setList(String category, String key, Object[] objValues, String comment) {
      category = category.toLowerCase();
      List<String> valuesList = new ArrayList();

      for(Object objValue : objValues) {
         valuesList.add(objValue.toString());
      }

      String[] values = (String[])valuesList.toArray(new String[valuesList.size()]);
      Property property = this.config.get(category, key, values);
      if (comment != null) {
         property.setComment(comment);
      }

      property.set(values);
      this.update();
   }
}
