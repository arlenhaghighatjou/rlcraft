package net.ilexiconn.llibrary.server.config;

import java.lang.reflect.Field;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/** @deprecated */
@Deprecated
public abstract class EntryProperty {
   protected final ConfigEntry entry;
   protected final Object wrappedConfig;
   protected final Field wrappedField;
   protected final Property forgeConfigurationProperty;
   protected final Object defaultValue;

   protected EntryProperty(Object wrappedConfig, Field wrappedField, Configuration forgeConfiguration) {
      this.entry = (ConfigEntry)wrappedField.getAnnotation(ConfigEntry.class);
      this.wrappedConfig = wrappedConfig;
      this.wrappedField = wrappedField;
      this.forgeConfigurationProperty = this.createForgeConfigurationProperty(forgeConfiguration);
      this.defaultValue = this.get();
      this.load();
   }

   public String getName() {
      return this.entry.name().isEmpty() ? this.wrappedField.getName() : this.entry.name();
   }

   public Object get() {
      try {
         return this.wrappedField.get(this.wrappedConfig);
      } catch (IllegalAccessException var2) {
         throw new RuntimeException("Cannot access ConfigEntry field. Is it not public?");
      }
   }

   public void set(Object value) {
      try {
         this.wrappedField.set(this.wrappedConfig, value);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException("Cannot access ConfigEntry field. Is it not public?");
      }
   }

   public void reset() {
      this.set(this.defaultValue);
   }

   public abstract void save();

   public abstract void load();

   protected abstract Property createForgeConfigurationProperty(Configuration var1);

   public static Class<? extends EntryProperty> getBuiltInPropertyClass(Class<?> type) {
      if (!Integer.class.isAssignableFrom(type) && !Integer.TYPE.isAssignableFrom(type)) {
         if (!Boolean.class.isAssignableFrom(type) && !Boolean.TYPE.isAssignableFrom(type)) {
            if (String.class.isAssignableFrom(type)) {
               return EntryProperty.StringEntryProperty.class;
            } else if (!Float.class.isAssignableFrom(type) && !Float.TYPE.isAssignableFrom(type)) {
               if (!Double.class.isAssignableFrom(type) && !Double.TYPE.isAssignableFrom(type)) {
                  if (!Integer[].class.isAssignableFrom(type) && !int[].class.isAssignableFrom(type)) {
                     if (!Boolean[].class.isAssignableFrom(type) && !boolean[].class.isAssignableFrom(type)) {
                        if (String[].class.isAssignableFrom(type)) {
                           return EntryProperty.StringArrayEntryProperty.class;
                        } else if (!Float[].class.isAssignableFrom(type) && !float[].class.isAssignableFrom(type)) {
                           return !Double[].class.isAssignableFrom(type) && !double[].class.isAssignableFrom(type) ? null : EntryProperty.DoubleArrayEntryProperty.class;
                        } else {
                           return EntryProperty.FloatArrayEntryProperty.class;
                        }
                     } else {
                        return EntryProperty.BooleanArrayEntryProperty.class;
                     }
                  } else {
                     return EntryProperty.IntegerArrayEntryProperty.class;
                  }
               } else {
                  return EntryProperty.DoubleEntryProperty.class;
               }
            } else {
               return EntryProperty.FloatEntryProperty.class;
            }
         } else {
            return EntryProperty.BooleanEntryProperty.class;
         }
      } else {
         return EntryProperty.IntegerEntryProperty.class;
      }
   }

   public static class BooleanArrayEntryProperty extends EntryProperty {
      public BooleanArrayEntryProperty(Object wrappedConfig, Field field, Configuration forgeConfiguration) {
         super(wrappedConfig, field, forgeConfiguration);
      }

      protected Property createForgeConfigurationProperty(Configuration forgeConfiguration) {
         Property property = forgeConfiguration.get(this.entry.category(), this.getName(), (boolean[])this.get(), this.entry.comment());
         property.setValidValues(this.entry.validValues());
         return property;
      }

      public void save() {
         this.forgeConfigurationProperty.set((boolean[])this.get());
      }

      public void load() {
         this.set(this.forgeConfigurationProperty.getBooleanList());
      }
   }

   public static class BooleanEntryProperty extends EntryProperty {
      public BooleanEntryProperty(Object wrappedConfig, Field field, Configuration forgeConfiguration) {
         super(wrappedConfig, field, forgeConfiguration);
      }

      protected Property createForgeConfigurationProperty(Configuration forgeConfiguration) {
         return forgeConfiguration.get(this.entry.category(), this.getName(), ((Boolean)this.get()).booleanValue(), this.entry.comment());
      }

      public void save() {
         this.forgeConfigurationProperty.set(((Boolean)this.get()).booleanValue());
      }

      public void load() {
         this.set(Boolean.valueOf(this.forgeConfigurationProperty.getBoolean()));
      }
   }

   public static class DoubleArrayEntryProperty extends EntryProperty {
      public DoubleArrayEntryProperty(Object wrappedConfig, Field field, Configuration forgeConfiguration) {
         super(wrappedConfig, field, forgeConfiguration);
      }

      protected Property createForgeConfigurationProperty(Configuration forgeConfiguration) {
         Property property = forgeConfiguration.get(this.entry.category(), this.getName(), (double[])this.get(), this.entry.comment());
         property.setValidValues(this.entry.validValues());
         return property;
      }

      public void save() {
         this.forgeConfigurationProperty.set((double[])this.get());
      }

      public void load() {
         this.set(this.forgeConfigurationProperty.getDoubleList());
      }
   }

   public static class DoubleEntryProperty extends EntryProperty {
      public DoubleEntryProperty(Object wrappedConfig, Field field, Configuration forgeConfiguration) {
         super(wrappedConfig, field, forgeConfiguration);
      }

