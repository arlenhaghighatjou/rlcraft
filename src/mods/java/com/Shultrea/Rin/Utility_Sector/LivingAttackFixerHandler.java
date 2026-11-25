package com.Shultrea.Rin.Utility_Sector;

import com.Shultrea.Rin.Main_Sector.ModConfig;
import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LivingAttackFixerHandler {
   private static final UUID CACHED_UUID = UUID.fromString("e6123481-134f-4c54-a535-29c3a241c7a21");
   private PotionEffect potyon = null;
   int level = 0;
   float addedDamage = 0.0F;
   float negativeValue = 0.0F;
   float TotalDamage = 0.0F;
   float damageHolder = 0.0F;
   float damageHolder2 = 1.0F;
   boolean isTrue = false;

   public LivingAttackFixerHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST,
      receiveCanceled = true
   )
   public void Fix(LivingUpdateEvent event) {
      if (event.getEntityLiving() instanceof EntityPlayer) {
         this.RemoveAttackDamage(event.getEntityLiving());
         if (event.getEntityLiving().isPotionActive(MobEffects.WEAKNESS)) {
            if (ModConfig.miscellaneous.EnableFixEnchantment) {
               Potion potion = MobEffects.WEAKNESS;
               this.potyon = event.getEntityLiving().getActivePotionEffect(potion);
               this.level = this.potyon.getAmplifier() + 1;
               this.AddAttackDamage(event.getEntityLiving());
            }
         } else {
            this.RemoveAttackDamage(event.getEntityLiving());
         }

      }
   }

   private void AddAttackDamage(EntityLivingBase fEntity) {
      IAttributeInstance attackAttr = fEntity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
      AttributeModifier attackDamage = new AttributeModifier(CACHED_UUID, "attackDamage", (double)(this.level * 4), 0);
      attackAttr.removeModifier(attackDamage);
      attackAttr.applyModifier(attackDamage);
   }

   private void RemoveAttackDamage(EntityLivingBase fEntity) {
      IAttributeInstance attackAttr = fEntity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
      if (attackAttr.getModifier(CACHED_UUID) != null) {
         AttributeModifier attackDamage = new AttributeModifier(CACHED_UUID, "attackDamage", (double)(this.level * 4), 0);
         attackAttr.removeModifier(attackDamage);
      }
   }

   public float getDamage() {
      return this.damageHolder;
   }
}
