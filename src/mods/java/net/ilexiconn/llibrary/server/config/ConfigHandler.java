package net.ilexiconn.llibrary.server.config;

import com.google.common.collect.SetMultimap;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.ilexiconn.llibrary.LLibrary;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;

/** @deprecated */
@Deprecated
public enum ConfigHandler {
   INSTANCE;

   private Map<Class<?>, Class<EntryProperty>> entryPropertyClasses = new HashMap();
   private Map<String, ConfigHandler.ConfigContainer> configContainers = new HashMap();

   private ConfigHandler() {
   }

   public <T> void registerEntryPropertyClass(Class<T> type, Class<EntryProperty> entryAdapter) {
      this.entryPropertyClasses.put(type, entryAdapter);
   }

   public boolean hasConfigForID(String modid) {
      return this.configContainers.containsKey(modid);
   }

   public Configuration getConfigForID(String modid) {
      ConfigHandler.ConfigContainer configContainer = (ConfigHandler.ConfigContainer)this.configContainers.get(modid);
      return configContainer != null ? configContainer.forgeConfiguration : null;
   }

   /** @deprecated */
   @Deprecated
   public Object getObjectForID(String modid) {
      ConfigHandler.ConfigContainer configContainer = (ConfigHandler.ConfigContainer)this.configContainers.get(modid);
      return configContainer != null ? configContainer.wrappedConfig : null;
   }

   public <T> T getObjectForID(String modid, Class<T> type) {
      ConfigHandler.ConfigContainer configContainer = (ConfigHandler.ConfigContainer)this.configContainers.get(modid);
      return (T)(configContainer != null ? type.cast(configContainer.wrappedConfig) : null);
   }

   public void saveConfigForID(String modid) {
      ConfigHandler.ConfigContainer configContainer = (ConfigHandler.ConfigContainer)this.configContainers.get(modid);
      if (configContainer != null) {
         configContainer.save();
      }

   }

   public void loadConfigForID(String modid) {
      ConfigHandler.ConfigContainer configContainer = (ConfigHandler.ConfigContainer)this.configContainers.get(modid);
      if (configContainer != null) {
         configContainer.load();
      }

   }

   public void injectConfig(ModContainer mod, ASMDataTable data) {
      SetMultimap<String, ASMData> annotations = data.getAnnotationsFor(mod);
      if (annotations != null) {
         Set<ASMData> targetList = annotations.get(Config.class.getName());
         ClassLoader classLoader = Loader.instance().getModClassLoader();

         for(ASMData target : targetList) {
            try {
               Class<?> targetClass = Class.forName(target.getClassName(), true, classLoader);
               Field field = targetClass.getDeclaredField(target.getObjectName());
               field.setAccessible(true);
               Class<?> configClass = field.getType();
               File configFile = new File(".", "config" + File.separator + mod.getModId() + ".cfg");
               field.set((Object)null, this.registerConfig(mod, configFile, configClass.newInstance()));
            } catch (Exception var12) {
               LLibrary.LOGGER.fatal("Failed to inject config for mod container {}", var12, mod);
            }
         }
      }

   }

   private <T> T registerConfig(ModContainer mod, File file, T config) {
      ConfigHandler.ConfigContainer configContainer = new ConfigHandler.ConfigContainer(mod, config, new Configuration(file));
      this.configContainers.put(mod.getModId(), configContainer);
      configContainer.load();
      configContainer.save();
      return config;
   }

   private class ConfigContainer {
      public final Object wrappedConfig;
      public final Configuration forgeConfiguration;
      private final List<EntryProperty> entryProperties;

      public ConfigContainer(ModContainer mod, Object wrappedConfig, Configuration forgeConfiguration) {
         this.wrappedConfig = wrappedConfig;
         this.forgeConfiguration = forgeConfiguration;
         this.entryProperties = (List)Arrays.stream(wrappedConfig.getClass().getFields()).filter((field) -> {
            return field.isAnnotationPresent(ConfigEntry.class);
         }).map((field) -> {
            Class<? extends EntryProperty> entryPropertyClass = EntryProperty.getBuiltInPropertyClass(field.getType());
            if (entryPropertyClass == null) {
               entryPropertyClass = (Class)ConfigHandler.this.entryPropertyClasses.get(field.getType());
            }

            if (entryPropertyClass != null) {
               try {
                  Constructor<? extends EntryProperty> constructor = entryPropertyClass.getConstructor(Object.class, Field.class, Configuration.class);
                  return (EntryProperty)constructor.newInstance(wrappedConfig, field, forgeConfiguration);
               } catch (Exception var7) {
                  LLibrary.LOGGER.error("Failed to create property entry {} for mod {}", field.getName(), mod.getName(), var7);
               }
            } else {
               LLibrary.LOGGER.error("Found unsupported config entry {} for mod {}", field.getName(), mod.getName());
            }

            return null;
         }).filter(Objects::nonNull).collect(Collectors.toList());
      }

      public void save() {
         this.entryProperties.forEach(EntryProperty::save);
         this.forgeConfiguration.save();
      }

      public void load() {
         this.entryProperties.forEach(EntryProperty::load);
      }

      public void reset() {
         this.entryProperties.forEach(EntryProperty::reset);
      }
   }
}
