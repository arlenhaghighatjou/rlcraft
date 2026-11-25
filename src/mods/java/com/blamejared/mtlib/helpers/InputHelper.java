package com.blamejared.mtlib.helpers;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.IngredientStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.mc1120.item.MCItemStack;
import crafttweaker.mc1120.liquid.MCLiquidStack;
import gnu.trove.set.TCharSet;
import gnu.trove.set.hash.TCharHashSet;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class InputHelper {
   public InputHelper() {
   }

   public static boolean isABlock(IItemStack block) {
      return isABlock(toStack(block));
   }

   public static <T> T[][] getMultiDimensionalArray(Class<T> clazz, T[] array, int height, int width) {
      T[][] multiDim = (T[][])((Object[][])Array.newInstance(clazz, new int[]{height, width}));

      for(int y = 0; y < height; ++y) {
         for(int x = 0; x < width; ++x) {
            multiDim[y][x] = array[x + y * width];
         }
      }

      return multiDim;
   }

   public static IItemStack[] toStacks(IIngredient[] iIngredient) {
      ArrayList<IItemStack> stacks = new ArrayList();

      for(IIngredient ing : iIngredient) {
         for(IItemStack stack : ing.getItems()) {
            stacks.add(stack);
         }
      }

      return (IItemStack[])stacks.toArray(new IItemStack[stacks.size()]);
   }

   public static boolean isABlock(ItemStack block) {
      return block.getItem() instanceof ItemBlock;
   }

   public static ItemStack toStack(IItemStack iStack) {
      if (iStack == null) {
         return ItemStack.EMPTY;
      } else {
         Object internal = iStack.getInternal();
         if (!(internal instanceof ItemStack)) {
            LogHelper.logError("Not a valid item stack: " + iStack);
         }

         return (ItemStack)internal;
      }
   }

   public static IIngredient toIngredient(ItemStack stack) {
      return toIItemStack(stack);
   }

   public static IIngredient toIngredient(FluidStack stack) {
      return stack == null ? null : new MCLiquidStack(stack);
   }

   public static IItemStack toIItemStack(ItemStack stack) {
      return stack.isEmpty() ? null : new MCItemStack(stack);
   }

   public static ILiquidStack toILiquidStack(FluidStack stack) {
      return stack == null ? null : new MCLiquidStack(stack);
   }

   public static ItemStack[] toStacks(IItemStack[] iStack) {
      if (iStack == null) {
         return null;
      } else {
         ItemStack[] output = new ItemStack[iStack.length];

         for(int i = 0; i < iStack.length; ++i) {
            output[i] = toStack(iStack[i]);
         }

         return output;
      }
   }

   public static Object toObject(IIngredient iStack) {
      if (iStack == null) {
         return null;
      } else if (iStack instanceof IOreDictEntry) {
         return toString((IOreDictEntry)iStack);
      } else if (iStack instanceof IItemStack) {
         return toStack((IItemStack)iStack);
      } else {
         return iStack instanceof IngredientStack ? ((IngredientStack)iStack).getItems() : null;
      }
   }

   public static Object[] toObjects(IIngredient[] ingredient) {
      if (ingredient == null) {
         return null;
      } else {
         Object[] output = new Object[ingredient.length];

         for(int i = 0; i < ingredient.length; ++i) {
            if (ingredient[i] != null) {
               output[i] = toObject(ingredient[i]);
            } else {
               output[i] = "";
            }
         }

         return output;
      }
   }

   public static String toString(IOreDictEntry entry) {
      return entry.getName();
   }

   public static FluidStack toFluid(ILiquidStack iStack) {
      return iStack == null ? null : (FluidStack)iStack.getInternal();
   }

   public static Fluid getFluid(ILiquidStack iStack) {
      return iStack == null ? null : ((FluidStack)iStack.getInternal()).getFluid();
   }

   public static FluidStack[] toFluids(ILiquidStack[] iStack) {
      FluidStack[] stack = new FluidStack[iStack.length];

      for(int i = 0; i < stack.length; ++i) {
         stack[i] = toFluid(iStack[i]);
      }

      return stack;
   }

   public static Object[] toShapedObjects(IIngredient[][] ingredients) {
      if (ingredients == null) {
         return null;
      } else {
         ArrayList<Object> prep = new ArrayList();
         TCharSet usedCharSet = new TCharHashSet();
         prep.add("abc");
         prep.add("def");
         prep.add("ghi");
         char[][] map = new char[][]{{'a', 'b', 'c'}, {'d', 'e', 'f'}, {'g', 'h', 'i'}};

         for(int x = 0; x < ingredients.length; ++x) {
            if (ingredients[x] != null) {
               for(int y = 0; y < ingredients[x].length; ++y) {
                  if (ingredients[x][y] != null && x < map.length && y < map[x].length) {
                     prep.add(Character.valueOf(map[x][y]));
                     usedCharSet.add(map[x][y]);
                     prep.add(toObject(ingredients[x][y]));
                  }
               }
            }
         }

         for(int i = 0; i < 3; ++i) {
            StringBuilder sb = new StringBuilder();
            if (prep.get(i) instanceof String) {
               String s = (String)prep.get(i);

               for(int j = 0; j < 3; ++j) {
                  char c = s.charAt(j);
                  if (usedCharSet.contains(c)) {
                     sb.append(c);
                  } else {
                     sb.append(" ");
                  }
               }

               prep.set(i, sb.toString());
            }
         }

         return prep.toArray();
      }
   }

   public static <R> NonNullList<R> toNonNullList(R[] items) {
      NonNullList<R> nonNullList = NonNullList.create();
      Collections.addAll(nonNullList, items);
      return nonNullList;
   }
}
