package com.tmtravlr.qualitytools.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.tmtravlr.qualitytools.QualityToolsHelper;
import com.tmtravlr.qualitytools.baubles.BaublesHandler;
import java.lang.reflect.Type;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;

public class QualityItem {
   public String itemClass;
   public Item item;
   public String[] equipLocations;
   public int meta;
   public NBTTagCompound tag;

   public QualityItem() {
   }

   public boolean itemMatches(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         if (this.meta != 32767 && stack.getItemDamage() != this.meta) {
            return false;
         } else if (this.tag != null && !NBTUtil.areNBTEquals(this.tag, stack.getTagCompound(), true)) {
            return false;
         } else if (this.item != null && stack.getItem() != this.item) {
            return false;
         } else if (this.itemClass != null && !QualityToolsHelper.hasClassType(this.itemClass, stack.getItem().getClass())) {
            return false;
         } else {
            if (this.equipLocations != null && this.equipLocations.length > 0) {
               boolean canEquip = false;

               for(String location : this.equipLocations) {
                  if (BaublesHandler.INSTANCE.canEquipBauble(stack, location)) {
                     canEquip = true;
                  }

                  try {
                     EntityEquipmentSlot equipment = EntityEquipmentSlot.fromString(location);
                     if (stack.getItem() instanceof ItemArmor && ((ItemArmor)stack.getItem()).armorType == equipment || equipment == EntityEquipmentSlot.CHEST && stack.getItem() == Items.ELYTRA) {
                        canEquip = true;
                        continue;
                     }
                  } catch (IllegalArgumentException var8) {
                     ;
                  }

                  if (location.equalsIgnoreCase("horse") && HorseArmorType.getByItemStack(stack) != HorseArmorType.NONE) {
                     canEquip = true;
                  }
               }

               if (!canEquip) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static class Serializer implements JsonDeserializer<QualityItem> {
      public Serializer() {
      }

      public QualityItem deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
         JsonObject json = jsonElement.getAsJsonObject();
         QualityItem qualityItem = new QualityItem();
         qualityItem.meta = JsonUtils.getInt(json, "meta", 32767);
         String tagString = json.has("tag") ? JsonUtils.getString(json, "tag") : "";
         if (!tagString.isEmpty() && !tagString.equals("{}")) {
            try {
               qualityItem.tag = JsonToNBT.getTagFromJson(tagString);
            } catch (NBTException var9) {
               FMLLog.warning("[Quality Tools] Problem loading item tag %s in %s. Ignoring.", new Object[]{tagString, ConfigLoader.typeDeserializing});
               ConfigLoader.problems.add(ConfigLoader.typeDeserializing + ": Problem loading nbt tag: " + var9.getMessage() + ", with tag " + tagString);
            }
         }

         if (JsonUtils.hasField(json, "class")) {
            qualityItem.itemClass = JsonUtils.getString(json, "class");
         }

         if (JsonUtils.hasField(json, "slot")) {
            if (JsonUtils.isString(json, "slot")) {
               qualityItem.equipLocations = new String[]{JsonUtils.getString(json, "slot")};
            } else {
               JsonArray slotArray = JsonUtils.getJsonArray(json, "slot");
               qualityItem.equipLocations = new String[slotArray.size()];

               for(int i = 0; i < qualityItem.equipLocations.length; ++i) {
                  if (!slotArray.get(i).isJsonPrimitive() || !slotArray.get(i).getAsJsonPrimitive().isString()) {
                     throw new JsonSyntaxException("Expected slot entry to be a string, was " + JsonUtils.toString(slotArray.get(i)));
                  }

                  qualityItem.equipLocations[i] = slotArray.get(i).getAsString();
               }
            }
         }

         if (JsonUtils.hasField(json, "item")) {
            String itemName = JsonUtils.getString(json, "item");
            qualityItem.item = (Item)Item.REGISTRY.getObject(new ResourceLocation(itemName));
            if (qualityItem.item == null) {
               if (ConfigLoader.ignoreUnknownItems) {
                  FMLLog.warning("[Quality Tools] Unknown item %s in %s. Ignoring.", new Object[]{itemName, ConfigLoader.typeDeserializing});
                  return null;
               }

               throw new JsonParseException("Unknown item " + itemName);
            }
         }

         return qualityItem;
      }
   }
}
