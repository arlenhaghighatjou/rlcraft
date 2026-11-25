package net.silentchaos512.lib.util;

import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

public class AttributeHelper {
   public AttributeHelper() {
   }

   public static void apply(EntityLivingBase entity, IAttribute attribute, UUID uuid, String name, double amount, int op) {
      IAttributeInstance instance = entity.getAttributeMap().getAttributeInstance(attribute);
      apply(instance, uuid, name, amount, op);
   }

   public static void apply(IAttributeInstance attributeInstance, UUID uuid, String name, double amount, int op) {
      if (attributeInstance != null) {
         AttributeModifier currentMod = attributeInstance.getModifier(uuid);
         AttributeModifier newMod = new AttributeModifier(uuid, name, amount, op);
         if (currentMod == null || currentMod.getAmount() == amount && currentMod.getOperation() == op) {
            if (currentMod == null) {
               attributeInstance.applyModifier(newMod);
            }
         } else {
            attributeInstance.removeModifier(currentMod);
            attributeInstance.applyModifier(newMod);
         }

      }
   }

   public static void remove(EntityLivingBase entity, IAttribute attribute, UUID uuid) {
      IAttributeInstance instance = entity.getAttributeMap().getAttributeInstance(attribute);
      remove(instance, uuid);
   }

   public static void remove(IAttributeInstance attributeInstance, UUID uuid) {
      if (attributeInstance != null) {
         attributeInstance.removeModifier(uuid);
      }
   }
}
