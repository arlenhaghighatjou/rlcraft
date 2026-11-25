package net.mcft.copy.backpacks.item.recipe;

import java.util.ArrayList;
import java.util.List;
import net.mcft.copy.backpacks.item.IDyeableItem;
import net.mcft.copy.backpacks.misc.util.DyeUtils;
import net.mcft.copy.backpacks.misc.util.NbtUtils;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class RecipeDyeableItem extends Impl<IRecipe> implements IRecipe {
   public RecipeDyeableItem() {
   }

   public boolean isDynamic() {
      return true;
   }

   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }

   public boolean matches(InventoryCrafting crafting, World world) {
      boolean hasDyeable = false;
      boolean hasDyes = false;

      for(int i = 0; i < crafting.getSizeInventory(); ++i) {
         ItemStack stack = crafting.getStackInSlot(i);
         if (!stack.isEmpty()) {
            if (DyeUtils.isDye(stack)) {
               hasDyes = true;
            } else {
               if (!(stack.getItem() instanceof IDyeableItem)) {
                  return false;
               }

               if (!((IDyeableItem)stack.getItem()).canDye(stack)) {
                  return false;
               }

               if (hasDyeable) {
                  return false;
               }

               hasDyeable = true;
            }
         }
      }

      return hasDyeable && hasDyes;
   }

   public ItemStack getCraftingResult(InventoryCrafting crafting) {
      ItemStack dyeable = ItemStack.EMPTY;
      List<ItemStack> dyes = new ArrayList();

      for(int i = 0; i < crafting.getSizeInventory(); ++i) {
         ItemStack stack = crafting.getStackInSlot(i);
         if (!stack.isEmpty()) {
            if (DyeUtils.isDye(stack)) {
               dyes.add(stack);
            } else {
               if (!(stack.getItem() instanceof IDyeableItem)) {
                  return ItemStack.EMPTY;
               }

               if (!((IDyeableItem)stack.getItem()).canDye(stack)) {
                  return ItemStack.EMPTY;
               }

               if (!dyeable.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               dyeable = stack.copy();
            }
         }
      }

      if (dyes.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         int oldColor = ((Integer)NbtUtils.get(dyeable, Integer.valueOf(-1), "display", "color")).intValue();
         int newColor = DyeUtils.getColorFromDyes(oldColor, dyes);
         NbtUtils.set(dyeable, Integer.valueOf(newColor), "display", "color");
         return dyeable;
      }
   }
}
