package com.tmtravlr.qualitytools.config;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.oredict.OreDictionary;

public class CustomMaterial {
   public Item item;
   public int meta;
   public NBTTagCompound tag;
   public String oreDict;

   public CustomMaterial() {
   }

   public boolean itemMatches(ItemStack material) {
      if (material != null && !material.isEmpty()) {
         if (this.item != null) {
            if (this.item == material.getItem() && (this.meta == 32767 || this.meta == material.getItemDamage()) && NBTUtil.areNBTEquals(this.tag, material.getTagCompound(), true)) {
               return true;
            }
         } else if (this.oreDict != null) {
            int[] entries = OreDictionary.getOreIDs(material);
            int oreId = OreDictionary.getOreID(this.oreDict);

            for(int i = 0; i < entries.length; ++i) {
               if (entries[i] == oreId) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.item == null ? 0 : Item.REGISTRY.getIDForObject(this.item));
      result = 31 * result + this.meta;
      result = 31 * result + (this.tag == null ? 0 : this.tag.hashCode());
      result = 31 * result + (this.oreDict == null ? 0 : this.oreDict.hashCode());
      return result;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CustomMaterial other = (CustomMaterial)obj;
         if (this.item != other.item) {
            return false;
         } else if (this.meta != other.meta) {
            return false;
         } else {
            if (this.tag == null) {
               if (other.tag != null) {
                  return false;
               }
            } else if (!this.tag.equals(other.tag)) {
               return false;
            }

            if (this.oreDict == null) {
               if (other.oreDict != null) {
                  return false;
               }
            } else if (!this.oreDict.equals(other.oreDict)) {
               return false;
            }

            return true;
         }
      }
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      if (this.item != null) {
         compound.setString("Item", "" + Item.REGISTRY.getNameForObject(this.item));
      }

      if (this.meta != 32767) {
         compound.setInteger("Meta", this.meta);
      }

      if (this.tag != null) {
         compound.setTag("Tag", this.tag);
      }

      if (this.oreDict != null) {
         compound.setString("OreDict", this.oreDict);
      }

      return compound;
   }

   public CustomMaterial readFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("Item")) {
         this.item = (Item)Item.REGISTRY.getObject(new ResourceLocation(compound.getString("Item")));
      }

      if (compound.hasKey("Meta")) {
         this.meta = compound.getInteger("Meta");
      } else {
         this.meta = 32767;
      }

      if (compound.hasKey("Tag")) {
         this.tag = compound.getCompoundTag("Tag");
      }

      if (compound.hasKey("OreDict")) {
         this.oreDict = compound.getString("OreDict");
      }

      return this;
   }

   public static class Serializer implements JsonDeserializer<CustomMaterial> {
      public Serializer() {
      }

      public CustomMaterial deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
         JsonObject json = jsonElement.getAsJsonObject();
         CustomMaterial reforgeMaterial = new CustomMaterial();
         reforgeMaterial.meta = JsonUtils.getInt(json, "meta", 32767);
         String tagString = json.has("tag") ? JsonUtils.getString(json, "tag") : "";
         if (!tagString.isEmpty() && !tagString.equals("{}")) {
            try {
               reforgeMaterial.tag = JsonToNBT.getTagFromJson(tagString);
            } catch (NBTException var8) {
               FMLLog.warning("[Quality Tools] Problem loading item tag %s in reforging materials.json. Ignoring.", new Object[]{tagString});
               ConfigLoader.problems.add("reforging materials.json: Problem loading nbt tag: " + var8.getMessage() + ", with tag " + tagString);
            }
         }

         if (JsonUtils.hasField(json, "ore_dict")) {
            reforgeMaterial.oreDict = JsonUtils.getString(json, "ore_dict");
         }

         if (JsonUtils.hasField(json, "item")) {
            String itemName = JsonUtils.getString(json, "item");
            reforgeMaterial.item = (Item)Item.REGISTRY.getObject(new ResourceLocation(itemName));
            if (reforgeMaterial.item == null) {
               if (ConfigLoader.ignoreUnknownItems) {
                  FMLLog.warning("[Quality Tools] Unknown item %s in reforging materials.json. Ignoring.", new Object[]{itemName});
                  return null;
               }

               throw new JsonParseException("Unknown item " + itemName);
            }
         }

         return reforgeMaterial;
      }
   }
}
