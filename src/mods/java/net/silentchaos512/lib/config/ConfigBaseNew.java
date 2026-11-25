package net.silentchaos512.lib.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import javax.activation.UnsupportedDataTypeException;
import javax.annotation.Nullable;
import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.LogHelper;

public abstract class ConfigBaseNew extends ConfigBase {
   public ConfigBaseNew(String modId) {
      super(modId);
   }

   public abstract I18nHelper i18n();

   public abstract LogHelper log();

   public void load() {
      this.readAnnotatedFields();
   }

   private void readAnnotatedFields() {
      this.readAnnotatedFields(this.getClass());
   }

   private void readAnnotatedFields(Class<?> clazz) {
      for(Class<?> innerClass : clazz.getDeclaredClasses()) {
         this.readAnnotatedFields(innerClass);
      }

      for(Field field : clazz.getDeclaredFields()) {
         ConfigOption config = null;
         ConfigOption.Comment comment = null;
         ConfigOption.BooleanDefault booleanDefault = null;
         ConfigOption.RangeDouble rangeDouble = null;
         ConfigOption.RangeFloat rangeFloat = null;
         ConfigOption.RangeInt rangeInt = null;
         ConfigOption.RangeLong rangeLong = null;

         for(Annotation annotation : field.getDeclaredAnnotations()) {
            if (annotation instanceof ConfigOption) {
               config = (ConfigOption)annotation;
            } else if (annotation instanceof ConfigOption.Comment) {
               comment = (ConfigOption.Comment)annotation;
            } else if (annotation instanceof ConfigOption.RangeDouble) {
               rangeDouble = (ConfigOption.RangeDouble)annotation;
            } else if (annotation instanceof ConfigOption.RangeFloat) {
               rangeFloat = (ConfigOption.RangeFloat)annotation;
            } else if (annotation instanceof ConfigOption.RangeInt) {
               rangeInt = (ConfigOption.RangeInt)annotation;
            } else if (annotation instanceof ConfigOption.RangeLong) {
               rangeLong = (ConfigOption.RangeLong)annotation;
            } else if (annotation instanceof ConfigOption.BooleanDefault) {
               booleanDefault = (ConfigOption.BooleanDefault)annotation;
            }
         }

         if (config != null) {
            String commentTranslated = comment != null ? comment.value() : "";

            try {
               if (field.getType() == Integer.TYPE) {
                  field.set(this, Integer.valueOf(this.readInt(field, config, rangeInt, commentTranslated)));
               } else if (field.getType() == Float.TYPE) {
                  field.set(this, Float.valueOf(this.readFloat(field, config, rangeFloat, commentTranslated)));
               } else if (field.getType() == Double.TYPE) {
                  field.set(this, Double.valueOf(this.readDouble(field, config, rangeDouble, commentTranslated)));
               } else if (field.getType() == Long.TYPE) {
                  field.set(this, Long.valueOf(this.readLong(field, config, rangeLong, commentTranslated)));
               } else {
                  if (field.getType() != Boolean.TYPE) {
                     throw new UnsupportedDataTypeException("Don't know how to load config for type " + field.getType() + "!");
                  }

                  field.set(this, this.readBoolean(field, config, booleanDefault, commentTranslated));
               }
            } catch (IllegalAccessException var17) {
               this.log().error("Field \"{}\" is not accessible?", field.getName());
               this.log().catching(var17);
            } catch (UnsupportedDataTypeException var18) {
               this.log().error("Unknown field type");
               this.log().catching(var18);
            }
         }
      }

   }

   private double readDouble(Field field, ConfigOption config, @Nullable ConfigOption.RangeDouble range, String comment) throws IllegalAccessException {
      double defaultValue = range != null ? range.value() : field.getDouble(this);
      return range != null ? this.loadDouble(config.name(), config.category(), defaultValue, range.min(), range.max(), comment) : this.loadDouble(config.name(), config.category(), defaultValue, comment);
   }

   private float readFloat(Field field, ConfigOption config, @Nullable ConfigOption.RangeFloat range, String comment) throws IllegalAccessException {
      float defaultValue = range != null ? range.value() : field.getFloat(this);
      return range != null ? this.loadFloat(config.name(), config.category(), defaultValue, range.min(), range.max(), comment) : this.loadFloat(config.name(), config.category(), defaultValue, comment);
   }

   private int readInt(Field field, ConfigOption config, @Nullable ConfigOption.RangeInt range, String comment) throws IllegalAccessException {
      int defaultValue = range != null ? range.value() : field.getInt(this);
      return range != null ? this.loadInt(config.name(), config.category(), defaultValue, range.min(), range.max(), comment) : this.loadInt(config.name(), config.category(), defaultValue, comment);
   }

   private long readLong(Field field, ConfigOption config, @Nullable ConfigOption.RangeLong range, String comment) throws IllegalAccessException {
      long defaultValue = range != null ? range.value() : field.getLong(this);
      return range != null ? this.loadLong(config.name(), config.category(), defaultValue, range.min(), range.max(), comment) : this.loadLong(config.name(), config.category(), defaultValue, comment);
   }

   private Object readBoolean(Field field, ConfigOption config, @Nullable ConfigOption.BooleanDefault booleanDefault, String comment) throws IllegalAccessException {
      boolean defaultValue = booleanDefault != null ? booleanDefault.value() : field.getBoolean(this);
      return this.loadBoolean(config.name(), config.category(), defaultValue, comment);
   }
}
