package com.charles445.simpledifficulty.compat;

import java.util.function.Supplier;
import com.charles445.simpledifficulty.compat.mod.SereneSeasonsModifier;
import com.charles445.simpledifficulty.compat.mod.BaublesModifier;
import com.charles445.simpledifficulty.compat.mod.AUWModifier;
import com.charles445.simpledifficulty.compat.mod.AUWDynamicModifier;
import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.temperature.ITemperatureDynamicModifier;
import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.charles445.simpledifficulty.util.CompatUtil;
import javax.annotation.Nullable;

public class CompatController {

   public CompatController() {
   }

   public static void setupCommonPostInit() {
      Object auwDynamicModifier = newCompatObject("armorunder", AUWDynamicModifier::new);
      Object auwModifier = newCompatObject("armorunder", AUWModifier::new);
      Object baublesModifier = newCompatObject("baubles", BaublesModifier::new);
      Object sereneSeasonsModifier = newCompatObject("sereneseasons", SereneSeasonsModifier::new);
      if (auwDynamicModifier instanceof ITemperatureDynamicModifier && auwModifier instanceof ITemperatureModifier) {
         SimpleDifficulty.logger.info("Armor Underwear Modifiers Enabled");
         TemperatureRegistry.registerDynamicModifier((ITemperatureDynamicModifier)auwDynamicModifier);
         TemperatureRegistry.registerModifier((ITemperatureModifier)auwModifier);
      }

      if (baublesModifier instanceof ITemperatureModifier) {
         SimpleDifficulty.logger.info("Baubles Modifier Enabled");
         TemperatureRegistry.registerModifier((ITemperatureModifier)baublesModifier);
      }

      if (sereneSeasonsModifier instanceof ITemperatureModifier) {
         SimpleDifficulty.logger.info("Serene Seasons Modifier Enabled");
         TemperatureRegistry.registerModifier((ITemperatureModifier)sereneSeasonsModifier);
      }

   }

   public static void setupClient() {
   }

   @Nullable
   public static Object newCompatObject(String modid, Supplier<Object> factory) {
      return CompatUtil.canUseMod(modid) ? factory.get() : null;
   }
}
