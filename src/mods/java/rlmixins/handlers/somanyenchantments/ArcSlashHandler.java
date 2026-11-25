package rlmixins.handlers.somanyenchantments;

import bettercombat.mod.event.RLCombatSweepEvent;
import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArcSlashHandler {
   public ArcSlashHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onSweepAttack(RLCombatSweepEvent event) {
      int level = EnchantmentHelper.getEnchantmentLevel(Smc_040.Swiper, event.getItemStack());
      if (level > 0 && event.getEntityPlayer() != null && event.getTargetEntity() != null) {
         event.setDoSweep(true);
         event.setSweepModifier(Math.max(event.getSweepModifier(), (float)level * 0.2F));
      }

   }
}
