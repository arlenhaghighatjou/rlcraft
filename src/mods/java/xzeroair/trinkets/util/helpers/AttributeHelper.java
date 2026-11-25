package xzeroair.trinkets.util.helpers;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import xzeroair.trinkets.attributes.RaceAttribute.RaceAttribute;

public class AttributeHelper {
   public AttributeHelper() {
   }

   public static void removeAttributes(EntityLivingBase entity, EaglercraftUUID uuid) {
      for(IAttributeInstance attribute : entity.getAttributeMap().getAllAttributes()) {
         if (!attribute.getAttribute().equals(RaceAttribute.ENTITY_RACE) && attribute.getModifier(uuid) != null) {
            attribute.removeModifier(uuid);
         }
      }

   }

   public static void removeAttributesByUUID(EntityLivingBase entity, EaglercraftUUID uuid) {
      for(IAttributeInstance attribute : entity.getAttributeMap().getAllAttributes()) {
         if (!attribute.getAttribute().equals(RaceAttribute.ENTITY_RACE) && attribute.getModifier(uuid) != null) {
            attribute.removeModifier(uuid);
         }
      }

   }

   public static void removeAttributesByUUID(EntityLivingBase entity, EaglercraftUUID... uuids) {
      for(IAttributeInstance attribute : entity.getAttributeMap().getAllAttributes()) {
         if (!attribute.getAttribute().equals(RaceAttribute.ENTITY_RACE)) {
            for(EaglercraftUUID uuid : uuids) {
               if (attribute.getModifier(uuid) != null) {
                  attribute.removeModifier(uuid);
               }
            }
         }
      }

   }
}
