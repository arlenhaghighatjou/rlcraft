package shadows.placebo.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

public class AttributeHelper {
   public AttributeHelper() {
   }

   public static void modify(EntityLivingBase entity, IAttribute attribute, String name, double modifier, int operation) {
      entity.getEntityAttribute(attribute).applyModifier((new AttributeModifier("placebo:" + name, modifier, operation)).setSaved(true));
   }

   public static void addToBase(EntityLivingBase entity, IAttribute attribute, String name, double modifier) {
      modify(entity, attribute, name, modifier, 0);
   }

   public static void addXTimesNewBase(EntityLivingBase entity, IAttribute attribute, String name, double modifier) {
      modify(entity, attribute, name, modifier, 1);
   }

   public static void multiplyFinal(EntityLivingBase entity, IAttribute attribute, String name, double modifier) {
      modify(entity, attribute, name, modifier, 2);
   }

   public static void setBaseValue(EntityLivingBase entity, IAttribute attribute, String name, double value) {
      IAttributeInstance inst = entity.getEntityAttribute(attribute);
      inst.getModifiersByOperation(0).clear();
      modify(entity, attribute, name, value - inst.getBaseValue(), 0);
   }

   public static void min(EntityLivingBase entity, IAttribute attribute, String name, double value) {
      if (value < entity.getEntityAttribute(attribute).getBaseValue()) {
         setBaseValue(entity, attribute, name, value);
      }

   }

   public static void max(EntityLivingBase entity, IAttribute attribute, String name, double value) {
      if (value > entity.getEntityAttribute(attribute).getBaseValue()) {
         setBaseValue(entity, attribute, name, value);
      }

   }
}
