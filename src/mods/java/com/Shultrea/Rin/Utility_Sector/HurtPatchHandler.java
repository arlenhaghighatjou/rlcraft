package com.Shultrea.Rin.Utility_Sector;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
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

public class HurtPatchHandler {
   private static final EaglercraftUUID TWEAKER_UUID = new EaglercraftUUID(0xE6123481134F4C54L, 0xA5379C3A241C7A22L);

   public HurtPatchHandler() {
   }

   public void register(EventBus bus) {
      bus.register(AttackEntityEvent.class, EventPriority.LOWEST, false, this::applyHurtPatch);
      bus.register(CriticalHitEvent.class, EventPriority.HIGHEST, true, this::revertHurtPatch);
   }

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

   public void revertHurtPatch(CriticalHitEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      if (player != null) {
         player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).removeModifier(TWEAKER_UUID);
      }

   }
}
