package com.oblivioussp.spartanweaponry.api.weaponproperty;

import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import com.oblivioussp.spartanweaponry.util.Log;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class WeaponPropertyExtraDamage extends WeaponPropertyWithCallback {
   public static final String NBT_RIDING_SPEED = "spartanweaponry_RidingSpeed";

   public WeaponPropertyExtraDamage(String propType, String propModId, float propMagnitude) {
      super(propType, propModId, propMagnitude);
   }

   public WeaponProperty.PropertyQuality getQuality() {
      return this.type == "extra_damage_thrown" ? WeaponProperty.PropertyQuality.NEUTRAL : WeaponProperty.PropertyQuality.POSITIVE;
   }

   protected void addTooltipDescription(ItemStack stack, List<String> tooltip) {
      if (!ConfigHandler.damageBonusCheckArmorValue || this.type != "extra_damage_chest" && this.type != "extra_damage_unarmoured") {
         tooltip.add(TextFormatting.GRAY + "" + TextFormatting.ITALIC + "  " + SpartanWeaponryAPI.internalHandler.translateFormattedString(this.type + ".desc", "tooltip", this.modId, MathHelper.floor((this.magnitude - 1.0F) * 100.0F)));
      } else {
         tooltip.add(TextFormatting.GRAY + "" + TextFormatting.ITALIC + "  " + SpartanWeaponryAPI.internalHandler.translateFormattedString(this.type + ".desc.armor_points", "tooltip", this.modId, MathHelper.floor((this.magnitude - 1.0F) * 100.0F), ConfigHandler.damageBonusMaxArmorValue));
      }

   }

   public float modifyDamageDealt(ToolMaterialEx material, float baseDamage, float initialDamage, DamageSource source, EntityLivingBase attacker, EntityLivingBase victim) {
      if (attacker != null && victim != null) {
         float bonusDamage = (this.getMagnitude() - 1.0F) * Math.min(baseDamage, initialDamage);
         String var8 = this.type;
         byte var9 = -1;
         switch(var8.hashCode()) {
         case -1642908160:
            if (var8.equals("extra_damage_chest")) {
               var9 = 0;
            }
            break;
         case 674846217:
            if (var8.equals("extra_damage_unarmoured")) {
               var9 = 1;
            }
            break;
         case 1039775894:
            if (var8.equals("extra_damage_riding")) {
               var9 = 2;
            }
            break;
         case 1130276702:
            if (var8.equals("extra_damage_undead")) {
               var9 = 3;
            }
            break;
         case 2140347466:
            if (var8.equals("extra_damage_backstab")) {
               var9 = 4;
            }
         }

         switch(var9) {
         case 0: {
               if (victim.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty()) {
                  return ConfigHandler.damageBonusCheckArmorValue && victim.getEntityAttribute(SharedMonsterAttributes.ARMOR).getBaseValue() > (double)ConfigHandler.damageBonusMaxArmorValue ? baseDamage : baseDamage + bonusDamage;
               }
         }
         case 1: {
               break;
         }
         case 2: {
               if (!attacker.isRiding()) {
                  return baseDamage;
               }

               if (ConfigHandler.damageBonusRidingCheckSpeed) {
                  if (!attacker.getEntityData().hasKey("spartanweaponry_RidingSpeed")) {
                     Log.error("Cannot retrieve speed value for player riding attack bonus! Reverting to old behavior!");
                  } else {
                     float velocity = attacker.getEntityData().getFloat("spartanweaponry_RidingSpeed");
                     attacker.getEntityData().removeTag("spartanweaponry_RidingSpeed");
                     bonusDamage *= MathHelper.clamp(velocity / ConfigHandler.damageBonusRidingSpeedMax, 0.0F, 1.0F);
                     Log.info("velocity = " + velocity + " bonus damage = " + bonusDamage + " total damage = " + (baseDamage + bonusDamage));
                  }
               }

               return attacker.isRiding() ? baseDamage + bonusDamage : baseDamage;
         }
         case 3: {
               boolean doBonusDamage = victim.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD;
               return doBonusDamage ? baseDamage + bonusDamage : baseDamage;
         }
         case 4: {
               Entity immediateEntity = source.getImmediateSource();
               float yaw = source.isProjectile() ? -immediateEntity.rotationYaw : immediateEntity.rotationYaw;
               float victimYaw = victim.rotationYaw;
               float difference = victimYaw - yaw;
               difference = this.posMod(difference + 180.0F, 360.0F) - 180.0F;
               boolean doBonusDamage = MathHelper.abs(difference) <= 30.0F;
               return doBonusDamage ? baseDamage + bonusDamage : baseDamage;
         }
         default: {
               return baseDamage;
         }
         }

         if (victim.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty() && victim.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty() && victim.getItemStackFromSlot(EntityEquipmentSlot.LEGS).isEmpty() && victim.getItemStackFromSlot(EntityEquipmentSlot.FEET).isEmpty()) {
            return ConfigHandler.damageBonusCheckArmorValue && victim.getEntityAttribute(SharedMonsterAttributes.ARMOR).getBaseValue() > (double)ConfigHandler.damageBonusMaxArmorValue ? baseDamage : baseDamage + bonusDamage;
         } else {
            return baseDamage;
         }
      } else {
         return baseDamage;
      }
   }

   private float posMod(float num, float den) {
      return (num % den + den) % den;
   }
}
