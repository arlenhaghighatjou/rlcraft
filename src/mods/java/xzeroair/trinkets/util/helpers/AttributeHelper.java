package xzeroair.trinkets.util.helpers;

import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import xzeroair.trinkets.attributes.RaceAttribute.RaceAttribute;

public class AttributeHelper {
   public AttributeHelper() {
   }

   public static void removeAttributes(EntityLivingBase entity, UUID uuid) {
      for(IAttributeInstance attribute : entity.getAttributeMap().getAllAttributes()) {
         if (!attribute.getAttribute().equals(RaceAttribute.ENTITY_RACE) && attribute.getModifier(uuid) != null) {
            attribute.removeModifier(uuid);
         }
      }

   }

   public static void removeAttributesByUUID(EntityLivingBase entity, UUID uuid) {
      for(IAttributeInstance attribute : entity.getAttributeMap().getAllAttributes()) {
         if (!attribute.getAttribute().equals(RaceAttribute.ENTITY_RACE) && attribute.getModifier(uuid) != null) {
            attribute.removeModifier(uuid);
         }
      }

   }

   public static void removeAttributesByUUID(EntityLivingBase entity, UUID... uuids) {
      for(IAttributeInstance attribute : entity.getAttributeMap().getAllAttributes()) {
         if (!attribute.getAttribute().equals(RaceAttribute.ENTITY_RACE)) {
            for(UUID uuid : uuids) {
               if (attribute.getModifier(uuid) != null) {
                  attribute.removeModifier(uuid);
               }
            }
         }
      }

   }
}
