package com.oblivioussp.spartanweaponry.mixin;

import com.oblivioussp.spartanweaponry.item.IBlockingWeapon;
import com.oblivioussp.spartanweaponry.util.IDamageSourceArmorPiercing;
import com.oblivioussp.spartanweaponry.util.Log;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityLivingBase.class})
public class EntityLivingBaseMixin extends EntityMixin {
   public EntityLivingBaseMixin() {
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"canBlockDamageSource(Lnet/minecraft/util/DamageSource;)Z"},
      cancellable = true
   )
   private void canBlockDamageSource(DamageSource damageSourceIn, CallbackInfoReturnable<Boolean> callback) {
      Entity thisEntity = this.world.getEntityByID(this.getEntityId());
      if (thisEntity instanceof EntityLivingBase) {
         EntityLivingBase thisLiving = (EntityLivingBase)thisEntity;
         if (thisLiving.getActiveItemStack().getItem() instanceof IBlockingWeapon) {
            callback.setReturnValue(Boolean.valueOf(false));
         }
      }

   }

   @Inject(
      at = {@At("HEAD")},
      method = {"applyArmorCalculations(Lnet/minecraft/util/DamageSource;F)F"},
      cancellable = true
   )
   protected void applyArmorCalculations(DamageSource source, float damage, CallbackInfoReturnable<Float> callback) {
      if (source instanceof IDamageSourceArmorPiercing && !source.isUnblockable()) {
         this.damageArmor(damage);
         float percentage = MathHelper.clamp(((IDamageSourceArmorPiercing)source).getPercentage(), 0.0F, 1.0F);
         Log.debug("Found armor piercing damage source! Reducing armor value of target by " + percentage * 100.0F + "%");
         CombatRules.getDamageAfterAbsorb(damage, (float)this.getTotalArmorValue(), (float)this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
         float armorPiercingDamage = damage * percentage;
         float regularDamage = damage - armorPiercingDamage;
         float reducedDamage = CombatRules.getDamageAfterAbsorb(regularDamage, (float)this.getTotalArmorValue(), (float)this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
         float resultDamage = armorPiercingDamage + reducedDamage;
         Log.debug("Full damage: " + damage + " Armor value: " + (float)this.getTotalArmorValue() + " Damage ignoring armor (" + percentage * 100.0F + "% damage): " + armorPiercingDamage + " Damage not ignoring armor: " + regularDamage + " Reduced Damage: " + reducedDamage + " Result Damage: " + resultDamage);
         callback.setReturnValue(Float.valueOf(resultDamage));
      }

   }

   @Shadow
   protected void damageArmor(float damage) {
      throw new IllegalStateException("Mixin failed to shadow the \"EntityLivingBase.damageArmor(float)\" method!");
   }

   @Shadow
   public int getTotalArmorValue() {
      throw new IllegalStateException("Mixin failed to shadow the \"EntityLivingBase.getTotalArmorValue()\" method!");
   }

   @Shadow
   public IAttributeInstance getEntityAttribute(IAttribute attribute) {
      throw new IllegalStateException("Mixin failed to shadow the \"EntityLivingBase.getEntityAttribute(IAttribute)\" method!");
   }
}
