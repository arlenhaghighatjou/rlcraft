package com.blamejared.mtlib.helpers;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class StackHelper {
   public StackHelper() {
   }

   public static boolean areEqual(ItemStack stack1, ItemStack stack2) {
      return !stack1.isEmpty() && !stack2.isEmpty() ? stack1.isItemEqual(stack2) : false;
   }

   public static boolean areEqual(FluidStack stack1, FluidStack stack2) {
      return stack1 != null && stack2 != null ? stack1.isFluidEqual(stack2) : false;
   }

   public static boolean areEqualOrNull(ItemStack stack1, ItemStack stack2) {
      return stack1 == stack2 ? true : areEqual(stack1, stack2);
   }

   public static boolean areEqualOrNull(FluidStack stack1, FluidStack stack2) {
      return stack1 == stack2 ? true : areEqual(stack1, stack2);
   }

   public static boolean matches(IIngredient ingredient, IItemStack itemStack) {
      if (ingredient == null) {
         return false;
      } else {
         return ingredient.matches(itemStack);
      }
   }

   public static boolean matches(IIngredient ingredient, IItemStack[] itemStack) {
      if (ingredient == null) {
         return false;
      } else {
         for(IItemStack stack : itemStack) {
            if (!ingredient.matches(stack)) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean matches(IIngredient ingredient, ILiquidStack liquidStack) {
      if (ingredient == null) {
         return false;
      } else if (ingredient.matches(liquidStack)) {
         return true;
      } else {
         if (ingredient.getLiquids() != null) {
            for(ILiquidStack liquid : ingredient.getLiquids()) {
               if (InputHelper.toFluid(liquid).isFluidEqual(InputHelper.toFluid(liquidStack))) {
                  return true;
               }
            }
         }

         return false;
      }
   }
}
