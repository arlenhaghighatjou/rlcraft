package ladysnake.spawnercontrol.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import ladysnake.spawnercontrol.SpawnerControl;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class CustomSpawnersConfig {
   private static final String CUSTOM_CONFIG_FOLDER = "spawnercontrol/custom_spawners";
   private static final Pattern VALIDATION_PATTERN = Pattern.compile("^[\\w\\s\\d]+$");
   public static VFile2 configDir;
   private static Configuration mainConfiguration;
   private static final Map<ResourceLocation, SpawnerConfigHolder> customSpawnerConfigs = new HashMap();

   public CustomSpawnersConfig() {
   }

   static Configuration getMainConfiguration() {
      if (mainConfiguration == null) {
         mainConfiguration = ConfigManager.getConfiguration("spawnercontrol", "spawnercontrol/spawnercontrol");
      }

      return mainConfiguration;
   }

   public static void initCustomConfig() {
      Configuration baseConfig = getMainConfiguration();
      ConfigCategory mainCategory = baseConfig.getCategory("general");
      Property customSpawnersProp = mainCategory.get("customSpawners");
      customSpawnersProp.setValidationPattern(VALIDATION_PATTERN);

      for(String name : customSpawnersProp.getStringList()) {
         if (VALIDATION_PATTERN.matcher(name).matches()) {
            generateConfig(baseConfig.getCategory("general.vanillaSpawnerConfig"), name);
         } else {
            SpawnerControl.LOGGER.warn("Invalid custom spawner name {}, skipping", name);
         }
      }

   }

   public static Collection<SpawnerConfigHolder> getCustomSpawnerConfigs() {
      return customSpawnerConfigs.values();
   }

   private static void generateConfig(ConfigCategory baseCategory, String name) {
      Configuration config = new Configuration(new VFile2(configDir, "spawnercontrol/custom_spawners/" + name + ".cfg"));
      ConfigCategory category = config.getCategory(name);
      for(Map.Entry<String, Property> entry : baseCategory.getValues().entrySet()) {
         if (!category.containsKey(entry.getKey())) {
            category.put(entry.getKey(), clone(entry.getValue()));
         }
      }

      SpawnerConfigHolder holder = new SpawnerConfigHolder(config, name);
      holder.sync();
      customSpawnerConfigs.put(holder.getRegistryName(), holder);
   }

   private static Property clone(Property prop) {
      Property cln;
      if (prop.isList()) {
         cln = new Property(prop.getName(), prop.getDefaults(), prop.getType(), prop.getLanguageKey());
      } else {
         cln = new Property(prop.getName(), prop.getDefault(), prop.getType(), prop.getValidValues(), prop.getLanguageKey());
      }

      cln.setValue(prop.getString());
      cln.setValues(prop.getStringList());
      cln.minValue = prop.getMinValue();
      cln.maxValue = prop.getMaxValue();
      cln.setComment(prop.getComment());
      return cln;
   }
}
