package com.endertech.minecraft.forge.configs;

import com.endertech.common.FloatBounds;
import com.endertech.common.IntBounds;
import com.endertech.minecraft.forge.units.UnitId;
import java.util.HashMap;
import java.util.Map;

public class MultiConfigProperty {
   public MultiConfigProperty() {
   }

   public abstract static class BaseProperty<K, V> {
      protected final Map<K, V> values = new HashMap();
      private final String category;
      private final String name;
      private final String comment;
      protected V defaultValue;

      protected BaseProperty(ForgeConfig config, String category, String name, V defaultValue, String comment) {
         this.category = category;
         this.name = name;
         this.comment = comment;
         this.defaultValue = defaultValue;
      }

      public abstract V getFrom(ForgeConfig var1, String var2, V var3);

      public V get(K key) {
         return (V)this.values.getOrDefault(key, this.getDefaultValue());
      }

      public void putValueFrom(ForgeConfig config, K key, boolean enabled) {
         V value = (V)this.getFrom(config, this.getCategory(), this.getDefaultValue());
         if (enabled && !value.equals(this.getDefaultValue())) {
            this.values.put(key, value);
         }

      }

      public String getCategory() {
         return this.category;
      }

      public String getName() {
         return this.name;
      }

      public String getComment() {
         return this.comment;
      }

      public V getDefaultValue() {
         return this.defaultValue;
      }
   }

   public static class BoolProperty<K> extends MultiConfigProperty.BaseProperty<K, Boolean> {
      public static <K> MultiConfigProperty.BoolProperty<K> from(ForgeConfig config, String category, String name, boolean defaultValue, String comment) {
         MultiConfigProperty.BoolProperty<K> property = new MultiConfigProperty.BoolProperty<K>(config, category, name, defaultValue, comment);
         property.defaultValue = (V)property.getFrom(config, category, Boolean.valueOf(defaultValue));
         return property;
      }

      protected BoolProperty(ForgeConfig config, String category, String name, boolean defaultValue, String comment) {
         super(config, category, name, Boolean.valueOf(defaultValue), comment);
      }

      public Boolean getFrom(ForgeConfig config, String category, Boolean defaultValue) {
         return ForgeConfig.getBool(config, category, this.getName(), defaultValue.booleanValue(), this.getComment());
      }
   }

   public static class FloatProperty<K> extends MultiConfigProperty.BaseProperty<K, Float> {
      private final FloatBounds bounds;

      public static <K> MultiConfigProperty.FloatProperty<K> from(ForgeConfig config, String category, String name, float defaultValue, FloatBounds bounds, String comment) {
         MultiConfigProperty.FloatProperty<K> property = new MultiConfigProperty.FloatProperty<K>(config, category, name, defaultValue, bounds, comment);
         property.defaultValue = (V)property.getFrom(config, category, Float.valueOf(defaultValue));
         return property;
      }

      protected FloatProperty(ForgeConfig config, String category, String name, float defaultValue, FloatBounds bounds, String comment) {
         super(config, category, name, Float.valueOf(defaultValue), comment);
         this.bounds = bounds;
      }

      public Float getFrom(ForgeConfig config, String category, Float defaultValue) {
         return ForgeConfig.getFloat(config, category, this.getName(), defaultValue.floatValue(), this.bounds, this.getComment());
      }
   }

   public static class IntProperty<K> extends MultiConfigProperty.BaseProperty<K, Integer> {
      private final IntBounds bounds;

      public static <K> MultiConfigProperty.IntProperty<K> from(ForgeConfig config, String category, String name, int defaultValue, IntBounds bounds, String comment) {
         MultiConfigProperty.IntProperty<K> property = new MultiConfigProperty.IntProperty<K>(config, category, name, defaultValue, bounds, comment);
         property.defaultValue = (V)property.getFrom(config, category, Integer.valueOf(defaultValue));
         return property;
      }

      protected IntProperty(ForgeConfig config, String category, String name, int defaultValue, IntBounds bounds, String comment) {
         super(config, category, name, Integer.valueOf(defaultValue), comment);
         this.bounds = bounds;
         this.defaultValue = (V)this.getFrom(config, category, Integer.valueOf(defaultValue));
      }

      public Integer getFrom(ForgeConfig config, String category, Integer defaultValue) {
         return ForgeConfig.getInt(config, category, this.getName(), defaultValue.intValue(), this.bounds, this.getComment());
      }
   }

   public static class StrArrayProperty<K> extends MultiConfigProperty.BaseProperty<K, String[]> {
      public static <K> MultiConfigProperty.StrArrayProperty<K> from(ForgeConfig config, String category, String name, String comment) {
         return from(config, category, name, new String[0], comment);
      }

      public static <K> MultiConfigProperty.StrArrayProperty<K> from(ForgeConfig config, String category, String name, String[] defaultValue, String comment) {
         MultiConfigProperty.StrArrayProperty<K> property = new MultiConfigProperty.StrArrayProperty<K>(config, category, name, defaultValue, comment);
         property.defaultValue = (V)property.getFrom(config, category, defaultValue);
         return property;
      }

      protected StrArrayProperty(ForgeConfig config, String category, String name, String[] defaultValue, String comment) {
         super(config, category, name, defaultValue, comment);
         this.defaultValue = (V)this.getFrom(config, category, defaultValue);
      }

      public String[] getFrom(ForgeConfig config, String category, String[] defaultValue) {
         return ForgeConfig.getStrArray(config, category, this.getName(), defaultValue, this.getComment());
      }
   }

   public static class StringProperty<K> extends MultiConfigProperty.BaseProperty<K, String> {
      public static <K> MultiConfigProperty.StringProperty<K> from(ForgeConfig config, String category, String name, String defaultValue, String comment) {
         MultiConfigProperty.StringProperty<K> property = new MultiConfigProperty.StringProperty<K>(config, category, name, defaultValue, comment);
         property.defaultValue = (V)property.getFrom(config, category, defaultValue);
         return property;
      }

      protected StringProperty(ForgeConfig config, String category, String name, String defaultValue, String comment) {
         super(config, category, name, defaultValue, comment);
         this.defaultValue = (V)this.getFrom(config, category, defaultValue);
      }

      public String getFrom(ForgeConfig config, String category, String defaultValue) {
         return ForgeConfig.getStr(config, category, this.getName(), defaultValue, this.getComment());
      }
   }

   public static class UnitIdProperty<K> extends MultiConfigProperty.BaseProperty<K, UnitId> {
      public static <K> MultiConfigProperty.UnitIdProperty<K> from(ForgeConfig config, String category, String name, UnitId defaultValue, String comment) {
         MultiConfigProperty.UnitIdProperty<K> property = new MultiConfigProperty.UnitIdProperty<K>(config, category, name, defaultValue, comment);
         property.defaultValue = (V)property.getFrom(config, category, defaultValue);
         return property;
      }

      protected UnitIdProperty(ForgeConfig config, String category, String name, UnitId defaultValue, String comment) {
         super(config, category, name, defaultValue, comment);
         this.defaultValue = (V)this.getFrom(config, category, defaultValue);
      }

      public UnitId getFrom(ForgeConfig config, String category, UnitId defaultValue) {
         return ForgeConfig.getUnitId(config, category, this.getName(), defaultValue, this.getComment());
      }
   }
}
