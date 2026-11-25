package org.orecruncher.lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.lib.math.MathStuff;

public final class ConfigProcessor {
   public ConfigProcessor() {
   }

   public static void process(@Nonnull Configuration config, @Nonnull Class<?> clazz) {
      process(config, clazz, (Object)null);
   }

   public static void process(@Nonnull Configuration config, @Nonnull Class<?> clazz, @Nullable Object parameters) {
      process(config, (String)null, clazz, parameters);
   }

   private static void process(@Nonnull Configuration config, @Nullable String category, @Nonnull Class<?> clazz, @Nullable Object parameters) {
      for(Field field : clazz.getFields()) {
         ConfigProcessor.Option annotation = (ConfigProcessor.Option)field.getAnnotation(ConfigProcessor.Option.class);
         if (annotation != null) {
            String property = annotation.value();
            String language = field.getAnnotation(ConfigProcessor.LangKey.class) != null ? ((ConfigProcessor.LangKey)field.getAnnotation(ConfigProcessor.LangKey.class)).value() : null;
            String comment = field.getAnnotation(ConfigProcessor.Comment.class) != null ? ((ConfigProcessor.Comment)field.getAnnotation(ConfigProcessor.Comment.class)).value() : null;
            String defaultValue = field.getAnnotation(ConfigProcessor.DefaultValue.class) != null ? ((ConfigProcessor.DefaultValue)field.getAnnotation(ConfigProcessor.DefaultValue.class)).value() : null;

            try {
               ConfigCategory cat = config.getCategory(category);
               Property prop = cat.get(property);
               Object fieldValue = field.get(parameters);
               if (fieldValue instanceof Boolean) {
                  boolean dv = StringUtils.isEmpty(defaultValue) ? false : Boolean.valueOf(defaultValue).booleanValue();
                  if (prop == null || prop != null && prop.getType() != Type.BOOLEAN) {
                     cat.remove(property);
                     prop = new Property(property, Boolean.toString(dv), Type.BOOLEAN);
                     cat.put(property, prop);
                  }

                  field.set(parameters, Boolean.valueOf(config.getBoolean(property, category, dv, comment)));
               } else if (fieldValue instanceof Integer) {
                  int minInt = Integer.MIN_VALUE;
                  int maxInt = Integer.MAX_VALUE;
                  ConfigProcessor.RangeInt mmi = (ConfigProcessor.RangeInt)field.getAnnotation(ConfigProcessor.RangeInt.class);
                  if (mmi != null) {
                     minInt = mmi.min();
                     maxInt = mmi.max();
                  }

                  int dv = StringUtils.isEmpty(defaultValue) ? MathStuff.clamp(0, minInt, maxInt) : Integer.valueOf(defaultValue).intValue();
                  if (prop == null || prop != null && prop.getType() != Type.INTEGER) {
                     cat.remove(property);
                     prop = new Property(property, Integer.toString(dv), Type.INTEGER);
                     cat.put(property, prop);
                  }

                  field.set(parameters, Integer.valueOf(config.getInt(property, category, dv, minInt, maxInt, comment)));
                  prop.setValue(field.getInt(parameters));
               } else if (fieldValue instanceof Float) {
                  float minFloat = Float.MIN_VALUE;
                  float maxFloat = Float.MAX_VALUE;
                  ConfigProcessor.RangeFloat mmf = (ConfigProcessor.RangeFloat)field.getAnnotation(ConfigProcessor.RangeFloat.class);
                  if (mmf != null) {
                     minFloat = mmf.min();
                     maxFloat = mmf.max();
                  }

                  float dv = StringUtils.isEmpty(defaultValue) ? MathStuff.clamp(0.0F, minFloat, maxFloat) : Float.valueOf(defaultValue).floatValue();
                  if (prop == null || prop != null && prop.getType() != Type.DOUBLE) {
                     cat.remove(property);
                     prop = new Property(property, Double.toString((double)dv), Type.DOUBLE);
                     cat.put(property, prop);
                  }

                  field.set(parameters, Float.valueOf(config.getFloat(property, category, dv, minFloat, maxFloat, comment)));
                  prop.setValue((double)field.getFloat(parameters));
               } else if (fieldValue instanceof String) {
                  field.set(parameters, config.getString(property, category, defaultValue, comment));
               } else if (fieldValue instanceof String[]) {
                  field.set(parameters, config.getStringList(property, category, StringUtils.split(defaultValue, ','), comment));
               }

               if (!StringUtils.isEmpty(language)) {
                  prop.setLanguageKey(language);
               }

               if (field.getAnnotation(ConfigProcessor.RestartRequired.class) != null) {
                  ConfigProcessor.RestartRequired restart = (ConfigProcessor.RestartRequired)field.getAnnotation(ConfigProcessor.RestartRequired.class);
                  prop.setRequiresMcRestart(restart.server());
                  prop.setRequiresWorldRestart(restart.world());
               } else {
                  prop.setRequiresMcRestart(false);
                  prop.setRequiresWorldRestart(false);
               }

               prop.setShowInGui(field.getAnnotation(ConfigProcessor.Hidden.class) == null);
            } catch (Throwable var21) {
               LibLog.log().error("Unable to parse configuration", var21);
            }
         }
      }

      for(Class<?> c : clazz.getDeclaredClasses()) {
         ConfigProcessor.Category annotation = (ConfigProcessor.Category)c.getAnnotation(ConfigProcessor.Category.class);
         if (annotation != null) {
            String s = StringUtils.isEmpty(category) ? annotation.value() : category + "." + annotation.value();
            ConfigProcessor.LangKey lk = (ConfigProcessor.LangKey)c.getAnnotation(ConfigProcessor.LangKey.class);
            if (lk != null) {
               config.setCategoryLanguageKey(s, lk.value());
            }

            ConfigProcessor.RestartRequired rr = (ConfigProcessor.RestartRequired)c.getAnnotation(ConfigProcessor.RestartRequired.class);
            if (rr != null) {
               config.setCategoryRequiresMcRestart(s, rr.server());
               config.setCategoryRequiresWorldRestart(s, rr.world());
            } else {
               config.setCategoryRequiresMcRestart(s, false);
               config.setCategoryRequiresWorldRestart(s, false);
            }

            try {
               ReflectedField.ObjectField<Object, String> path = new ReflectedField.ObjectField<Object, String>(c, "PATH", (String)null);
               if (path.isAvailable()) {
                  path.set((Object)null, s);
               }
            } catch (Exception var20) {
               ;
            }

            ConfigProcessor.Comment com = (ConfigProcessor.Comment)c.getAnnotation(ConfigProcessor.Comment.class);
            if (com != null) {
               config.setCategoryComment(s, com.value());
            }

            process(config, s, c, parameters);
         }
      }

   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.TYPE})
   public @interface Category {
      String value();
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.TYPE, ElementType.FIELD})
   public @interface Comment {
      String value();
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface DefaultValue {
      String value();
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface Hidden {
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.TYPE, ElementType.FIELD})
   public @interface LangKey {
      String value();
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface Option {
      String value();
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface RangeFloat {
      float min() default Float.MIN_VALUE;

      float max() default Float.MAX_VALUE;
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface RangeInt {
      int min() default Integer.MIN_VALUE;

      int max() default Integer.MAX_VALUE;
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.TYPE, ElementType.FIELD})
   public @interface RestartRequired {
      boolean world() default true;

      boolean server() default false;
   }
}
