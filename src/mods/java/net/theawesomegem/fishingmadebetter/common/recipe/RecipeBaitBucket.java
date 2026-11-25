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
import net.theawesomegem.fishingmadebetter.common.item.ItemBaitBucket;

public class RecipeBaitBucket extends Impl<IRecipe> implements IRecipe {
   public RecipeBaitBucket() {
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      return this.validInput(inv) != null;
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      Integer[] slots = this.validInput(inv);
      if (slots == null) {
         return ItemStack.EMPTY;
      } else {
         ItemStack itemBaitBucket = inv.getStackInSlot(slots[0].intValue()).copy();
         ItemStack itemBait = slots[1].intValue() != -1 ? inv.getStackInSlot(slots[1].intValue()).copy() : null;
         if (itemBait == null) {
            ItemBaitBucket.removeBait(itemBaitBucket);
         } else {
            int count = slots.length - 1;
            ItemBaitBucket.setBaitId(itemBaitBucket, ((ResourceLocation)Item.REGISTRY.getNameForObject(itemBait.getItem())).toString());
            ItemBaitBucket.setBaitMetadata(itemBaitBucket, itemBait.getMetadata());
            ItemBaitBucket.setBaitDisplayName(itemBaitBucket, itemBait.getDisplayName());
            ItemBaitBucket.setBaitCount(itemBaitBucket, ItemBaitBucket.getBaitCount(itemBaitBucket) + count);
         }

         return itemBaitBucket;
      }
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
      Integer[] slots = this.validInput(inv);
      if (slots == null) {
         return ret;
      } else {
         ItemStack itemBaitBucket = inv.getStackInSlot(slots[0].intValue()).copy();
         ItemStack itemBait = slots[1].intValue() != -1 ? inv.getStackInSlot(slots[1].intValue()).copy() : null;
         if (itemBait == null && Item.getByNameOrId(ItemBaitBucket.getBaitId(itemBaitBucket)) != null && ItemBaitBucket.getBaitCount(itemBaitBucket) > 0) {
            ItemStack oldBait = new ItemStack(Item.getByNameOrId(ItemBaitBucket.getBaitId(itemBaitBucket)), ItemBaitBucket.getBaitCount(itemBaitBucket), ItemBaitBucket.getBaitMetadata(itemBaitBucket));
            ret.set(0, oldBait);
            return ret;
         } else {
            return ret;
         }
      }
   }

   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }

   @Nullable
   private Integer[] validInput(InventoryCrafting inv) {
      int numStacks = 0;
      int bucketSlot = -1;
      List<Integer> baitSlots = new ArrayList();
      ItemStack bait = null;
      List<Integer> occupiedSlots = new ArrayList();

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         if (!inv.getStackInSlot(i).isEmpty()) {
            ++numStacks;
            occupiedSlots.add(Integer.valueOf(i));
         }
      }

      if (numStacks <= 0) {
         return null;
      } else {
         Iterator var10 = occupiedSlots.iterator();

         while(var10.hasNext()) {
            int i = ((Integer)var10.next()).intValue();
            ItemStack itemStack = inv.getStackInSlot(i);
            if (itemStack.isEmpty()) {
               return null;
            }

            if (itemStack.getItem() instanceof ItemBaitBucket && bucketSlot == -1) {
               bucketSlot = i;
            } else {
               if (!BetterFishUtil.isValidBait(itemStack) || bait != null && itemStack.getItem() != bait.getItem()) {
                  return null;
               }

               baitSlots.add(Integer.valueOf(i));
               bait = itemStack;
            }
         }

         if (bucketSlot == -1) {
            return null;
         } else {
            if (numStacks > 1 && !ItemBaitBucket.getBaitId(inv.getStackInSlot(bucketSlot)).isEmpty()) {
               if (!ItemBaitBucket.getBaitId(inv.getStackInSlot(bucketSlot)).equals(((ResourceLocation)Item.REGISTRY.getNameForObject(inv.getStackInSlot(((Integer)baitSlots.get(0)).intValue()).getItem())).toString())) {
                  return null;
               }

               if (ItemBaitBucket.getBaitMetadata(inv.getStackInSlot(bucketSlot)) != inv.getStackInSlot(((Integer)baitSlots.get(0)).intValue()).getMetadata()) {
                  return null;
               }

               if (ItemBaitBucket.getBaitCount(inv.getStackInSlot(bucketSlot)) + numStacks - 1 > 64) {
                  return null;
               }
            } else if (numStacks == 1 && (ItemBaitBucket.getBaitId(inv.getStackInSlot(bucketSlot)).isEmpty() || ItemBaitBucket.getBaitCount(inv.getStackInSlot(bucketSlot)) <= 0)) {
               return null;
            }

            Integer[] slots = new Integer[Math.max(2, numStacks)];
            slots[0] = bucketSlot;
            slots[1] = Integer.valueOf(-1);

            for(int i = 0; i < baitSlots.size(); ++i) {
               slots[i + 1] = (Integer)baitSlots.get(i);
            }

            return slots;
         }
      }
   }
}
