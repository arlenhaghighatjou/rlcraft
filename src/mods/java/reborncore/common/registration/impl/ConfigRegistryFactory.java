package reborncore.common.registration.impl;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;
import reborncore.RebornCore;
import reborncore.common.RebornCoreConfig;
import reborncore.common.registration.IRegistryFactory;
import reborncore.common.registration.RebornRegistry;
import reborncore.common.registration.RegistrationManager;
import reborncore.common.registration.RegistryConstructionEvent;
import reborncore.common.registration.RegistryTarget;
import reborncore.common.util.serialization.SerializationUtil;

@IRegistryFactory.RegistryFactory
public class ConfigRegistryFactory implements IRegistryFactory {
   private static File configDir = null;
   private static HashMap<String, Configuration> configMap = new HashMap();
   private static HashMap<Configuration, ConfigRegistryFactory.ConfigData> configDataMap = new HashMap();
   private static NBTTagCompound configVersionTag = null;

   public ConfigRegistryFactory() {
   }

   public Class<? extends Annotation> getAnnotation() {
      return ConfigRegistry.class;
   }

   public void handleField(Field field) {
      try {
         RebornRegistry rebornRegistry = (RebornRegistry)RegistrationManager.getAnnoation(field.getDeclaringClass().getAnnotations(), RebornRegistry.class);
         ConfigRegistry annotation = (ConfigRegistry)RegistrationManager.getAnnoationFromArray(field.getAnnotations(), this);
         Configuration configuration = getOrCreateConfig(annotation, rebornRegistry);
         if (!Modifier.isStatic(field.getModifiers())) {
            throw new RuntimeException("Field " + field.getName() + " must be static");
         } else {
            if (!Modifier.isPublic(field.getModifiers())) {
               field.setAccessible(true);
            }

            String key = annotation.key();
            if (key.isEmpty()) {
               key = field.getName();
            }

            Object defaultValue = field.get((Object)null);
            String comment = annotation.comment();
            comment = comment + " [Default=" + defaultValue + "]";
            Property property = get(annotation.category(), key, defaultValue, comment, field.getType(), configuration);
            if (this.shouldReset(property, annotation, rebornRegistry.modID()) && RebornCoreConfig.configUpdating) {
               property.setToDefault();
            }

            Object value;
            try {
               value = getObjectFromProperty(property, field);
            } catch (Exception var11) {
               RebornCore.logHelper.error("Failed to read config value " + annotation.category() + "." + annotation.key() + " resetting to default value");
               var11.printStackTrace();
               value = defaultValue;
            }

            field.set((Object)null, value);
            ConfigRegistryFactory.ConfigData configData;
            if (configDataMap.containsKey(configuration)) {
               configData = (ConfigRegistryFactory.ConfigData)configDataMap.get(configuration);
            } else {
               configData = new ConfigRegistryFactory.ConfigData(configuration);
               configDataMap.put(configuration, configData);
            }

            configData.fieldMap.put(configData.getName(annotation.category(), property), field);
         }
      } catch (IllegalAccessException var12) {
         throw new Error("Failed to load config", var12);
      }
   }

   public void factoryComplete() {
      MinecraftForge.EVENT_BUS.post(new ConfigRegistryFactory.RebornRegistryEvent());
      saveAll();
   }

   private static Object getObjectFromProperty(Property property, @Nullable Field field) {
      if (property.getType() == Type.STRING) {
         return property.isList() ? getList(property, field) : property.getString();
      } else if (property.getType() == Type.BOOLEAN) {
         return property.getBoolean();
      } else if (property.getType() == Type.INTEGER) {
         return property.getInt();
      } else if (property.getType() == Type.DOUBLE) {
         return property.getDouble();
      } else {
         throw new RuntimeException("Type not supported");
      }
   }

   public static Property get(String category, String key, Object defaultValue, String comment, Class<?> type, Configuration configuration) {
      if (type == String.class) {
         return configuration.get(category, key, (String)defaultValue, comment);
      } else if (type == Boolean.TYPE) {
         return configuration.get(category, key, ((Boolean)defaultValue).booleanValue(), comment);
      } else if (type == Integer.TYPE) {
         return configuration.get(category, key, ((Integer)defaultValue).intValue(), comment);
      } else if (type == Double.TYPE) {
         return configuration.get(category, key, ((Double)defaultValue).doubleValue(), comment);
      } else if (type == List.class) {
         return getListProperty(category, key, defaultValue, comment, type, configuration);
      } else {
         throw new RuntimeException("Type not supported: " + type);
      }
   }

   public static Configuration getOrCreateConfig(ConfigRegistry annotation, RebornRegistry rebornRegistry) {
      return getOrCreateConfig(rebornRegistry.modID(), annotation.config());
   }

   private static Configuration getOrCreateConfig(String modId, String config) {
      String configIdent = modId;
      if (!config.isEmpty()) {
         configIdent = modId + ":" + config;
      }

      Configuration configuration;
      if (configMap.containsKey(configIdent)) {
         configuration = (Configuration)configMap.get(configIdent);
      } else {
         File modConfigDir = new File(configDir, modId);
         String configName = "config.cfg";
         if (!config.isEmpty()) {
            configName = config + ".cfg";
         }

         configuration = new Configuration(new File(modConfigDir, configName));
         configMap.put(configIdent, configuration);
      }

      return configuration;
   }

   public static List<Pair<Configuration, String>> getConfigs(String modid) {
      return (List)configMap.entrySet().stream().filter((entry) -> {
         return ((String)entry.getKey()).startsWith(modid);
      }).map((entry) -> {
         return Pair.of(entry.getValue(), entry.getKey());
      }).collect(Collectors.toList());
   }

