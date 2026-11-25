package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemDrop {
   public static List<ItemDrop> allDrops = new ArrayList();
   public String itemId;
   protected int metadata;
   protected String burningItemId;
   protected int burningMetadata;
   protected Map<Integer, String> effectItemIds = new HashMap();
   protected Map<Integer, Integer> effectItemMetadata = new HashMap();
   public int minAmount = 1;
   public int maxAmount = 1;
   public boolean bonusAmount = true;
   public boolean amountMultiplier = true;
   public float chance = 0.0F;
   public int subspeciesIndex = -1;
   public int variantIndex = -1;
   public boolean adultOnly = true;

   public static ItemDrop createFromJSON(JsonObject json) {
      ItemDrop itemDrop = null;
      int itemMetadata = 0;
      if (json.has("item")) {
         if (json.has("metadata")) {
            itemMetadata = json.get("metadata").getAsInt();
         }

         String itemId = json.get("item").getAsString();
         itemDrop = new ItemDrop(itemId, itemMetadata, 1.0F);
         itemDrop.loadFromJSON(json);
      } else {
         LycanitesMobs.logWarning("", "[JSON] Unable to load item drop from json as it has no item id!");
      }

      allDrops.add(itemDrop);
      return itemDrop;
   }

   public static ItemDrop createFromConfigString(String itemDropString) {
      if (itemDropString != null && itemDropString.length() > 0) {
         String[] customDropValues = itemDropString.split(",");
         String itemId = customDropValues[0];
         int itemMetadata = 0;
         if (customDropValues.length > 1) {
            itemMetadata = Integer.parseInt(customDropValues[1]);
         }

         int amountMin = 1;
         if (customDropValues.length > 2) {
            amountMin = Integer.parseInt(customDropValues[2]);
         }

         int amountMax = 1;
         if (customDropValues.length > 3) {
            amountMax = Integer.parseInt(customDropValues[3]);
         }

         float chance = 1.0F;
         if (customDropValues.length > 4) {
            chance = Float.parseFloat(customDropValues[4]);
         }

         ItemDrop itemDrop = new ItemDrop(itemId, itemMetadata, chance);
         itemDrop.setMinAmount(amountMin);
         itemDrop.setMaxAmount(amountMax);
         return itemDrop;
      } else {
         return null;
      }
   }

   public ItemDrop(String itemId, int metadata, float chance) {
      this.itemId = itemId;
      this.metadata = metadata;
      this.chance = chance;
   }

   public ItemDrop(NBTTagCompound nbtTagCompound) {
      this.readFromNBT(nbtTagCompound);
   }

   public ItemDrop(ItemDrop copyDrop) {
      this.itemId = copyDrop.itemId;
      this.metadata = copyDrop.metadata;
      this.minAmount = copyDrop.minAmount;
      this.maxAmount = copyDrop.maxAmount;
      this.bonusAmount = copyDrop.bonusAmount;
      this.amountMultiplier = copyDrop.amountMultiplier;
      this.chance = copyDrop.chance;
      this.subspeciesIndex = copyDrop.subspeciesIndex;
      this.variantIndex = copyDrop.variantIndex;
      this.adultOnly = copyDrop.adultOnly;
      this.burningItemId = copyDrop.burningItemId;
      this.burningMetadata = copyDrop.burningMetadata;
      this.effectItemIds = copyDrop.effectItemIds;
      this.effectItemMetadata = copyDrop.effectItemMetadata;
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("minAmount")) {
         this.minAmount = json.get("minAmount").getAsInt();
      }

      if (json.has("maxAmount")) {
         this.maxAmount = json.get("maxAmount").getAsInt();
      }

      if (json.has("bonusAmount")) {
         this.bonusAmount = json.get("bonusAmount").getAsBoolean();
      }

      if (json.has("amountMultiplier")) {
         this.amountMultiplier = json.get("amountMultiplier").getAsBoolean();
      }

      if (json.has("chance")) {
         this.chance = json.get("chance").getAsFloat();
      }

      if (json.has("subspecies")) {
         this.subspeciesIndex = json.get("subspecies").getAsInt();
      }

      if (json.has("variant")) {
         this.variantIndex = json.get("variant").getAsInt();
      }

      if (json.has("adultOnly")) {
         this.adultOnly = json.get("adultOnly").getAsBoolean();
      }

      if (json.has("burningItem")) {
         this.burningItemId = json.get("burningItem").getAsString();
         if (json.has("burningMetadata")) {
            this.burningMetadata = json.get("burningMetadata").getAsInt();
         }
      }

   }

   public ItemDrop setDrop(ItemStack itemStack) {
      this.itemId = itemStack.getItem().getRegistryName().toString();
      this.metadata = itemStack.getMetadata();
      return this;
   }

   public ItemDrop setBurningDrop(ItemStack itemStack) {
      this.burningItemId = itemStack.getItem().getRegistryName().toString();
      this.burningMetadata = itemStack.getMetadata();
      return this;
   }

   public ItemDrop setEffectDrop(int effectID, ItemStack itemStack) {
      this.effectItemIds.put(Integer.valueOf(effectID), itemStack.getItem().getRegistryName().toString());
      this.effectItemMetadata.put(Integer.valueOf(effectID), Integer.valueOf(itemStack.getMetadata()));
      return this;
   }

   public ItemDrop setMinAmount(int amount) {
      this.minAmount = amount;
      return this;
   }

   public ItemDrop setMaxAmount(int amount) {
      this.maxAmount = amount;
      return this;
   }

   public ItemDrop setChance(float chance) {
      this.chance = chance;
      return this;
   }

   public ItemDrop setSubspecies(int subspeciesIndex) {
      this.subspeciesIndex = subspeciesIndex;
      return this;
   }

   public ItemDrop setVariant(int variantIndex) {
      this.variantIndex = variantIndex;
      return this;
   }

   public int getQuantity(EaglercraftRandom random, int bonus, int multiplier) {
      float roll = random.nextFloat();
      roll = Math.max(roll, 0.0F);
      if (roll > this.chance) {
         return 0;
      } else {
         if (!this.amountMultiplier) {
            multiplier = 1;
         }

         int min = this.minAmount;
         int max = this.maxAmount + (this.bonusAmount ? bonus : 0);
         if (max <= min) {
            return min * multiplier;
         } else {
            roll = roll / this.chance;
            float dropRange = (float)(max - min) * roll;
            int dropAmount = min + Math.round(dropRange);
            return Math.min(dropAmount * multiplier, this.getItemStack().getMaxStackSize());
         }
      }
   }

   public ItemStack getItemStack() {
      if (this.itemId == null) {
         return ItemStack.EMPTY;
      } else {
         Item item = (Item)GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(this.itemId));
         return item != null ? new ItemStack(item, 1, this.metadata) : ItemStack.EMPTY;
      }
   }

   public ItemStack getBurningItemStack() {
      if (this.burningItemId == null) {
         return this.getItemStack();
      } else {
         Item item = (Item)GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(this.burningItemId));
         return item != null ? new ItemStack(item, 1, this.burningMetadata) : this.getItemStack();
      }
   }

   public ItemStack getEffectItemStack(int effectId) {
      if (this.effectItemIds.containsKey(Integer.valueOf(effectId)) && this.effectItemMetadata.containsKey(Integer.valueOf(effectId))) {
         Item item = (Item)GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation((String)this.effectItemIds.get(Integer.valueOf(effectId))));
         return item != null ? new ItemStack(item, 1, ((Integer)this.effectItemMetadata.get(Integer.valueOf(effectId))).intValue()) : ItemStack.EMPTY;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public ItemStack getEntityDropItemStack(EntityLivingBase entity, int quantity) {
      ItemStack itemStack = this.getItemStack();
      if (entity != null) {
         if (entity.isBurning()) {
            itemStack = this.getBurningItemStack();
         }

         for(Object potionEffect : entity.getActivePotionEffects()) {
            if (potionEffect instanceof PotionEffect) {
               int effectId = Potion.getIdFromPotion(((PotionEffect)potionEffect).getPotion());
               ItemStack effectStack = this.getEffectItemStack(effectId);
               if (!effectStack.isEmpty()) {
                  itemStack = effectStack;
               }
            }
         }
      }

      itemStack.setCount(quantity);
      return itemStack;
   }

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      if (nbtTagCompound.hasKey("ItemId")) {
         this.itemId = nbtTagCompound.getString("ItemId");
      }

      if (nbtTagCompound.hasKey("Metadata")) {
         this.metadata = nbtTagCompound.getInteger("Metadata");
      }

      this.minAmount = nbtTagCompound.getInteger("MinAmount");
      this.maxAmount = nbtTagCompound.getInteger("MaxAmount");
      if (nbtTagCompound.hasKey("BonusAmount")) {
         this.bonusAmount = nbtTagCompound.getBoolean("BonusAmount");
      }

      this.chance = nbtTagCompound.getFloat("Chance");
      if (nbtTagCompound.hasKey("AmountMultiplier")) {
         this.amountMultiplier = nbtTagCompound.getBoolean("AmountMultiplier");
      }

      if (nbtTagCompound.hasKey("Subspecies")) {
         this.subspeciesIndex = nbtTagCompound.getInteger("Subspecies");
      }

      if (nbtTagCompound.hasKey("Variant")) {
         this.variantIndex = nbtTagCompound.getInteger("Variant");
      }

      if (nbtTagCompound.hasKey("AdultOnly")) {
         this.adultOnly = nbtTagCompound.getBoolean("AdultOnly");
      }

   }

   public boolean writeToNBT(NBTTagCompound nbtTagCompound) {
      if (this.itemId == null) {
         return false;
      } else {
         nbtTagCompound.setString("ItemId", this.itemId);
         nbtTagCompound.setInteger("Metadata", this.metadata);
         nbtTagCompound.setInteger("MinAmount", this.minAmount);
         nbtTagCompound.setInteger("MaxAmount", this.maxAmount);
         nbtTagCompound.setBoolean("BonusAmount", this.bonusAmount);
         nbtTagCompound.setFloat("Chance", this.chance);
         nbtTagCompound.setBoolean("AmountMultiplier", this.amountMultiplier);
         nbtTagCompound.setInteger("Subspecies", this.subspeciesIndex);
         nbtTagCompound.setInteger("Variant", this.variantIndex);
         nbtTagCompound.setBoolean("AdultOnly", this.adultOnly);
         return true;
      }
   }

   public String toConfigString() {
      return this.itemId + "," + this.metadata + "," + this.minAmount + "," + this.maxAmount + "," + this.chance;
   }
}
