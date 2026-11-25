package xzeroair.trinkets.attributes;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.world.World;
import xzeroair.trinkets.util.compat.firstaid.FirstAidCompat;

public class UpdatingAttribute {
   public String modifierName;
   public String attribute;
   public EaglercraftUUID uuid;
   public double amount;
   public int operation;
   public boolean isSavedInNBT;

   public UpdatingAttribute(EaglercraftUUID uuid, IAttribute attribute) {
      this("xat." + attribute.getName() + ".modifier", uuid, attribute.getName());
   }

   public UpdatingAttribute(String name, EaglercraftUUID uuid, IAttribute attribute) {
      this(name, uuid, attribute.getName());
   }

   public UpdatingAttribute(String name, EaglercraftUUID uuid, String attributeName) {
      this.modifierName = name;
      this.uuid = uuid;
      this.attribute = attributeName;
      this.isSavedInNBT = true;
      this.amount = 0.0D;
      this.operation = 0;
   }

   public UpdatingAttribute setSavedInNBT(boolean isSavedInNBT) {
      this.isSavedInNBT = isSavedInNBT;
      return this;
   }

   public UpdatingAttribute setAmount(double amount) {
      this.amount = amount;
      return this;
   }

   public UpdatingAttribute setOperation(int operation) {
      this.operation = operation;
      return this;
   }

   private AttributeModifier createModifier(double amount, int operation) {
      return !this.isSavedInNBT ? (new AttributeModifier(this.uuid, this.modifierName, amount, operation)).setSaved(false) : new AttributeModifier(this.uuid, this.modifierName, amount, operation);
   }

   public boolean isValidAttribute(EntityLivingBase entity) {
      if (entity != null) {
         IAttributeInstance AttributeInstance = entity.getAttributeMap().getAttributeInstanceByName(this.attribute);
         return AttributeInstance != null;
      } else {
         return false;
      }
   }

   public void addModifier(EntityLivingBase entity) {
      this.addModifier(entity, this.amount, this.operation);
   }

   public void addModifier(EntityLivingBase entity, double amount, int operation) {
      World world = entity == null ? null : entity.getEntityWorld();
      if (entity != null && world != null) {
         IAttributeInstance AttributeInstance = entity.getAttributeMap().getAttributeInstanceByName(this.attribute);
         if (AttributeInstance != null && this.uuid.compareTo(EaglercraftUUID.fromString("00000000-0000-0000-0000-000000000000")) != 0) {
            if (AttributeInstance.getModifier(this.uuid) != null) {
               AttributeModifier m = AttributeInstance.getModifier(this.uuid);
               if (amount == 0.0D || m.getAmount() != amount || m.getOperation() != operation) {
                  this.removeModifier(entity);
               }
            }

            if (amount != 0.0D && AttributeInstance.getModifier(this.uuid) == null) {
               if (AttributeInstance.getAttribute() == SharedMonsterAttributes.MAX_HEALTH) {
                  AttributeModifier modifier = this.createModifier(amount, operation);
                  AttributeInstance.applyModifier(modifier);
                  FirstAidCompat.rescale(entity);
               } else {
                  AttributeInstance.applyModifier(this.createModifier(amount, operation));
               }
            }

         }
      }
   }

   public void removeModifier(EntityLivingBase entity) {
      World world = entity == null ? null : entity.getEntityWorld();
      if (entity != null && world != null) {
         IAttributeInstance AttributeInstance = entity.getAttributeMap().getAttributeInstanceByName(this.attribute);
         if (AttributeInstance != null && AttributeInstance.getModifier(this.uuid) != null) {
            if (AttributeInstance.getAttribute() == JumpAttribute.stepHeight && (double)entity.stepHeight != AttributeInstance.getBaseValue()) {
               entity.stepHeight = (float)AttributeInstance.getBaseValue();
            }

            AttributeInstance.removeModifier(this.uuid);
         }
      }
   }
}
