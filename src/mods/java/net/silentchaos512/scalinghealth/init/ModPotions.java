package net.silentchaos512.scalinghealth.init;

import net.silentchaos512.lib.registry.SRegistry;
import net.silentchaos512.scalinghealth.potion.PotionBandaged;

public class ModPotions {
   public static final PotionBandaged bandaged = new PotionBandaged();

   public ModPotions() {
   }

   public static void registerAll(SRegistry reg) {
      reg.registerPotion(bandaged, "bandaged");
   }
}