   public static void saveAll() {
      for(Entry<String, Configuration> configurationEntry : configMap.entrySet()) {
         ((Configuration)configurationEntry.getValue()).save();
      }

      try {
         CompressedStreamTools.write(configVersionTag, new File(configDir, "configData.nbt"));
      } catch (IOException var2) {
         throw new RuntimeException("Failed to save config data", var2);
      }
   }

   @SubscribeEvent
   public static void onChange(OnConfigChangedEvent event) {
      List<Pair<Configuration, String>> configs = getConfigs(event.getModID());
      if (!configs.isEmpty()) {
         configs.forEach((pair) -> {
            reload((Configuration)pair.getLeft());
         });
      }
   }

   public static void reload(Configuration configuration) {
      if (configDataMap.containsKey(configuration)) {
         ConfigRegistryFactory.ConfigData data = (ConfigRegistryFactory.ConfigData)configDataMap.get(configuration);

         for(String category : configuration.getCategoryNames()) {
            for(Property property : configuration.getCategory(category).values()) {
               Field field = (Field)data.fieldMap.get(data.getName(category, property));
               if (field == null) {
                  RebornCore.logHelper.debug("failed to find field for " + property.getName());
               } else {
                  try {
                     field.set((Object)null, getObjectFromProperty(property, field));
                  } catch (IllegalAccessException var8) {
                     var8.printStackTrace();
                  }
               }
            }
         }

         configuration.save();
      }
   }

   public static void setConfigDir(File configDir) {
      configDir = configDir;
      File dataFile = new File(configDir, "configData.nbt");
      if (dataFile.exists()) {
         try {
            configVersionTag = CompressedStreamTools.read(dataFile);
         } catch (Exception var3) {
            var3.printStackTrace();
            RebornCore.logHelper.error("Failed to read config data, resetting.");
            RebornCore.logHelper.error(var3);
            dataFile.delete();
            configVersionTag = new NBTTagCompound();
         }
      } else {
         configVersionTag = new NBTTagCompound();
      }

   }

   public boolean shouldReset(Property property, ConfigRegistry configRegistry, String modID) {
      NBTTagCompound propertyTag = configVersionTag.getCompoundTag(this.getPropertyString(configRegistry, modID));
      propertyTag.setInteger("version", 0);
      propertyTag.setString("comment", configRegistry.comment());
      propertyTag.setString("key", configRegistry.key());
      propertyTag.setString("config", configRegistry.config());
      String configValue = property.getString();
      String savedDefault = propertyTag.getString("default");
      boolean shouldReset = false;
      if (configValue.equals(savedDefault) && !configValue.equals(property.getDefault())) {
         RebornCore.logHelper.info(configRegistry.key() + " is being reset to new mod default:" + property.getDefault());
         shouldReset = true;
      } else {
         propertyTag.setString("default", property.getDefault());
      }

      configVersionTag.setTag(this.getPropertyString(configRegistry, modID), propertyTag);
      return shouldReset;
   }

   private String getPropertyString(ConfigRegistry configRegistry, String modid) {
      return modid + "." + configRegistry.config() + "." + configRegistry.category() + "." + configRegistry.key();
   }

   public List<RegistryTarget> getTargets() {
      return Collections.singletonList(RegistryTarget.FIELD);
   }

   public Class<? extends FMLStateEvent> getProcessSate() {
      return RegistryConstructionEvent.class;
   }

   private static List getList(Property property, Field field) {
      ParameterizedType genericType = (ParameterizedType)field.getGenericType();
      Class<?> listClass = (Class)genericType.getActualTypeArguments()[0];
      if (listClass == String.class) {
         return Arrays.asList(property.getStringList());
      } else if (listClass == ItemStack.class) {
         List<ItemStack> stacks = (List)Arrays.stream(property.getStringList()).map((s) -> {
            return (ItemStack)SerializationUtil.GSON_FLAT.fromJson(s, ItemStack.class);
         }).collect(Collectors.toList());
         return stacks;
      } else {
         throw new UnsupportedOperationException("List type " + listClass.getName() + " not supported");
      }
   }

   private static Property getListProperty(String category, String key, Object defaultValue, String comment, Class<?> type, Configuration configuration) {
      List defaultList = (List)defaultValue;
      if (defaultList.isEmpty()) {
         return configuration.get(category, key, new String[0], comment);
      } else if (defaultList.get(0) instanceof ItemStack) {
         String[] stackList = (String[])defaultList.stream().map((o) -> {
            ItemStack itemStack = (ItemStack)o;
            return SerializationUtil.GSON_FLAT.toJson(itemStack);
         }).toArray((x$0) -> {
            return new String[x$0];
         });
         return configuration.get(category, key, stackList, comment);
      } else {
         throw new UnsupportedOperationException("List type " + defaultList.get(0).getClass().getName() + " not supported");
      }
   }

   private static class ConfigData {
      Configuration configuration;
      Map<String, Field> fieldMap;

      public ConfigData(Configuration configuration) {
         this.configuration = configuration;
         this.fieldMap = new HashMap();
      }

      public String getName(String category, Property property) {
         return category + ":" + property.getName();
      }
   }

   public class RebornRegistryEvent extends Event {
      public RebornRegistryEvent() {
      }

      public Configuration getConfiguration(String modid, String config) {
         return ConfigRegistryFactory.getOrCreateConfig(modid, config);
      }
   }
}
