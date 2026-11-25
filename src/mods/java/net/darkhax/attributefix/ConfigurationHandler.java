package net.darkhax.attributefix;

import java.io.File;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigurationHandler {
   public static final Logger log = LogManager.getLogger("AttributeFix");
   public static Configuration config;

   public ConfigurationHandler(File file) {
      config = new Configuration(file);
      if (config.hasCategory("attributes")) {
         log.warn("Outdated AttributeFix config file detected! Please regenerate and reconfigure.");
      }

   }

   public void configureRangedAttribute(RangedAttribute attribute) {
      String name = attribute.getName().replace("generic.", "");
      double oldMin = attribute.minimumValue;
      double oldMax = attribute.maximumValue;
      config.addCustomCategoryComment(name, "Options for the " + name + " attribute.");
      boolean modifyMax = config.getBoolean("modifyMax", name, true, "Should the max value be modified?");
      float maximumValue = config.getFloat("max", name, this.getAdjustedAmount((float)attribute.maximumValue), -3.4028235E38F, Float.MAX_VALUE, "The maximum amount for the " + name + " attribute.");
      boolean modifyMin = config.getBoolean("modifyMin", name, true, "Should the max value be modified?");
      float minimumValue = config.getFloat("min", name, (float)attribute.minimumValue, -3.4028235E38F, Float.MAX_VALUE, "The minimum amount for the " + name + " attribute.");
      if (modifyMax) {
         attribute.maximumValue = (double)maximumValue;
      }

      if (modifyMin) {
         attribute.minimumValue = (double)minimumValue;
      }

      if (attribute.minimumValue > attribute.maximumValue) {
         throw new IllegalArgumentException("The " + name + " attribute was configured incorrectly! Minimum must not be greater than maximum!");
      } else {
         if (modifyMin || modifyMax) {
            log.debug("Modified {}. Min {} -> {} and Max {} -> {}", name, Double.valueOf(oldMin), Double.valueOf(attribute.minimumValue), Double.valueOf(oldMax), Double.valueOf(attribute.maximumValue));
         }

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
