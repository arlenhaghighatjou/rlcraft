package ladysnake.spawnercontrol.config;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import ladysnake.spawnercontrol.SpawnerControl;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CustomSpawnersConfig {
   private static final String CUSTOM_CONFIG_FOLDER = "spawnercontrol/custom_spawners";
   private static final Pattern VALIDATION_PATTERN = Pattern.compile("^[\\w\\s\\d]+$");
   public static File configDir;
   static MethodHandle configManager$sync;
   private static Configuration mainConfiguration;
   private static final Map<ResourceLocation, SpawnerConfigHolder> customSpawnerConfigs = new HashMap();

   public CustomSpawnersConfig() {
   }

   static Configuration getMainConfiguration() {
      if (mainConfiguration == null) {
         try {
            Method getConfiguration = ConfigManager.class.getDeclaredMethod("getConfiguration", String.class, String.class);
            getConfiguration.setAccessible(true);
            mainConfiguration = (Configuration)getConfiguration.invoke((Object)null, "spawnercontrol", "spawnercontrol/spawnercontrol");
         } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException var1) {
            SpawnerControl.LOGGER.error("Error while attempting to access spawner control's configuration", var1);
            return new Configuration(new File(configDir, "spawnercontrol/spawnercontrol.cfg"));
         }
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
      Configuration config = new Configuration(new File(configDir, "spawnercontrol/custom_spawners/" + name + ".cfg"));
      ConfigCategory category = config.getCategory(name);
      baseCategory.getValues().forEach((key, value) -> {
         if (!category.containsKey(key)) {
            category.put(key, clone(value));
         }

      });
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
      ReflectionHelper.setPrivateValue(Property.class, cln, prop.getMinValue(), new String[]{"minValue"});
      ReflectionHelper.setPrivateValue(Property.class, cln, prop.getMaxValue(), new String[]{"maxValue"});
      cln.setComment(prop.getComment());
      return cln;
   }

   static {
      try {
         Method m = ConfigManager.class.getDeclaredMethod("sync", Configuration.class, Class.class, String.class, String.class, Boolean.TYPE, Object.class);
         m.setAccessible(true);
         configManager$sync = MethodHandles.lookup().unreflect(m);
      } catch (NoSuchMethodException | IllegalAccessException var1) {
         SpawnerControl.LOGGER.error("Error while reflecting ConfigManager#sync", var1);
      }

   }
}