      protected Property createForgeConfigurationProperty(Configuration forgeConfiguration) {
         double minDouble = this.entry.minValue().isEmpty() ? Double.MIN_VALUE : Double.parseDouble(this.entry.minValue());
         double maxDouble = this.entry.maxValue().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(this.entry.maxValue());
         return forgeConfiguration.get(this.entry.category(), this.getName(), ((Double)this.get()).doubleValue(), this.entry.comment(), minDouble, maxDouble);
      }

      public void save() {
         this.forgeConfigurationProperty.set(((Double)this.get()).doubleValue());
      }

      public void load() {
         this.set(Double.valueOf(this.forgeConfigurationProperty.getDouble()));
      }
   }

   public static class FloatArrayEntryProperty extends EntryProperty {
      public FloatArrayEntryProperty(Object wrappedConfig, Field field, Configuration forgeConfiguration) {
         super(wrappedConfig, field, forgeConfiguration);
      }

      protected Property createForgeConfigurationProperty(Configuration forgeConfiguration) {
         Property property = forgeConfiguration.get(this.entry.category(), this.getName(), this.getDoubleArray(), this.entry.comment());
         property.setValidValues(this.entry.validValues());
         return property;
      }

      public void save() {
         this.forgeConfigurationProperty.set(this.getDoubleArray());
      }

      public void load() {
         this.setDoubleArray(this.forgeConfigurationProperty.getDoubleList());
      }

      public double[] getDoubleArray() {
         float[] floats = (float[])this.get();
         double[] doubles = new double[floats.length];

         for(int i = 0; i < floats.length; ++i) {
            floats[i] = (float)doubles[i];
         }

         return doubles;
      }

      public void setDoubleArray(double[] doubles) {
         float[] floats = new float[doubles.length];

         for(int i = 0; i < doubles.length; ++i) {
            floats[i] = (float)doubles[i];
         }

         this.set(floats);
      }
   }

   public static class FloatEntryProperty extends EntryProperty {
      public FloatEntryProperty(Object wrappedConfig, Field field, Configuration forgeConfiguration) {
         super(wrappedConfig, field, forgeConfiguration);
      }

      protected Property createForgeConfigurationProperty(Configuration forgeConfiguration) {
         float minFloat = this.entry.minValue().isEmpty() ? Float.MIN_VALUE : Float.parseFloat(this.entry.minValue());
         float maxFloat = this.entry.maxValue().isEmpty() ? Float.MAX_VALUE : Float.parseFloat(this.entry.maxValue());
         return forgeConfiguration.get(this.entry.category(), this.getName(), (double)((Float)this.get()).floatValue(), this.entry.comment(), (double)minFloat, (double)maxFloat);
      }

      public void save() {
         this.forgeConfigurationProperty.set((double)((Float)this.get()).floatValue());
      }

      public void load() {
         this.set(Float.valueOf((float)this.forgeConfigurationProperty.getDouble()));
      }
   }

   public static class IntegerArrayEntryProperty extends EntryProperty {
      public IntegerArrayEntryProperty(Object wrappedConfig, Field field, Configuration forgeConfiguration) {
         super(wrappedConfig, field, forgeConfiguration);
      }

      protected Property createForgeConfigurationProperty(Configuration forgeConfiguration) {
         Property property = forgeConfiguration.get(this.entry.category(), this.getName(), (int[])this.get(), this.entry.comment());
         property.setValidValues(this.entry.validValues());
         return property;
      }

      public void save() {
         this.forgeConfigurationProperty.set((int[])this.get());
      }

      public void load() {
         this.set(this.forgeConfigurationProperty.getIntList());
      }
   }

   public static class IntegerEntryProperty extends EntryProperty {
      public IntegerEntryProperty(Object wrappedConfig, Field field, Configuration forgeConfiguration) {
         super(wrappedConfig, field, forgeConfiguration);
      }

      protected Property createForgeConfigurationProperty(Configuration forgeConfiguration) {
         int minInt = this.entry.minValue().isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(this.entry.minValue());
         int maxInt = this.entry.maxValue().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(this.entry.maxValue());
         return forgeConfiguration.get(this.entry.category(), this.getName(), ((Integer)this.get()).intValue(), this.entry.comment(), minInt, maxInt);
      }

      public void save() {
         this.forgeConfigurationProperty.set(((Integer)this.get()).intValue());
      }

      public void load() {
         this.set(Integer.valueOf(this.forgeConfigurationProperty.getInt()));
      }
   }

   public static class StringArrayEntryProperty extends EntryProperty {
      public StringArrayEntryProperty(Object wrappedConfig, Field field, Configuration forgeConfiguration) {
         super(wrappedConfig, field, forgeConfiguration);
      }

      protected Property createForgeConfigurationProperty(Configuration forgeConfiguration) {
         Property property = forgeConfiguration.get(this.entry.category(), this.getName(), (String[])this.get(), this.entry.comment());
         property.setValidValues(this.entry.validValues());
         return property;
      }

      public void save() {
         this.forgeConfigurationProperty.set((String[])this.get());
      }

      public void load() {
         this.set(this.forgeConfigurationProperty.getStringList());
      }
   }

   public static class StringEntryProperty extends EntryProperty {
      public StringEntryProperty(Object wrappedConfig, Field field, Configuration forgeConfiguration) {
         super(wrappedConfig, field, forgeConfiguration);
      }

      protected Property createForgeConfigurationProperty(Configuration forgeConfiguration) {
         return forgeConfiguration.get(this.entry.category(), this.getName(), (String)this.get(), this.entry.comment());
      }

      public void save() {
         this.forgeConfigurationProperty.set((String)this.get());
      }

      public void load() {
         this.set(this.forgeConfigurationProperty.getString());
      }
   }
}
