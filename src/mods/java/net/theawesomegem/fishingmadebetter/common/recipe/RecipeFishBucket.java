package net.theawesomegem.fishingmadebetter.common.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import net.theawesomegem.fishingmadebetter.BetterFishUtil;
import net.theawesomegem.fishingmadebetter.common.configuration.CustomConfigurationHandler;
import net.theawesomegem.fishingmadebetter.common.data.FishData;
import net.theawesomegem.fishingmadebetter.common.item.ItemFishBucket;

public class RecipeFishBucket extends Impl<IRecipe> implements IRecipe {
   private long worldTime;

   public RecipeFishBucket() {
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      if (worldIn == null) {
         return false;
      } else {
         this.worldTime = worldIn.getTotalWorldTime();
         return this.validInput(inv) != null;
      }
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      Integer[] slots = this.validInput(inv);
      if (slots == null) {
         return ItemStack.EMPTY;
      } else {
         ItemStack itemFish = inv.getStackInSlot(slots[1].intValue()).copy();
         ItemStack fishBucket = ItemFishBucket.getItemStack(BetterFishUtil.getFishId(itemFish));
         return fishBucket;
      }
   }

   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
   }

   @Nullable
   private Integer[] validInput(InventoryCrafting inv) {
      int numStacks = 0;
      int bucketSlot = -1;
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

               if (itemStack.getItem() == Items.WATER_BUCKET) {
                  bucketSlot = i;
                  continue;
               }

               if (BetterFishUtil.isBetterFish(itemStack) && !BetterFishUtil.isDead(itemStack, this.worldTime) && ((FishData)CustomConfigurationHandler.fishDataMap.get(BetterFishUtil.getFishId(itemStack))).liquid.equals(FishData.FishingLiquid.WATER)) {
                  fishSlot = i;
                  continue;
               }

               return null;
            }

            Integer[] slots = new Integer[2];
            slots[0] = bucketSlot;
            slots[1] = fishSlot;
            return bucketSlot != -1 && fishSlot != -1 ? slots : null;
         }
      }
   }
}
