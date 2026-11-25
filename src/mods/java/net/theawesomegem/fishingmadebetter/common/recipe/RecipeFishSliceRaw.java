package net.theawesomegem.fishingmadebetter.common.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import net.theawesomegem.fishingmadebetter.BetterFishUtil;
import net.theawesomegem.fishingmadebetter.common.configuration.CustomConfigurationHandler;
import net.theawesomegem.fishingmadebetter.common.data.FishData;
import net.theawesomegem.fishingmadebetter.common.item.ItemManager;
import net.theawesomegem.fishingmadebetter.common.item.filletknife.ItemFilletKnife;

public class RecipeFishSliceRaw extends Impl<IRecipe> implements IRecipe {
   public RecipeFishSliceRaw() {
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      return this.validInput(inv) != null;
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      Integer[] slots = this.validInput(inv);
      if (slots == null) {
         return ItemStack.EMPTY;
      } else {
         ItemStack itemStackFish = inv.getStackInSlot(slots[1].intValue());
         if (!BetterFishUtil.isBetterFish(itemStackFish)) {
            return new ItemStack(ItemManager.FISH_SLICE_RAW, 1);
         } else {
            String fishId = BetterFishUtil.getFishId(itemStackFish);
            int fishWeight = BetterFishUtil.getFishWeight(itemStackFish);
            FishData fishData = (FishData)CustomConfigurationHandler.fishDataMap.get(fishId);
            if (fishData.defaultFillet) {
               return ItemManager.FISH_SLICE_RAW.getItemStack(fishId, fishId, fishData.filletUseWeight ? Math.min(this.getSliceAmount(fishWeight), 64) : 1);
            } else {
               Item ret = Item.getByNameOrId(fishData.filletItem);
               return new ItemStack(ret, fishData.filletUseWeight ? Math.min(this.getSliceAmount(fishWeight), 64) : 1, fishData.filletItemMetadata);
            }
         }
      }
   }

   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      Integer[] slots = this.validInput(inv);
      ItemStack itemStackKnife = inv.getStackInSlot(slots[0].intValue()).copy();
      ItemStack itemStackFish = inv.getStackInSlot(slots[1].intValue());
      Integer remain = Integer.valueOf(0);
      if (!BetterFishUtil.isBetterFish(itemStackFish)) {
         itemStackKnife.setItemDamage(itemStackKnife.getItemDamage() + 1);
      } else {
         itemStackKnife.setItemDamage(itemStackKnife.getItemDamage() + (((FishData)CustomConfigurationHandler.fishDataMap.get(BetterFishUtil.getFishId(itemStackFish))).filletUseWeight ? this.getSliceAmount(BetterFishUtil.getFishWeight(itemStackFish)) : 1));
      }

      if (itemStackKnife.getItemDamage() >= itemStackKnife.getMaxDamage()) {
         itemStackKnife = ItemStack.EMPTY;
      }

      NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
      if (BetterFishUtil.isBetterFish(itemStackFish) && ((FishData)CustomConfigurationHandler.fishDataMap.get(BetterFishUtil.getFishId(itemStackFish))).filletUseWeight) {
         remain = this.getSliceAmount(BetterFishUtil.getFishWeight(itemStackFish)) - 64;
      }

      for(int i = 0; i < ret.size(); ++i) {
         ItemStack itemStack = inv.getStackInSlot(i);
         if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemFilletKnife) {
            ret.set(i, itemStackKnife);
         } else if (itemStack.isEmpty() && remain.intValue() > 0) {
            String fishId = BetterFishUtil.getFishId(itemStackFish);
            FishData fishData = (FishData)CustomConfigurationHandler.fishDataMap.get(fishId);
            if (remain.intValue() >= 64) {
               ret.set(i, fishData.defaultFillet ? ItemManager.FISH_SLICE_RAW.getItemStack(fishId, fishId, 64) : new ItemStack(Item.getByNameOrId(fishData.filletItem), 64, fishData.filletItemMetadata));
            } else {
               ret.set(i, fishData.defaultFillet ? ItemManager.FISH_SLICE_RAW.getItemStack(fishId, fishId, remain.intValue()) : new ItemStack(Item.getByNameOrId(fishData.filletItem), remain.intValue(), fishData.filletItemMetadata));
            }

            remain = remain.intValue() - 64;
         }
      }

      return ret;
   }

   private int getSliceAmount(int weight) {
      return Math.max(1, (int)Math.cbrt(Math.pow((double)weight, 2.0D)));
   }

   @Nullable
   private Integer[] validInput(InventoryCrafting inv) {
      int numStacks = 0;
      int knifeSlot = -1;
      int fishSlot = -1;
      List<Integer> occupiedSlots = new ArrayList();

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         if (!inv.getStackInSlot(i).isEmpty()) {
            ++numStacks;
            occupiedSlots.add(Integer.valueOf(i));
         }
      }

      if (numStacks != 2) {
         return null;
      } else {
         Iterator var9 = occupiedSlots.iterator();

         while(var9.hasNext()) {
            int i = ((Integer)var9.next()).intValue();
            ItemStack itemStack = inv.getStackInSlot(i);
            if (itemStack.isEmpty()) {
               return null;
            }

            if (itemStack.getItem() instanceof ItemFilletKnife && itemStack.getMaxDamage() > itemStack.getItemDamage()) {
               knifeSlot = i;
            } else if (BetterFishUtil.isBetterFish(itemStack) && ((FishData)CustomConfigurationHandler.fishDataMap.get(BetterFishUtil.getFishId(itemStack))).allowFillet) {
               fishSlot = i;
            } else {
               if (!itemStack.getItem().getRegistryName().equals(new ResourceLocation("minecraft:fish"))) {
                  return null;
               }

               fishSlot = i;
            }
         }

         Integer[] slots = new Integer[2];
         slots[0] = knifeSlot;
         slots[1] = fishSlot;
         return knifeSlot != -1 && fishSlot != -1 ? slots : null;
      }
   }
}
