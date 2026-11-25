package com.tmtravlr.qualitytools;

import com.google.common.collect.Multimap;
import com.tmtravlr.qualitytools.config.ConfigLoader;
import com.tmtravlr.qualitytools.config.CustomMaterial;
import com.tmtravlr.qualitytools.config.QualityType;
import java.util.Iterator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class QualityToolsHelper {
   public static final String TAG_NAME_QUALITY = "Quality";
   public static final String TAG_NAME_NAME = "Name";
   public static final String TAG_NAME_COLOR = "Color";
   public static final String TAG_NAME_TYPE = "Type";
   public static final String TAG_NAME_SLOTS = "Slots";
   public static final String TAG_NAME_ATTRIBUTE_NAME = "AttributeName";
   public static final String TAG_NAME_ATTRIBUTE_MODIFIERS = "AttributeModifiers";

   public QualityToolsHelper() {
   }

   public static NBTTagCompound getQualityTag(ItemStack stack) {
      return stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("Quality", 10) ? stack.getSubCompound("Quality") : new NBTTagCompound();
   }

   public static boolean hasQualityTag(ItemStack stack) {
      return stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("Quality", 10);
   }

   public static boolean generateQualityTag(ItemStack stack, boolean skipNormal) {
      if (stack != null && !stack.isEmpty() && (stack.getItem().getItemStackLimit(stack) == 1 || ConfigLoader.allowStackableItems)) {
         for(QualityType qualityType : ConfigLoader.qualityTypes.values()) {
            if (qualityType != null && qualityType.itemMatches(stack)) {
               qualityType.generateQualityTag(stack, skipNormal);
               return true;
            }
         }
      }

      return false;
   }

   public static boolean canReforgeWith(ItemStack tool, ItemStack material) {
      if (tool != null && !tool.isEmpty() && material != null && material.getItem() != null) {
         if (ConfigLoader.universalReforgeItem != null && ConfigLoader.universalReforgeItem.itemMatches(material)) {
            return true;
         } else {
            label50:
            for(CustomMaterial toolType : ConfigLoader.customReforgeMaterials.keySet()) {
               if (toolType != null && toolType.itemMatches(tool)) {
                  Iterator var4 = ConfigLoader.customReforgeMaterials.get(toolType).iterator();

                  while(true) {
                     if (!var4.hasNext()) {
                        continue label50;
                     }

                     CustomMaterial materialType = (CustomMaterial)var4.next();
                     if (materialType != null && materialType.itemMatches(material)) {
                        break;
                     }
                  }

                  return true;
               }
            }

            if (ConfigLoader.useRepairItem && tool.getItem().getIsRepairable(tool, material)) {
               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public static void applyAttributesForSlot(EntityLivingBase entity, ItemStack stack, String slot, Multimap<String, AttributeModifier> modifiersToRemove) {
      if (stack != null && !stack.isEmpty() && entity != null) {
         NBTTagCompound tag = getQualityTag(stack);
         if (!tag.hasNoTags()) {
            boolean hasSlot = false;
            NBTTagList slots = tag.getTagList("Slots", 8);

            for(int i = 0; i < slots.tagCount(); ++i) {
               if (slots.getStringTagAt(i).equalsIgnoreCase(slot)) {
                  hasSlot = true;
               }
            }

            if (!hasSlot) {
               return;
            }

            NBTTagList attributeList = tag.getTagList("AttributeModifiers", 10);

            for(int i = 0; i < attributeList.tagCount(); ++i) {
               AttributeModifier modifier = SharedMonsterAttributes.readAttributeModifierFromNBT(attributeList.getCompoundTagAt(i));
               String attributeName = attributeList.getCompoundTagAt(i).getString("AttributeName");
               if (modifiersToRemove.get(attributeName).contains(modifier)) {
                  modifiersToRemove.get(attributeName).remove(modifier);
               } else {
                  IAttributeInstance entityAttribute = entity.getAttributeMap().getAttributeInstanceByName(attributeName);
                  if (entityAttribute != null && !entityAttribute.hasModifier(modifier)) {
                     entityAttribute.applyModifier(modifier);
                  }
               }
            }
         }

      }
   }

   public static boolean hasClassType(String itemClass, Class currentClass) {
      if (currentClass.getSimpleName().equals(itemClass)) {
         return true;
      } else if (currentClass.getSuperclass() != null && hasClassType(itemClass, currentClass.getSuperclass())) {
         return true;
      } else {
         if (currentClass.getInterfaces() != null && currentClass.getInterfaces().length > 0) {
            for(Class currentInterface : currentClass.getInterfaces()) {
               if (hasClassType(itemClass, currentInterface)) {
                  return true;
               }
            }
         }

         return false;
      }
   }
}
