package xzeroair.trinkets.attributes.RaceAttribute;

import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import xzeroair.trinkets.races.EntityRace;

public class RaceAttribute {
   public static final IAttribute ENTITY_RACE = (new RangedAttribute((IAttribute)null, "xat.entityRace", 1.0D, 1.401298464324817E-45D, 3.4028234663852886E38D)).setDescription("Entity Race").setShouldWatch(true);

   public RaceAttribute() {
   }

   public static void removeModifier(EntityLivingBase entity, UUID uuid) {
      IAttributeInstance AttributeInstance = entity.getAttributeMap().getAttributeInstance(ENTITY_RACE);
      if (AttributeInstance != null) {
         if (AttributeInstance.getModifier(uuid) != null) {
            AttributeInstance.removeModifier(uuid);
         }

      }
   }

   public static void removeModifier(EntityLivingBase entity, EntityRace race) {
      removeModifier(entity, race.getUUID());
   }

   public static void removeAllModifiersExcluding(EntityLivingBase entity, UUID uuid) {
      IAttributeInstance AttributeInstance = entity.getAttributeMap().getAttributeInstance(ENTITY_RACE);
      if (AttributeInstance != null) {
         if (!AttributeInstance.getModifiers().isEmpty()) {
            for(AttributeModifier modifier : AttributeInstance.getModifiers()) {
               if (modifier.getID().compareTo(uuid) != 0) {
                  AttributeInstance.removeModifier(modifier);
               }
            }
         }

      }
   }

   public static void removeAllModifiersExcluding(EntityLivingBase entity, EntityRace race) {
      removeAllModifiersExcluding(entity, race.getUUID());
   }

   public static void removeAllModifiers(EntityLivingBase entity) {
      IAttributeInstance AttributeInstance = entity.getAttributeMap().getAttributeInstance(ENTITY_RACE);
      if (AttributeInstance != null) {
         if (!AttributeInstance.getModifiers().isEmpty()) {
            AttributeInstance.removeAllModifiers();
         }

      }
   }
}
