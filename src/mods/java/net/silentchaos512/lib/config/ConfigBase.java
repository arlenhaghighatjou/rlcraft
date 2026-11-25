package net.silentchaos512.lib.config;

import com.google.common.primitives.UnsignedInts;
import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.lib.registry.IPhasedInitializer;
import net.silentchaos512.lib.registry.SRegistry;

public abstract class ConfigBase implements IPhasedInitializer {
   public static final String SEP = ".";
   public static final String CAT_MAIN = "main";
   protected Configuration config;
   protected final String modId;

   public ConfigBase(String modId) {
      this.modId = modId;
      MinecraftForge.EVENT_BUS.register(this);
   }

   public abstract void load();

   public int loadInt(String key, String category, int defaultValue, String comment) {
      Property prop = this.config.get(category, key, defaultValue);
      prop.setComment(comment);
      return prop.getInt(defaultValue);
   }

   public int loadInt(String key, String category, int defaultValue, int min, int max, String comment) {
      Property prop = this.config.get(category, key, defaultValue);
      prop.setComment(comment + " [range: " + min + " ~ " + max + ", default: " + defaultValue + "]");
      prop.setMinValue(min);
      prop.setMaxValue(max);
      int val = prop.getInt(defaultValue);
      return val < min ? min : (val > max ? max : val);
   }

   public long loadLong(String key, String category, long defaultValue, String comment) {
      Property prop = this.config.get(category, key, (double)defaultValue);
      prop.setComment(comment);
      return prop.getLong(defaultValue);
   }

   public long loadLong(String key, String category, long defaultValue, long min, long max, String comment) {
      Property prop = this.config.get(category, key, (double)defaultValue);
      prop.setComment(comment + " [range: " + min + " ~ " + max + ", default: " + defaultValue + "]");
      prop.setMinValue((double)min);
      prop.setMaxValue((double)max);
      long val = prop.getLong(defaultValue);
      return val < min ? min : (val > max ? max : val);
   }

   public double loadDouble(String key, String category, double defaultValue, String comment) {
      Property prop = this.config.get(category, key, Double.toString(defaultValue));
      prop.setComment(comment);
      double val = 0.0D;

      try {
         val = Double.parseDouble(prop.getString());
      } catch (Exception var10) {
         var10.printStackTrace();
      }

      return val;
   }

   public double loadDouble(String key, String category, double defaultValue, double min, double max, String comment) {
      Property prop = this.config.get(category, key, Double.toString(defaultValue));
      prop.setComment(comment + " [range: " + min + " ~ " + max + ", default: " + defaultValue + "]");
      prop.setMinValue(min);
      prop.setMaxValue(max);
      double val = 0.0D;

      try {
         val = Double.parseDouble(prop.getString());
      } catch (Exception var14) {
         var14.printStackTrace();
      }

      return val < min ? min : (val > max ? max : val);
   }

   public float loadFloat(String key, String category, float defaultValue, String comment) {
      return (float)this.loadDouble(key, category, (double)defaultValue, comment);
   }

   public float loadFloat(String key, String category, float defaultValue, float min, float max, String comment) {
      return (float)this.loadDouble(key, category, (double)defaultValue, (double)min, (double)max, comment);
   }

   public boolean loadBoolean(String key, String category, boolean defaultValue, String comment) {
      Property prop = this.config.get(category, key, defaultValue);
      prop.setComment(comment);
      return prop.getBoolean();
   }

   public int loadColorCode(String key, String category, int defaultValue, boolean includeAlpha, String comment) throws NumberFormatException {
      String str = this.config.getString(key, category, String.format(includeAlpha ? "%08x" : "%06x", defaultValue), comment);
      int result = UnsignedInts.parseUnsignedInt(str, 16);
      if (includeAlpha && (result & -16777216) == 0) {
         result |= -16777216;
      }

      return result;
   }

   public <T extends Enum<T>> T loadEnum(String key, String category, Class<T> enumClass, T defaultValue, String comment) {
      return (T)this.loadEnum(key, category, defaultValue, comment, true, EnumSet.allOf(enumClass));
   }

   public <T extends Enum<T>> T loadEnum(String key, String category, Class<T> enumClass, T defaultValue, String comment, boolean appendValidValuesToComment) {
      return (T)this.loadEnum(key, category, defaultValue, comment, appendValidValuesToComment, EnumSet.allOf(enumClass));
   }

   public <T extends Enum<T>> T loadEnum(String key, String category, T defaultValue, String comment, boolean appendValidValuesToComment, Set<T> allowedValues) {
      String[] validValues = (String[])allowedValues.stream().map(Enum::name).toArray((x$0) -> {
         return (T)(new String[x$0]);
      });
      if (appendValidValuesToComment) {
         comment = comment + "\nValid values: " + Arrays.toString(validValues);
      }

      String value = this.config.getString(key, category, defaultValue.name(), comment, validValues);

      for(T t : allowedValues) {
         if (t.name().equalsIgnoreCase(value)) {
            return t;
         }
      }

      return defaultValue;
   }

   public void init(File file) {
      this.config = new Configuration(file);
      this.load();
   }

   public void save() {
      if (this.config.hasChanged()) {
         this.config.save();
      }

   }

   @SubscribeEvent
   public void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equalsIgnoreCase(this.modId)) {
         this.load();
         this.save();
      }

   }

   public ConfigCategory getCategory(String category) {
      return this.config.getCategory(category);
   }

   /** @deprecated */
   @Deprecated
   public List<IConfigElement> getConfigElements() {
      return (new ConfigElement(this.getCategory("main"))).getChildElements();
   }

   public Configuration getConfiguration() {
      return this.config;
   }

   public void preInit(SRegistry registry, FMLPreInitializationEvent event) {
      this.init(event.getSuggestedConfigurationFile());
   }

   public void init(SRegistry registry, FMLInitializationEvent event) {
      this.save();
   }

   public void postInit(SRegistry registry, FMLPostInitializationEvent event) {
   }
}
