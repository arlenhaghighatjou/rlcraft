package com.fantasticsource.mctools.items;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ItemFilter {
   public ItemStack itemStack = null;
   public LinkedHashMap<String, String> tagsRequired = new LinkedHashMap();
   public LinkedHashMap<String, String> tagsDisallowed = new LinkedHashMap();

   public ItemFilter() {
   }

   public ItemFilter(ItemStack itemStack, LinkedHashMap<String, String> tagsRequired, LinkedHashMap<String, String> tagsDisallowed) {
      this.itemStack = itemStack;
      this.tagsRequired = tagsRequired;
      this.tagsDisallowed = tagsDisallowed;
   }

   public static ItemFilter getInstance(String itemStackString) {
      return getInstance(itemStackString, false);
   }

   public static ItemFilter getInstance(String itemStackString, boolean suppressItemMissingError) {
      ItemFilter result = new ItemFilter();
      String[] registryAndNBT = itemStackString.trim().split(Pattern.quote(">"));
      if (registryAndNBT.length == 0) {
         System.err.println(I18n.translateToLocalFormatted("fantasticlib.error.notEnoughItemFilterArgs", new Object[]{itemStackString}));
         return null;
      } else if (registryAndNBT.length > 2) {
         System.err.println(I18n.translateToLocalFormatted("fantasticlib.error.tooManyItemFilterArgs", new Object[]{itemStackString}));
         return null;
      } else {
         String token = registryAndNBT[0].trim();
         if (!token.equals("")) {
            int meta = 0;
            String[] innerTokens = token.split(Pattern.quote(":"));
            if (innerTokens.length > 3) {
               System.err.println(I18n.translateToLocalFormatted("fantasticlib.error.badItemName", new Object[]{token}));
               return null;
            }

            ResourceLocation resourceLocation;
            if (innerTokens.length == 3) {
               resourceLocation = new ResourceLocation(innerTokens[0], innerTokens[1]);

               try {
                  meta = Integer.parseInt(innerTokens[2]);
               } catch (NumberFormatException var15) {
                  System.err.println(I18n.translateToLocalFormatted("fantasticlib.error.badItemMeta", new Object[]{token}));
                  return null;
               }
            } else if (innerTokens.length == 1) {
               resourceLocation = new ResourceLocation("minecraft", innerTokens[0]);
            } else {
               try {
                  meta = Integer.parseInt(innerTokens[1]);
                  resourceLocation = new ResourceLocation("minecraft", innerTokens[0]);
               } catch (NumberFormatException var14) {
                  meta = 0;
                  resourceLocation = new ResourceLocation(innerTokens[0], innerTokens[1]);
               }
            }

            Item item = (Item)ForgeRegistries.ITEMS.getValue(resourceLocation);
            if (item != null) {
               result.itemStack = new ItemStack(item, 1, meta);
            } else {
               Block block = ForgeRegistries.BLOCKS.containsKey(resourceLocation) ? (Block)ForgeRegistries.BLOCKS.getValue(resourceLocation) : null;
               if (block != null) {
                  result.itemStack = new ItemStack(block, 1, meta);
               }
            }

            if (result.itemStack == null) {
               if (!suppressItemMissingError) {
                  System.err.println(I18n.translateToLocalFormatted("fantasticlib.error.itemForFilterNotFound", new Object[]{token}));
               }

               return null;
            }
         }

         if (registryAndNBT.length > 1) {
            String nbt = registryAndNBT[1].trim();

            for(Entry<ResourceLocation, Enchantment> entry : ForgeRegistries.ENCHANTMENTS.getEntries()) {
               nbt = nbt.replaceAll(((ResourceLocation)entry.getKey()).toString(), "id:" + Enchantment.getEnchantmentID((Enchantment)entry.getValue()) + "s");
            }

            String[] tags = nbt.split(Pattern.quote("&"));

            for(String tag : tags) {
               tag = tag.trim();
               if (!tag.equals("")) {
                  String[] keyValue = tag.split(Pattern.quote("="));
                  if (keyValue.length > 2) {
                     System.err.println(I18n.translateToLocalFormatted("fantasticlib.error.tooManyNBTValues", new Object[]{itemStackString}));
                     return null;
                  }

                  String key = keyValue[0].trim();
                  if (!key.equals("")) {
                     LinkedHashMap<String, String> map;
                     if (key.charAt(0) == '!') {
                        key = key.substring(1);
                        map = result.tagsDisallowed;
                     } else {
                        map = result.tagsRequired;
                     }

                     map.put(key, keyValue.length == 2 ? keyValue[1].trim() : null);
                  }
               }
            }
         }

         return result;
      }
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public boolean matches(ItemStack stack) {
      if (this.itemStack.getItem().equals(stack.getItem()) && (stack.isItemStackDamageable() || this.itemStack.getMetadata() == stack.getMetadata())) {
         NBTTagCompound compound = stack.getTagCompound();
         if (compound != null) {
            for(Entry<String, String> entry : this.tagsDisallowed.entrySet()) {
               if (this.checkNBT(compound, ((String)entry.getKey()).split(":", -1), (String)entry.getValue())) {
                  return false;
               }
            }
         }

         Set<Entry<String, String>> entrySet = this.tagsRequired.entrySet();
         if (entrySet.size() > 0) {
            if (compound == null) {
               return false;
            }

            for(Entry<String, String> entry : entrySet) {
               if (!this.checkNBT(compound, ((String)entry.getKey()).split(":", -1), (String)entry.getValue())) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean checkNBT(NBTBase base, String[] keymap, String value) {
      if (keymap != null && keymap.length != 0) {
         String key = keymap[0].trim();
         if (!key.equals("")) {
            if (base instanceof NBTTagCompound) {
               NBTTagCompound compound = (NBTTagCompound)base;
               return !compound.hasKey(key) ? false : this.checkNBT(compound.getTag(key), (String[])Arrays.copyOfRange(keymap, 1, keymap.length), value);
            } else {
               return false;
            }
         } else {
            if (base instanceof NBTTagList) {
               for(NBTBase base1 : (NBTTagList)base) {
                  if (this.checkNBT(base1, (String[])Arrays.copyOfRange(keymap, 1, keymap.length), value)) {
                     return true;
                  }
               }
            }

            return false;
         }
      } else {
         return this.checkValue(base, value);
      }
   }

   private boolean checkValue(NBTBase base, String value) {
      if (value != null && !value.trim().equals(base.toString())) {
         value = value.replace(";", ",");
         if (value.equals(base.toString())) {
            return true;
         } else {
            String[] newValues = value.split(",");
            if (newValues.length < 2 && !value.contains(":")) {
               return false;
            } else {
               for(String newValue : newValues) {
                  String[] tokens = newValue.split(":");
                  if (!this.checkNBT(base, (String[])Arrays.copyOf(tokens, tokens.length - 1), tokens[tokens.length - 1])) {
                     return false;
                  }
               }

               return true;
            }
         }
      } else {
         return true;
      }
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof ItemFilter)) {
         return false;
      } else if (obj.getClass() != this.getClass()) {
         return obj.equals(this);
      } else {
         ItemFilter other = (ItemFilter)obj;
         if (this.itemStack == null) {
            if (other.itemStack != null) {
               return false;
            }
         } else {
            if (this.itemStack.getItem() != other.itemStack.getItem()) {
               return false;
            }

            if (this.itemStack.getMetadata() != other.itemStack.getMetadata()) {
               return false;
            }
         }

         if (this.tagsRequired.size() != other.tagsRequired.size()) {
            return false;
         } else if (this.tagsDisallowed.size() != other.tagsDisallowed.size()) {
            return false;
         } else {
            for(Entry<String, String> entry : this.tagsRequired.entrySet()) {
               if (!((String)entry.getValue()).equals(other.tagsRequired.get(entry.getKey()))) {
                  return false;
               }
            }

            for(Entry<String, String> entry : this.tagsDisallowed.entrySet()) {
               if (!((String)entry.getValue()).equals(other.tagsDisallowed.get(entry.getKey()))) {
                  return false;
               }
            }

            return true;
         }
      }
   }
}
