package com.endertech.minecraft.forge.configs;

import com.endertech.common.Args;
import com.endertech.common.CommonMath;
import com.endertech.common.CommonTime;
import com.endertech.common.FloatBounds;
import com.endertech.common.IntBounds;
import com.endertech.common.KeyValuePair;
import com.endertech.minecraft.forge.ForgeMain;
import com.endertech.minecraft.forge.data.Names;
import com.endertech.minecraft.forge.math.Percentage;
import com.endertech.minecraft.forge.units.UnitId;
import java.io.File;
import java.nio.file.Path;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfigEntries.NumberSliderEntry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

public class ForgeConfig extends Configuration {
   public static final String EXTENSION = ".cfg";
   public static final String KEY_LIST = "list";

   public ForgeConfig(File file) {
      super(file, true);
      this.load();
   }

   protected Logger getLogger() {
      return ForgeMain.instance.getLogger();
   }

   public static boolean readConfigEnabled(ForgeConfig config, boolean defaultValue) {
      boolean enabled = getBool(config, "Config", "enabled", defaultValue, "If set to False, parameters from this config will not be used.");
      return enabled;
   }

   public static String getDescription(String param, String description, boolean addCR) {
      return param + " " + description + (addCR ? "\n" : "");
   }

   public static String getSyntax(Object... args) {
      return "Syntax: " + Args.join(args);
   }

   public static boolean getBool(ForgeConfig config, String category, String key, boolean defaultValue, String comment) {
      return config != null ? config.getBool(category, key, defaultValue, comment) : defaultValue;
   }

   public static int getInt(ForgeConfig config, String category, String key, int defaultValue, IntBounds bounds, String comment) {
      return config != null ? config.getInt(category, key, defaultValue, bounds, comment) : CommonMath.Range.enclose(defaultValue, bounds);
   }

   public static IntBounds getIntBounds(ForgeConfig config, String category, String caption, IntBounds defaultValue, IntBounds bounds, String comment) {
      return config != null ? config.getIntBounds(category, caption, defaultValue, bounds, comment) : defaultValue.fit(bounds);
   }

   public static float getFloat(ForgeConfig config, String category, String key, float defaultValue, FloatBounds bounds, String comment) {
      return config != null ? config.getFloat(category, key, defaultValue, bounds, comment) : CommonMath.Range.enclose(defaultValue, bounds);
   }

   public static FloatBounds getFloatBounds(ForgeConfig config, String category, String caption, FloatBounds defaultValue, FloatBounds bounds, String comment) {
      return config != null ? config.getFloatBounds(category, caption, defaultValue, bounds, comment) : defaultValue.fit(bounds);
   }

   public static Percentage getPercentage(ForgeConfig config, String category, String key, Percentage defaultValue, FloatBounds bounds, String comment) {
      if (config != null) {
         return config.getPercentage(category, key, defaultValue, bounds, comment);
      } else {
         float value = bounds.enclose(Float.valueOf(defaultValue.getValue())).floatValue();
         return new Percentage(value);
      }
   }

   public static String getStr(ForgeConfig config, String category, String key, String defaultValue, String comment) {
      return config != null ? config.getStr(category, key, defaultValue, comment) : defaultValue;
   }

   public static ColorARGB getColorARGB(ForgeConfig config, String category, String key, ColorARGB defaultValue, String comment) {
      return config != null ? config.getColorARGB(category, key, defaultValue, comment) : defaultValue;
   }

   public static KeyValuePair getKeyValuePair(ForgeConfig config, String category, String key, KeyValuePair defaultValue, String comment) {
      return config != null ? config.getKeyValuePair(category, key, defaultValue, comment) : defaultValue;
   }

   public static UnitId getUnitId(ForgeConfig config, String category, String key, UnitId defaultValue, String comment) {
      return config != null ? config.getUnitId(category, key, defaultValue, comment) : defaultValue;
   }

   public static CommonTime.Interval getTimeInSeconds(ForgeConfig config, String category, String key, CommonTime.Interval defaultValue, String comment) {
      return config != null ? config.getTimeInSeconds(category, key, defaultValue, comment) : defaultValue;
   }

   public static CommonTime.Interval getTimeInMillis(ForgeConfig config, String category, String key, CommonTime.Interval defaultValue, String comment) {
      return config != null ? config.getTimeInMillis(category, key, defaultValue, comment) : defaultValue;
   }

   public static String[] getStrArray(ForgeConfig config, String category, String key, String[] defaultValues, String comment) {
      return config != null ? config.getStrArray(category, key, defaultValues, comment) : defaultValues;
   }

   public static void save(ForgeConfig config) {
      if (config != null && config.hasChanged()) {
         config.save();
      }

   }

