package rlmixins.handlers.somanyenchantments;

import bettercombat.mod.event.RLCombatModifyDamageEvent.Post;
import com.Shultrea.Rin.Enchantments_Sector.Smc_010;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FieryEdgeHandler {
   public FieryEdgeHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL
   )
   public static void modifyAttackDamagePost(Post event) {
      if (event.getEntityPlayer() != null && !event.getStack().isEmpty() && event.getTarget() != null && event.getTarget() instanceof EntityLivingBase) {
         EntityLivingBase target = (EntityLivingBase)event.getTarget();
         int level = EnchantmentHelper.getEnchantmentLevel(Smc_010.FieryEdge, event.getStack());
         if (level > 0) {
            if (target.isBurning() && event.getCooledStrength() > 0.9F && (float)target.hurtResistantTime > (float)target.maxHurtResistantTime / 2.0F && target.world.rand.nextFloat() < 0.05F * (float)level) {
               target.hurtResistantTime = 0;
            }

            target.setFire(level * 6);
         }

      }
   }
}
