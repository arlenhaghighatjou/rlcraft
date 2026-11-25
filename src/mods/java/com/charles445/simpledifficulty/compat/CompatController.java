package com.charles445.simpledifficulty.compat;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.temperature.ITemperatureDynamicModifier;
import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.charles445.simpledifficulty.util.CompatUtil;
import javax.annotation.Nullable;

public class CompatController {
   private static final String compatMod = "com.charles445.simpledifficulty.compat.mod.";

   public CompatController() {
   }

   public static void setupCommonPostInit() {
      Object auwDynamicModifier = newCompatObject("armorunder", "com.charles445.simpledifficulty.compat.mod.AUWDynamicModifier");
      Object auwModifier = newCompatObject("armorunder", "com.charles445.simpledifficulty.compat.mod.AUWModifier");
      Object baublesModifier = newCompatObject("baubles", "com.charles445.simpledifficulty.compat.mod.BaublesModifier");
      Object firstAidCompat = newCompatObject("firstaid", "com.charles445.simpledifficulty.compat.mod.FirstAidCompat");
      Object harvestFestivalModifier = newCompatObject("harvestfestival", "com.charles445.simpledifficulty.compat.mod.HarvestFestivalModifier");
      Object inspirationsHandler = newCompatObject("inspirations", "com.charles445.simpledifficulty.compat.mod.InspirationsHandler");
      Object oreExcavationHandler = newCompatObject("oreexcavation", "com.charles445.simpledifficulty.compat.mod.OreExcavationHandler");
      Object sereneSeasonsModifier = newCompatObject("sereneseasons", "com.charles445.simpledifficulty.compat.mod.SereneSeasonsModifier");
      if (auwDynamicModifier instanceof ITemperatureDynamicModifier && auwModifier instanceof ITemperatureModifier) {
         SimpleDifficulty.logger.info("Armor Underwear Modifiers Enabled");
         TemperatureRegistry.registerDynamicModifier((ITemperatureDynamicModifier)auwDynamicModifier);
         TemperatureRegistry.registerModifier((ITemperatureModifier)auwModifier);
      }

      if (baublesModifier instanceof ITemperatureModifier) {
         SimpleDifficulty.logger.info("Baubles Modifier Enabled");
         TemperatureRegistry.registerModifier((ITemperatureModifier)baublesModifier);
      }

      if (harvestFestivalModifier instanceof ITemperatureModifier) {
         SimpleDifficulty.logger.info("Harvest Festival Modifier Enabled");
         TemperatureRegistry.registerModifier((ITemperatureModifier)harvestFestivalModifier);
      }

      if (inspirationsHandler != null) {
         SimpleDifficulty.logger.info("Inspirations Handler Enabled");
      }

      if (oreExcavationHandler != null) {
         SimpleDifficulty.logger.info("OreExcavation Handler Enabled");
      }

      if (sereneSeasonsModifier instanceof ITemperatureModifier) {
         SimpleDifficulty.logger.info("Serene Seasons Modifier Enabled");
         TemperatureRegistry.registerModifier((ITemperatureModifier)sereneSeasonsModifier);
      }

   }

   public static void setupClient() {
   }

   @Nullable
   public static Object newCompatObject(String modid, String clazzpath) {
      if (CompatUtil.canUseMod(modid)) {
         try {
            Object o = Class.forName(clazzpath).newInstance();
            return o;
         } catch (Exception var3) {
            SimpleDifficulty.logger.error("Mod " + modid + " was loaded but object " + clazzpath + " was not accessible!", var3);
         }
      }

      return null;
   }
}
