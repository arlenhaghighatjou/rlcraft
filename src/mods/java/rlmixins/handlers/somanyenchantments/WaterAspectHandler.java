package rlmixins.handlers.somanyenchantments;

import bettercombat.mod.event.RLCombatModifyDamageEvent.Pre;
import com.Shultrea.Rin.Enchantments_Sector.Smc_010;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WaterAspectHandler {
   public WaterAspectHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL
   )
   public static void modifyAttackDamagePre(Pre event) {
      if (event.getEntityPlayer() != null && !event.getStack().isEmpty() && event.getTarget() != null && event.getTarget() instanceof EntityLivingBase) {
         EntityLivingBase target = (EntityLivingBase)event.getTarget();
         int level = EnchantmentHelper.getEnchantmentLevel(Smc_010.WaterAspect, event.getStack());
         if (level > 0) {
            if (target instanceof EntityEnderman || target instanceof EntityBlaze || target instanceof EntityMagmaCube) {
               event.setDamageModifier(event.getDamageModifier() + (float)level * 1.5F);
            }

            if (target.isInWater() || target.isWet()) {
               event.setDamageModifier(event.getDamageModifier() + (float)level * 0.75F);
            }

            if (event.getEntityPlayer().isInWater() || event.getEntityPlayer().isWet()) {
               event.setDamageModifier(event.getDamageModifier() + (float)level * 0.75F);
            }
         }

      }
   }
}
