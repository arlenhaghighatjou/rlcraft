package rlmixins.handlers.somanyenchantments;

import bettercombat.mod.event.RLCombatModifyDamageEvent.Post;
import com.Shultrea.Rin.Enchantments_Sector.Smc_010;
import com.oblivioussp.spartanweaponry.util.EntityDamageSourceArmorPiercing;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RunePiercingCapabilitiesHandler {
   public RunePiercingCapabilitiesHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void modifyAttackDamagePost(Post event) {
      EntityPlayer player = event.getEntityPlayer();
      Entity target = event.getTarget();
      if (player != null && target != null && !event.getStack().isEmpty()) {
         int pierceLevel = EnchantmentHelper.getEnchantmentLevel(Smc_010.Rune_PiercingCapabilities, event.getStack());
         if (pierceLevel > 0) {
            float piercePercent = (float)pierceLevel / 4.0F;
            if (event.getDamageSource() instanceof EntityDamageSourceArmorPiercing) {
               piercePercent += ((EntityDamageSourceArmorPiercing)event.getDamageSource()).getPercentage();
            }

            event.setDamageSource(new EntityDamageSourceArmorPiercing("player", player, Math.min(piercePercent, 1.0F)));
         }

      }
   }
}