   public boolean getBool(String category, String key, boolean defaultValue, String comment) {
      try {
         return this.getBoolean(key, category, defaultValue, comment);
      } catch (Exception var6) {
         this.getLogger().error(var6.toString());
         return defaultValue;
      }
   }

   /** @deprecated */
   @Deprecated
   public Property getProp(String category, String key, int defaultValue, IntBounds bounds, String comment) {
      Property prop = this.get(category, key, defaultValue, comment, bounds.getMin().intValue(), bounds.getMax().intValue());
      if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
         prop.setConfigEntryClass(NumberSliderEntry.class);
      }

      return prop;
   }

   public int getInt(String category, String key, int defaultValue, IntBounds bounds, String comment) {
      try {
         return this.getInt(key, category, defaultValue, bounds.getMin().intValue(), bounds.getMax().intValue(), comment);
      } catch (Exception var7) {
         this.getLogger().error(var7.toString());
         return CommonMath.Range.enclose(defaultValue, bounds);
      }
   }

   public IntBounds getIntBounds(String category, String caption, IntBounds defaultValue, IntBounds bounds, String comment) {
      category = Names.dotted().join(category, caption);
      this.setCategoryComment(category, comment);
      int min = this.getInt(category, "min", defaultValue.min.intValue(), bounds, "");
      int max = this.getInt(category, "max", defaultValue.max.intValue(), bounds, "");
      return IntBounds.from(Integer.valueOf(min), Integer.valueOf(max));
   }

   public float getFloat(String category, String key, float defaultValue, FloatBounds bounds, String comment) {
      try {
         return this.getFloat(key, category, defaultValue, bounds.getMin().floatValue(), bounds.getMax().floatValue(), comment);
      } catch (Exception var7) {
         this.getLogger().error(var7.toString());
         return CommonMath.Range.enclose(defaultValue, bounds);
      }
   }

   public FloatBounds getFloatBounds(String category, String caption, FloatBounds defaultValue, FloatBounds bounds, String comment) {
      category = Names.dotted().join(category, caption);
      this.setCategoryComment(category, comment);
      float min = this.getFloat(category, "min", defaultValue.min.floatValue(), bounds, "");
      float max = this.getFloat(category, "max", defaultValue.max.floatValue(), bounds, "");
      return FloatBounds.from(Float.valueOf(min), Float.valueOf(max));
   }

   public Percentage getPercentage(String category, String key, Percentage defaultValue, FloatBounds bounds, String comment) {
      float value = this.getFloat(category, key, defaultValue.getValue(), bounds, comment);
      return new Percentage(value);
   }

   public String getStr(String category, String key, String defaultValue, String comment) {
      try {
         return this.getString(key, category, defaultValue, comment);
      } catch (Exception var6) {
         this.getLogger().error(var6.toString());
         return defaultValue;
      }
   }

   public ColorARGB getColorARGB(String category, String key, ColorARGB defaultValue, String comment) {
      String hexARGB = this.getStr(category, key, defaultValue.getHexARGB(), comment);
      return ColorARGB.from(hexARGB);
   }

   public KeyValuePair getKeyValuePair(String category, String key, KeyValuePair defaultValue, String comment) {
      String str = this.getStr(category, key, defaultValue.toString(), comment);
      return KeyValuePair.from(str);
   }

   public UnitId getUnitId(String category, String key, UnitId defaultValue, String comment) {
      String stringId = this.getStr(category, key, defaultValue.toString(), comment);
      return UnitId.from(stringId);
   }

   public CommonTime.Interval getTimeInSeconds(String category, String key, CommonTime.Interval defaultValue, String comment) {
      float seconds = this.getFloat(category, key, (float)defaultValue.inSeconds(), FloatBounds.from(Float.valueOf(0.0F), Float.valueOf(86400.0F)), comment + " [in seconds]");
      return CommonTime.Interval.seconds((double)seconds);
   }

   public CommonTime.Interval getTimeInMillis(String category, String key, CommonTime.Interval defaultValue, String comment) {
      int milliseconds = this.getInt(category, key, (int)defaultValue.inMillis(), IntBounds.INTEGER_POSITIVE, comment + " [in milliseconds]");
      return CommonTime.Interval.millis((long)milliseconds);
   }

   public String[] getStrArray(String category, String key, String[] defaultValues, String comment) {
      try {
         return this.getStringList(key, category, defaultValues, comment);
      } catch (Exception var6) {
         this.getLogger().error(var6.toString());
         return defaultValues;
      }
   }

   public Path getConfigDir() {
      return this.getConfigFile().getParentFile().toPath();
   }

   public void save() {
      if (this.hasChanged()) {
         super.save();
      }

   }
}
