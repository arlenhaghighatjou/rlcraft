package com.tmtravlr.qualitytools.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.JsonUtils;

public class QualityType {
   public static final Random RAND = new Random();
   public String[] slots;
   public QualityEntry[] qualities;
   public QualityItem[] blacklist;
   public QualityItem[] whitelist;

   public QualityType() {
   }

   public boolean itemMatches(ItemStack stack) {
      boolean matches = false;

      for(QualityItem qualityItem : this.whitelist) {
         if (qualityItem != null && qualityItem.itemMatches(stack)) {
            matches = true;
         }
      }

      for(QualityItem qualityItem : this.blacklist) {
         if (qualityItem != null && qualityItem.itemMatches(stack)) {
            matches = false;
         }
      }

      return matches;
   }

   public void generateQualityTag(ItemStack stack, boolean skipNormal) {
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      if (stack.getTagCompound().hasKey("Quality")) {
         stack.getTagCompound().removeTag("Quality");
      }

      NBTTagCompound tag = new NBTTagCompound();
      QualityEntry entry = this.chooseQualityEntry(skipNormal);
      if (!entry.name.equalsIgnoreCase("normal")) {
         tag.setString("Name", entry.name);
         tag.setString("Color", entry.color.getFriendlyName());
         NBTTagList slotList = new NBTTagList();

         for(String slot : this.slots) {
            slotList.appendTag(new NBTTagString(slot));
         }

         tag.setTag("Slots", slotList);
         NBTTagList modifierList = new NBTTagList();

         for(String attributeName : entry.attributeMap.keySet()) {
            for(AttributeModifier modifier : entry.attributeMap.get(attributeName)) {
               NBTTagCompound modifierTag = SharedMonsterAttributes.writeAttributeModifierToNBT(new AttributeModifier(UUID.randomUUID(), modifier.getName(), modifier.getAmount(), modifier.getOperation()));
               modifierTag.setString("AttributeName", attributeName);
               modifierList.appendTag(modifierTag);
            }
         }

         tag.setTag("AttributeModifiers", modifierList);
      }

      stack.getTagCompound().setTag("Quality", tag);
   }

   public QualityEntry chooseQualityEntry(boolean skipNormal) {
      int weightTotal = 0;

      for(QualityEntry entry : this.qualities) {
         if (!skipNormal || !entry.name.equalsIgnoreCase("normal")) {
            weightTotal += entry.weight;
         }
      }

      if (weightTotal == 0) {
         return null;
      } else {
         int chosenWeight = RAND.nextInt(weightTotal) + 1;
         weightTotal = 0;

         for(QualityEntry entry : this.qualities) {
            if (!skipNormal || !entry.name.equalsIgnoreCase("normal")) {
               weightTotal += entry.weight;
               if (chosenWeight <= weightTotal) {
                  return entry;
               }
            }
         }

         return null;
      }
   }

   public static class Serializer implements JsonDeserializer<QualityType> {
      public Serializer() {
      }

      public QualityType deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
         JsonObject json = jsonElement.getAsJsonObject();
         QualityType quality = new QualityType();
         if (JsonUtils.isString(json, "slot")) {
            quality.slots = new String[]{JsonUtils.getString(json, "slot")};
         } else {
            JsonArray slotArray = JsonUtils.getJsonArray(json, "slot");
            quality.slots = new String[slotArray.size()];

            for(int i = 0; i < quality.slots.length; ++i) {
               if (!slotArray.get(i).isJsonPrimitive() || !slotArray.get(i).getAsJsonPrimitive().isString()) {
                  throw new JsonSyntaxException("Expected slot entry to be a string, was " + JsonUtils.toString(slotArray.get(i)));
               }

               quality.slots[i] = slotArray.get(i).getAsString();
            }
         }

         quality.qualities = (QualityEntry[])JsonUtils.deserializeClass(json, "qualities", context, QualityEntry[].class);
         quality.blacklist = (QualityItem[])JsonUtils.deserializeClass(json, "blacklist", new QualityItem[0], context, QualityItem[].class);
         quality.whitelist = (QualityItem[])JsonUtils.deserializeClass(json, "whitelist", new QualityItem[0], context, QualityItem[].class);
         return quality;
      }
   }
}
