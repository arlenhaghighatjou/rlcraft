package net.silentchaos512.lib.config;

import java.util.function.Consumer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.silentchaos512.lib.util.LogHelper;

public class IntRangeConfig {
   private int min;
   private int max;
   private final int defaultMin;
   private final int defaultMax;
   private final int lowerLimit;
   private final int upperLimit;

   public IntRangeConfig(int defaultMin, int defaultMax, int lowerLimit, int upperLimit) {
      this.defaultMin = defaultMin;
      this.defaultMax = defaultMax;
      this.lowerLimit = lowerLimit;
      this.upperLimit = upperLimit;
   }

   public void loadConfig(ConfigBase config, String name, String category, String comment) {
      this.min = config.loadInt(name + " Min", category, this.defaultMin, "Minimum: " + comment);
      this.max = config.loadInt(name + " Max", category, this.defaultMax, "Maximum: " + comment);
      this.min = MathHelper.clamp(this.min, this.lowerLimit, this.upperLimit);
      this.max = MathHelper.clamp(this.max, this.lowerLimit, this.upperLimit);
      if (this.max < this.min) {
         ModContainer modContainer = Loader.instance().activeModContainer();
         if (modContainer != null) {
            LogHelper.getRegisteredLogger(modContainer.getName()).ifPresent((log) -> {
               log.warn("Config '{}' (category {}): max ({}) is less than min ({})!", name, category, this.max, this.min);
            });
         }

         this.max = this.min;
      }

   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }
}
