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
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import net.theawesomegem.fishingmadebetter.BetterFishUtil;
import net.theawesomegem.fishingmadebetter.common.configuration.CustomConfigurationHandler;
import net.theawesomegem.fishingmadebetter.common.data.FishData;
import net.theawesomegem.fishingmadebetter.common.item.scalingknife.ItemScalingKnife;

public class RecipeFishScale extends Impl<IRecipe> implements IRecipe {
   public RecipeFishScale() {
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
         FishData fishData = (FishData)CustomConfigurationHandler.fishDataMap.get(BetterFishUtil.getFishId(itemStackFish));
         if (fishData == null) {
            return ItemStack.EMPTY;
         } else {
            Item itemResult = Item.getByNameOrId(fishData.scalingItem);
            if (itemResult == null) {
               return ItemStack.EMPTY;
            } else {
               int quantity = 1;
               int fishWeight = BetterFishUtil.getFishWeight(itemStackFish);
               if (fishData.scalingUseWeight) {
                  quantity = Math.min(this.getScaleAmount(fishWeight), 64);
               }

               return new ItemStack(itemResult, quantity, fishData.scalingItemMetadata);
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
      ItemStack itemStackFish = inv.getStackInSlot(slots[1].intValue()).copy();
      Integer remain = Integer.valueOf(0);
      itemStackFish = itemStackFish.splitStack(1);
      FishData fishData = (FishData)CustomConfigurationHandler.fishDataMap.get(BetterFishUtil.getFishId(itemStackFish));
      int fishWeight = BetterFishUtil.getFishWeight(itemStackFish);
      BetterFishUtil.setFishHasScale(itemStackFish, false);
      itemStackKnife.setItemDamage(itemStackKnife.getItemDamage() + (fishData.scalingUseWeight ? this.getScaleAmount(fishWeight) : 1));
      if (itemStackKnife.getItemDamage() >= itemStackKnife.getMaxDamage()) {
         itemStackKnife = ItemStack.EMPTY;
      }

      NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
      if (fishData.scalingUseWeight) {
         remain = this.getScaleAmount(fishWeight) - 64;
      }

      for(int i = 0; i < ret.size(); ++i) {
         ItemStack itemStack = inv.getStackInSlot(i);
         if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemScalingKnife) {
            ret.set(i, itemStackKnife);
         } else if (!itemStack.isEmpty()) {
            ret.set(i, itemStackFish);
         } else if (itemStack.isEmpty() && remain.intValue() > 0) {
            if (remain.intValue() >= 64) {
               ret.set(i, new ItemStack(Item.getByNameOrId(fishData.scalingItem), 64, fishData.scalingItemMetadata));
            } else {
               ret.set(i, new ItemStack(Item.getByNameOrId(fishData.scalingItem), remain.intValue(), fishData.scalingItemMetadata));
            }

            remain = remain.intValue() - 64;
         }
      }

      return ret;
   }

   private int getScaleAmount(int weight) {
      return Math.max(1, (int)Math.cbrt(Math.cbrt(Math.pow((double)weight, 4.0D))));
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

         while(true) {
            if (var9.hasNext()) {
               int i = ((Integer)var9.next()).intValue();
               ItemStack itemStack = inv.getStackInSlot(i);
               if (itemStack.isEmpty()) {
                  return null;
               }

               if (itemStack.getItem() instanceof ItemScalingKnife && itemStack.getMaxDamage() > itemStack.getItemDamage()) {
                  knifeSlot = i;
                  continue;
               }

               if (BetterFishUtil.isBetterFish(itemStack) && BetterFishUtil.doesFishHasScale(itemStack)) {
                  fishSlot = i;
                  continue;
               }

               return null;
            }

            Integer[] slots = new Integer[2];
            slots[0] = knifeSlot;
            slots[1] = fishSlot;
            return knifeSlot != -1 && fishSlot != -1 ? slots : null;
         }
      }
   }
}
