package ichttt.mods.firstaid.common.config;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.FieldWrapper;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Property.Type;
import net.minecraftforge.fml.common.Loader;

public class ExtraConfigManager {
   private static List<String> toDeleteEntries = new ArrayList();
   private static final Field CONFIG_FIELD;

   public ExtraConfigManager() {
   }

   public static void scheduleDelete(String name) {
      if (toDeleteEntries != null) {
         toDeleteEntries.add(name);
      } else {
         throw new IllegalStateException("Already cleaned up configs");
      }
   }

   private static Configuration getConfigFromField(File f) {
      if (CONFIG_FIELD != null) {
         try {
            return (Configuration)((Map)CONFIG_FIELD.get((Object)null)).get(f.getAbsolutePath());
         } catch (Exception var2) {
            FirstAid.LOGGER.error("Could not get config from field - enabling fallback", var2);
            return null;
         }
      } else {
         return null;
      }
   }

   public static void postProcessConfigs() {
      if (!toDeleteEntries.isEmpty()) {
         Config annotation = (Config)FirstAidConfig.class.getAnnotation(Config.class);
         String name = annotation.name();
         if (Strings.isNullOrEmpty(name)) {
            name = annotation.modid();
         }

         Configuration config = getConfigFromField(new File(Loader.instance().getConfigDir(), name + ".cfg"));
         if (config == null) {
            FirstAid.LOGGER.warn("Skipping post processing due to null config");
         } else {
            for(String s : toDeleteEntries) {
               String[] path = s.split("\\.");
               String catString = "general" + (path.length > 1 ? "." + s.substring(0, s.length() - (path[path.length - 1].length() + 1)) : "");
               if (config.hasCategory(catString)) {
                  ConfigCategory cat = config.getCategory(catString);
                  if (cat.containsKey(path[path.length - 1])) {
                     FirstAid.LOGGER.info("Removing prop " + s);
                     cat.remove(path[path.length - 1]);
                  }
               } else {
                  FirstAid.LOGGER.warn("Unable to find config category {} for removal of old config options", catString);
               }
            }

            if (config.hasChanged()) {
               config.save();
            }

            toDeleteEntries = null;
         }
      }
   }

   public static <T extends Annotation> List<ConfigEntry<T>> getAnnotatedFields(Class<T> annotationClass, Class<?> clazz) {
      return getAnnotatedFields(annotationClass, clazz, "general", (Object)null);
   }

   private static <T extends Annotation> List<ConfigEntry<T>> getAnnotatedFields(Class<T> annotationClass, Class<?> clazz, String category, Object instance) {
      Builder<ConfigEntry<T>> listBuilder = ImmutableList.builder();

      for(Field f : clazz.getDeclaredFields()) {
         T annotation = f.getAnnotation(annotationClass);
         if (annotation != null && FieldWrapper.hasWrapperFor(f)) {
            Object typeAdapter = FieldWrapper.get(instance, f, category).getTypeAdapter();
            UniqueProperty.Type type;
            if (typeAdapter == null) {
               type = UniqueProperty.Type.UNKNOWN;
            } else {
               try {
                  Method m = typeAdapter.getClass().getDeclaredMethod("getType");
                  m.setAccessible(true);
                  Type propType = (Type)m.invoke(typeAdapter);
                  type = UniqueProperty.Type.fromType(propType);
               } catch (ClassCastException | ReflectiveOperationException var15) {
                  FirstAid.LOGGER.fatal("Error getting type from type adapter for field " + f, var15);
                  type = UniqueProperty.Type.UNKNOWN;
               }
            }

            listBuilder.add(new ConfigEntry(f, instance, annotation, annotationClass == ExtraConfig.Sync.class, UniqueProperty.fromProperty(f, "firstaid", category, type)));
         }

         if (!FieldWrapper.hasWrapperFor(f) && f.getType().getSuperclass() != null && f.getType().getSuperclass().equals(Object.class)) {
            String sub = (category.isEmpty() ? "" : category + ".") + getName(f).toLowerCase(Locale.ENGLISH);
            if (annotation != null) {
               listBuilder.add(new ConfigEntry(f, instance, annotation, annotationClass == ExtraConfig.Sync.class, new UniqueProperty(getName(f), UniqueProperty.getLangKey(f, "firstaid", category), UniqueProperty.Type.CATEGORY)));
            }

            try {
               Object newInstance = f.get(instance);
               listBuilder.addAll(getAnnotatedFields(annotationClass, newInstance.getClass(), sub, newInstance));
            } catch (IllegalAccessException var14) {
               FirstAid.LOGGER.error("Error creating new instance of field " + f, var14);
            }
         }
      }

      List<ConfigEntry<T>> list = listBuilder.build();
      if (instance == null) {
         FirstAid.LOGGER.debug("Found {} annotations of the type {} for the {}", Integer.valueOf(list.size()), annotationClass, clazz);
      }

      return listBuilder.build();
   }

   private static String getName(Field f) {
      return f.isAnnotationPresent(Name.class) ? ((Name)f.getAnnotation(Name.class)).value() : f.getName();
   }

   static {
      FirstAid.LOGGER.debug("Setting up forge internal reflection");

      Field field;
      try {
         field = ConfigManager.class.getDeclaredField("CONFIGS");
         field.setAccessible(true);
         field.get((Object)null);
      } catch (IllegalArgumentException | ReflectiveOperationException var2) {
         FirstAid.LOGGER.error("Could not setup forge reflection - disabling config post processing", var2);
         field = null;
      }

      CONFIG_FIELD = field;
   }
}
