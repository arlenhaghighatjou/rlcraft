package net.silentchaos512.lib.config;

import net.lax1dude.eaglercraft.v1_8.HString;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public abstract class AdaptiveConfig extends ConfigBase {
   public static final String CAT_LAST_VERSION = "zzlastversion";
   @Nonnull
   protected AdaptiveConfig.Adaptor adaptor;
   protected boolean adaptorEnabled;
   protected int currentBuild;

   public AdaptiveConfig(String modId, boolean adaptorEnabled, int currentBuild) {
      super(modId);
      this.adaptorEnabled = adaptorEnabled;
      this.currentBuild = currentBuild;
   }

   public void init(VFile2 file) {
      this.config = new Configuration(file);
      int lastBuild = this.config.get("zzlastversion", "last_build", this.currentBuild).getInt(this.currentBuild);
      this.adaptor = new AdaptiveConfig.Adaptor(this.adaptorEnabled, lastBuild, this.currentBuild);
      this.load();
   }

   public void save() {
      this.config.get("zzlastversion", "last_build", this.currentBuild).setValue(this.currentBuild);
      super.save();
   }

   public int loadInt(String key, String category, int defaultValue, String comment) {
      Property prop = this.config.get(category, key, defaultValue);
      prop.setComment(comment);
      this.adaptor.adaptProperty(prop, Integer.valueOf(prop.getInt(defaultValue)));
      return prop.getInt(defaultValue);
   }

   public int loadInt(String key, String category, int defaultValue, int min, int max, String comment) {
      Property prop = this.config.get(category, key, defaultValue);
      if (comment != null && !comment.isEmpty()) {
         prop.setComment(comment + " [range: " + min + " ~ " + max + ", default: " + defaultValue + "]");
      }

      prop.setMinValue(min);
      prop.setMaxValue(max);
      this.adaptor.adaptProperty(prop, Integer.valueOf(prop.getInt(defaultValue)));
      int val = prop.getInt(defaultValue);
      return val < min ? min : (val > max ? max : val);
   }

   public double loadDouble(String key, String category, double defaultValue, String comment) {
      Property prop = this.config.get(category, key, defaultValue);
      prop.setComment(comment);
      this.adaptor.adaptProperty(prop, Double.valueOf(prop.getDouble(defaultValue)));
      return prop.getDouble();
   }

   public double loadDouble(String key, String category, double defaultValue, double min, double max, String comment) {
      String defaultString = this.formatDouble(defaultValue);
      Property prop = this.config.get(category, key, defaultString);
      if (comment != null && !comment.isEmpty()) {
         prop.setComment(comment + " [range: " + min + " ~ " + max + ", default: " + defaultString + "]");
      }

      prop.setMinValue(min);
      prop.setMaxValue(max);
      this.adaptor.adaptProperty(prop, prop.getString());
      double val = 0.0D;

      try {
         val = Double.parseDouble(prop.getString());
      } catch (Exception var15) {
         var15.printStackTrace();
      }

      return val < min ? min : (val > max ? max : val);
   }

   protected String formatDouble(double val) {
      return HString.format("%.4f", val).replaceFirst("0+$", "").replaceFirst("\\.$", ".0");
   }

   public <T> void addAdaptorMapping(int version, String key, T val) {
      if (!(val instanceof Float) && !(val instanceof Double)) {
         this.adaptor.addMapping(version, key, val);
      } else {
         this.adaptor.addMapping(version, key, this.formatDouble(((Double)val).doubleValue()));
      }

   }

   public static class AdaptableValue<T> {
      public final int version;
      public final T value;
      public final Class<? extends T> valueType;

      public AdaptableValue(int version, T value) {
         this.version = version;
         this.value = value;
         this.valueType = (Class<? extends T>)value.getClass();
      }
   }

   public static class Adaptor {
      private boolean enabled;
      private int lastBuild;
      private int currentBuild;
      private final Map<String, List<AdaptiveConfig.AdaptableValue>> adaptableValues = Maps.newHashMap();
      private final List<String> changes = Lists.newArrayList();

      public Adaptor(boolean enabled, int lastBuild, int currentBuild) {
         this.enabled = enabled;
         this.lastBuild = lastBuild;
         this.currentBuild = currentBuild;
      }

      public <T> void adaptProperty(Property prop, T val) {
         if (this.enabled) {
            String name = prop.getName();
            if (this.adaptableValues.containsKey(name)) {
               AdaptiveConfig.AdaptableValue<T> bestValue = null;

               for(AdaptiveConfig.AdaptableValue<T> value : (List<AdaptiveConfig.AdaptableValue<T>>)(List)this.adaptableValues.get(name)) {
                  if (value.version < this.lastBuild && (bestValue == null || value.version > bestValue.version)) {
                     bestValue = value;
                  }
               }

               if (bestValue != null) {
                  T expected = bestValue.value;
                  T def = (T)prop.getDefault();
                  if (this.areEqualNumbers(val, expected) && !this.areEqualNumbers(val, def)) {
                     prop.setValue(def.toString());
                     this.changes.add(" " + prop.getName() + ": " + val + " -> " + def);
                  }
               }

            }
         }
      }

      public <T> void addMapping(int version, String key, T val) {
         if (this.enabled) {
            AdaptiveConfig.AdaptableValue<T> adapt = new AdaptiveConfig.AdaptableValue<T>(version, val);
            if (!this.adaptableValues.containsKey(key)) {
               this.adaptableValues.put(key, new ArrayList());
            }

            ((List)this.adaptableValues.get(key)).add(adapt);
         }
      }

      public boolean areEqualNumbers(Object v1, Object v2) {
         double epsilon = 1.0E-6D;
         float v1f = ((Number)v1).floatValue();
         float v2f;
         if (v2 instanceof String) {
            v2f = Float.parseFloat((String)v2);
         } else {
            v2f = ((Number)v2).floatValue();
         }

         return (double)Math.abs(v1f - v2f) < epsilon;
      }
   }
}
