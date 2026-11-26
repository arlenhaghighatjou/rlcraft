package net.silentchaos512.scalinghealth.utils;

import net.lax1dude.eaglercraft.v1_8.HString;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.config.Config;

public class ModifierHandler {
   public static final EaglercraftUUID MODIFIER_ID_HEALTH = EaglercraftUUID.fromString("c0bef565-35f6-4dc5-bb4c-3644c382e6ce");
   public static final EaglercraftUUID MODIFIER_ID_DAMAGE = EaglercraftUUID.fromString("d3560b15-c459-451c-86a8-0247015ae899");
   public static final String MODIFIER_NAME_HEALTH = "ScalingHealth.HealthModifier";
   public static final String MODIFIER_NAME_DAMAGE = "ScalingHealth.DamageModifier";

   public ModifierHandler() {
   }

   private static void setModifier(IAttributeInstance attr, EaglercraftUUID id, String name, double amount, int op) {
      if (attr != null) {
         double normalValue = attr.getBaseValue();
         double difference = amount - normalValue;
         AttributeModifier mod = attr.getModifier(id);
         AttributeModifier newMod = new AttributeModifier(id, name, difference, op);
         if (mod != null) {
            attr.removeModifier(mod);
         }

         attr.applyModifier(newMod);
      }
   }

   public static void setMaxHealth(EntityLivingBase entity, double amount, int op) {
      if (amount <= 0.0D) {
         ScalingHealth.logHelper.warn("ModifierHandler.setMaxHealth: amount <= 0!", new Object[0]);
      } else if (entity instanceof EntityPlayer && !Config.Player.Health.allowModify) {
         ScalingHealth.logHelper.info(HString.format("Would have set player %s's health to %.1f, but modified player health has been disabled in the config!", entity.getName(), amount), new Object[0]);
      } else {
         float originalHealth = entity.getHealth();
         IAttributeInstance attr = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
         if (attr != null) {
            setModifier(attr, MODIFIER_ID_HEALTH, "ScalingHealth.HealthModifier", amount, op);
            entity.setHealth(originalHealth);
         }

      }
   }

   public static void addAttackDamage(EntityLivingBase entity, double amount, int op) {
      IAttributeInstance attr = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      if (attr != null) {
         amount = amount + attr.getBaseValue();
         setModifier(attr, MODIFIER_ID_DAMAGE, "ScalingHealth.DamageModifier", amount, op);
      }

   }

   public static double getHealthModifier(EntityLivingBase entity) {
      IAttributeInstance attr = entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
      if (attr == null) {
         return 0.0D;
      } else {
         AttributeModifier mod = attr.getModifier(MODIFIER_ID_HEALTH);
         return mod != null ? mod.getAmount() : 0.0D;
      }
   }

   public static double getDamageModifier(EntityLivingBase entity) {
      IAttributeInstance attr = entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
      if (attr == null) {
         return 0.0D;
      } else {
         AttributeModifier mod = attr.getModifier(MODIFIER_ID_DAMAGE);
         return mod != null ? mod.getAmount() : 0.0D;
      }
   }
}
