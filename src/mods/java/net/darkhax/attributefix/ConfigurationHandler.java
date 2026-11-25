package net.darkhax.attributefix;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.common.config.Configuration;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class ConfigurationHandler {
   public static final Logger log = LogManager.getLogger("AttributeFix");
   public static Configuration config;

   public ConfigurationHandler(VFile2 file) {
      config = new Configuration(file);
      if (config.hasCategory("attributes")) {
         log.warn("Outdated AttributeFix config file detected! Please regenerate and reconfigure.");
      }

   }

   public void configureRangedAttribute(RangedAttribute attribute) {
      String name = attribute.getName().replace("generic.", "");
      double oldMin = attribute.getMinimumValue();
      double oldMax = attribute.getMaximumValue();
      config.addCustomCategoryComment(name, "Options for the " + name + " attribute.");
      boolean modifyMax = config.getBoolean("modifyMax", name, true, "Should the max value be modified?");
      float maximumValue = config.getFloat("max", name, this.getAdjustedAmount((float)oldMax), -Float.MAX_VALUE, Float.MAX_VALUE, "The maximum amount for the " + name + " attribute.");
      boolean modifyMin = config.getBoolean("modifyMin", name, true, "Should the max value be modified?");
      float minimumValue = config.getFloat("min", name, (float)oldMin, -Float.MAX_VALUE, Float.MAX_VALUE, "The minimum amount for the " + name + " attribute.");
      if (modifyMin || modifyMax) {
         double min = modifyMin ? minimumValue : oldMin;
         double max = modifyMax ? maximumValue : oldMax;
         if (min > max) {
            throw new IllegalArgumentException("The " + name + " attribute was configured incorrectly! Minimum must not be greater than maximum!");
         }
         attribute.setRange(min, max);
         log.debug("Modified {}. Min {} -> {} and Max {} -> {}", name, oldMin, min, oldMax, max);
      }
   }

   private float getAdjustedAmount(float amount) {
      return Math.max(amount, 65536.0F);
   }

   public void syncConfigData() {
      if (config.hasChanged()) {
         config.save();
      }

   }
}
