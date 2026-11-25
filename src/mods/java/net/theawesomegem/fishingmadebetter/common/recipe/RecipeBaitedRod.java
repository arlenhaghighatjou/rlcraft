package net.theawesomegem.fishingmadebetter.common.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import net.theawesomegem.fishingmadebetter.BetterFishUtil;
import net.theawesomegem.fishingmadebetter.common.item.fishingrod.ItemBetterFishingRod;

public class RecipeBaitedRod extends Impl<IRecipe> implements IRecipe {
   public RecipeBaitedRod() {
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      return this.validInput(inv) != null;
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      Integer[] slots = this.validInput(inv);
      if (slots == null) {
         return ItemStack.EMPTY;
      } else {
         ItemStack itemFishingRod = inv.getStackInSlot(slots[0].intValue()).copy();
         ItemStack itemBait = inv.getStackInSlot(slots[1].intValue()).copy();
         ItemBetterFishingRod.setBaitItem(itemFishingRod, ((ResourceLocation)Item.REGISTRY.getNameForObject(itemBait.getItem())).toString());
         ItemBetterFishingRod.setBaitMetadata(itemFishingRod, itemBait.getMetadata());
         ItemBetterFishingRod.setBaitDisplayName(itemFishingRod, itemBait.getDisplayName());
         return itemFishingRod;
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
      int rodSlot = -1;
      int baitSlot = -1;
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

            if (itemStack.getItem() instanceof ItemBetterFishingRod) {
               rodSlot = i;
            } else {
               if (!BetterFishUtil.isValidBait(itemStack)) {
                  return null;
               }

               baitSlot = i;
            }
         }

         Integer[] slots = new Integer[2];
         slots[0] = rodSlot;
         slots[1] = baitSlot;
         return rodSlot != -1 && baitSlot != -1 ? slots : null;
      }
   }
}
