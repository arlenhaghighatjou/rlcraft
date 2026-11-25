package com.Shultrea.Rin.Utility_Sector;

import com.Shultrea.Rin.Main_Sector.ModConfig;
import java.util.UUID;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HurtPatchHandler {
   private static final UUID TWEAKER_UUID = UUID.fromString("e6123481-134f-4c54-a535-29c3a241c7a22");

   public HurtPatchHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void applyHurtPatch(AttackEntityEvent event) {
      if (ModConfig.miscellaneous.EnableFixEnchantment) {
         EntityPlayer player = event.getEntityPlayer();
         if (player != null) {
            World world = player.getEntityWorld();
            if (!world.isRemote) {
               Entity target = event.getTarget();
               if (target != null) {
                  if (target.canBeAttackedWithItem() && !target.hitByEntity(player)) {
                     IAttributeInstance attribute = player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                     float sma = (float)attribute.getAttributeValue();
                     float f1;
                     if (target instanceof EntityLivingBase) {
                        f1 = EnchantmentHelper.getModifierForCreature(player.getHeldItemMainhand(), ((EntityLivingBase)target).getCreatureAttribute());
                     } else {
                        f1 = EnchantmentHelper.getModifierForCreature(player.getHeldItemMainhand(), EnumCreatureAttribute.UNDEFINED);
                     }

                     float f2 = player.getCooledAttackStrength(0.5F);
                     float f = sma * (0.2F + f2 * f2 * 0.8F);
                     f1 = f1 * f2;
                     if (f > 0.0F || f1 > 0.0F) {
                        return;
                     }

                     double operationZeroAmount = 0.0D;

                     for(AttributeModifier mod : attribute.getModifiers()) {
                        if (mod.getOperation() == 0) {
                           operationZeroAmount += mod.getAmount();
                        }
                     }

                     double newMod = 0.01D - operationZeroAmount;
                     attribute.removeModifier(TWEAKER_UUID);
                     attribute.applyModifier(new AttributeModifier(TWEAKER_UUID, "SMEHurtPatch", newMod, 0));
                  }

               }
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST,
      receiveCanceled = true
   )
   public void revertHurtPatch(CriticalHitEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      if (player != null) {
         player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(TWEAKER_UUID);
      }

   }
}
