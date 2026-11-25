package crafttweaker.mc1120.recipes;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.item.ItemStackUnknown;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.recipes.CraftingInfo;
import crafttweaker.api.recipes.IRecipeAction;
import crafttweaker.api.recipes.IRecipeFunction;
import crafttweaker.api.world.IWorld;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.IShapedRecipe;
import stanhebben.zenscript.util.ArrayUtil;
import stanhebben.zenscript.util.Pair;

public class MCRecipeShaped extends MCRecipeBase implements IShapedRecipe {
   private static final Pair<Integer, Integer> offsetInvalid = new Pair<Integer, Integer>(Integer.valueOf(-1), Integer.valueOf(-1));
   private final IIngredient[][] ingredients;
   private final int width;
   private final int height;
   private final boolean isMirrored;

   public MCRecipeShaped(IIngredient[][] ingredients, IItemStack output, IRecipeFunction recipeFunction, IRecipeAction recipeAction, boolean isMirrored, boolean isHidden) {
      super(output, createIngredientList(ingredients), recipeFunction, recipeAction, isHidden);
      this.height = ingredients.length;
      this.isMirrored = isMirrored;
      int width = 0;

      for(IIngredient[] ingredientLine : ingredients) {
         width = Math.max(width, ingredientLine.length);
      }

      this.ingredients = ingredients;

      for(int index = 0; index < this.ingredients.length; ++index) {
         if (this.ingredients[index].length < width) {
            this.ingredients[index] = (IIngredient[])Arrays.copyOf(this.ingredients[index], width);
         }
      }

      this.width = width;
   }

   private static NonNullList<Ingredient> createIngredientList(IIngredient[][] ingredients) {
      int height = ingredients.length;
      int width = 0;

      for(IIngredient[] ingredientLine : ingredients) {
         width = Math.max(width, ingredientLine.length);
      }

      NonNullList<Ingredient> ingredientList = NonNullList.withSize(width * height, Ingredient.EMPTY);

      for(int row = 0; row < ingredients.length; ++row) {
         for(int column = 0; column < ingredients[row].length; ++column) {
            if (ingredients[row][column] != null) {
               ingredientList.set(row * width + column, CraftTweakerMC.getIngredient(ingredients[row][column]));
            }
         }
      }

      return ingredientList;
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      return this.calculateOffset(inv) != offsetInvalid;
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      Pair<Integer, Integer> offsetPair = this.checkRecipe(this.ingredients, inv);
      if (offsetPair == offsetInvalid && this.isMirrored) {
         IIngredient[][] ingredients = (IIngredient[][])ArrayUtil.inverse(this.ingredients, this.height);
         offsetPair = this.checkRecipe(ingredients, inv);
         if (offsetPair != offsetInvalid) {
            return this.getCraftingResult(inv, offsetPair, ingredients);
         } else {
            for(int i = 0; i < ingredients.length; ++i) {
               ingredients[i] = (IIngredient[])ArrayUtil.inverse(this.ingredients[i], this.width);
            }

            offsetPair = this.checkRecipe(ingredients, inv);
            if (offsetPair != offsetInvalid) {
               return this.getCraftingResult(inv, offsetPair, ingredients);
            } else {
               ingredients = (IIngredient[][])ArrayUtil.inverse(ingredients, this.height);
               offsetPair = this.checkRecipe(ingredients, inv);
               return this.getCraftingResult(inv, offsetPair, ingredients);
            }
         }
      } else {
         return this.getCraftingResult(inv, offsetPair, this.ingredients);
      }
   }

   private ItemStack getCraftingResult(InventoryCrafting inv, Pair<Integer, Integer> offsetPair, IIngredient[][] ingredients) {
      if (offsetPair == offsetInvalid) {
         return ItemStack.EMPTY;
      } else {
         int rowOffset = ((Integer)offsetPair.getKey()).intValue();
         int columnOffset = ((Integer)offsetPair.getValue()).intValue();
         if (this.recipeFunction == null) {
            return CraftTweakerMC.getItemStack(this.output);
         } else {
            Map<String, IItemStack> marks = new HashMap();

            for(int row = 0; row < ingredients.length; ++row) {
               for(int column = 0; column < ingredients[row].length; ++column) {
                  IIngredient ingredient = ingredients[row][column];
                  if (ingredient != null && ingredient.getMark() != null) {
                     marks.put(ingredient.getMark(), CraftTweakerMC.getIItemStack(inv.getStackInRowAndColumn(column + columnOffset, row + rowOffset)));
                  }
               }
            }

            IItemStack out = null;

            try {
               out = this.recipeFunction.process(this.output, marks, new CraftingInfo(MCCraftingInventorySquared.get(inv), (IWorld)null));
            } catch (Exception var10) {
               CraftTweakerAPI.logError("Could not execute RecipeFunction: ", var10);
            }

            return CraftTweakerMC.getItemStack(out);
         }
      }
   }

   public boolean canFit(int width, int height) {
      return this.width <= width && this.height <= height;
   }

   public ItemStack getRecipeOutput() {
      return CraftTweakerMC.getItemStack(this.output);
   }

   public void applyTransformers(InventoryCrafting inventory, IPlayer byPlayer) {
      Pair<Integer, Integer> offsetPair = this.checkRecipe(this.ingredients, inventory);
      if (offsetPair == offsetInvalid && this.isMirrored) {
         IIngredient[][] ingredients = (IIngredient[][])ArrayUtil.inverse(this.ingredients, this.height);
         offsetPair = this.checkRecipe(ingredients, inventory);
         if (offsetPair != offsetInvalid) {
            this.applyTransformers(inventory, byPlayer, offsetPair, ingredients);
         } else {
            for(int i = 0; i < ingredients.length; ++i) {
               ingredients[i] = (IIngredient[])ArrayUtil.inverse(this.ingredients[i], this.width);
            }

            offsetPair = this.checkRecipe(ingredients, inventory);
            if (offsetPair != offsetInvalid) {
               this.applyTransformers(inventory, byPlayer, offsetPair, ingredients);
            } else {
               ingredients = (IIngredient[][])ArrayUtil.inverse(ingredients, this.height);
               offsetPair = this.checkRecipe(ingredients, inventory);
               this.applyTransformers(inventory, byPlayer, offsetPair, ingredients);
            }
         }
      } else {
         this.applyTransformers(inventory, byPlayer, offsetPair, this.ingredients);
      }
   }

   private void applyTransformers(InventoryCrafting inventory, IPlayer byPlayer, Pair<Integer, Integer> offsetPair, IIngredient[][] ingredients) {
      if (offsetPair != offsetInvalid) {
         int rowOffset = ((Integer)offsetPair.getKey()).intValue();
         int columnOffset = ((Integer)offsetPair.getValue()).intValue();

         for(int column = 0; column < this.height; ++column) {
            for(int row = 0; row < this.width; ++row) {
               ItemStack itemStack = inventory.getStackInSlot(column + columnOffset + (row + rowOffset) * inventory.getWidth());
               if (ingredients.length > row && ingredients[row].length > column) {
                  IIngredient ingredient = ingredients[row][column];
                  if (ingredient != null && ingredient.hasTransformers()) {
                     IItemStack out = null;

                     try {
                        out = ingredient.applyTransform(CraftTweakerMC.getIItemStack(itemStack), byPlayer);
                     } catch (Exception var13) {
                        CraftTweakerAPI.logError("Could not execute RecipeTransformer on : " + ingredient.toCommandString(), var13);
                     }

                     inventory.setInventorySlotContents(column + columnOffset + (row + rowOffset) * inventory.getWidth(), CraftTweakerMC.getItemStack(out));
                  }
               }
            }
         }

      }
   }

   public MCRecipeShaped update() {
      this.ingredientList = createIngredientList(this.ingredients);
      return this;
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      Pair<Integer, Integer> offsetPair = this.checkRecipe(this.ingredients, inv);
      if (offsetPair == offsetInvalid && this.isMirrored) {
         IIngredient[][] ingredients = (IIngredient[][])ArrayUtil.inverse(this.ingredients, this.height);
         offsetPair = this.checkRecipe(ingredients, inv);
         if (offsetPair != offsetInvalid) {
            return this.getRemainingItems(inv, offsetPair, ingredients);
         } else {
            for(int i = 0; i < ingredients.length; ++i) {
               ingredients[i] = (IIngredient[])ArrayUtil.inverse(this.ingredients[i], this.width);
            }

            offsetPair = this.checkRecipe(ingredients, inv);
            if (offsetPair != offsetInvalid) {
               return this.getRemainingItems(inv, offsetPair, ingredients);
            } else {
               ingredients = (IIngredient[][])ArrayUtil.inverse(ingredients, this.height);
               offsetPair = this.checkRecipe(ingredients, inv);
               return this.getRemainingItems(inv, offsetPair, ingredients);
            }
         }
      } else {
         return this.getRemainingItems(inv, offsetPair, this.ingredients);
      }
   }

   private NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv, Pair<Integer, Integer> offsetPair, IIngredient[][] ingredients) {
      NonNullList<ItemStack> out = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
      if (offsetPair == offsetInvalid) {
         return out;
      } else {
         int rowOffset = ((Integer)offsetPair.getKey()).intValue();
         int columnOffset = ((Integer)offsetPair.getValue()).intValue();

         for(int column = 0; column < this.width; ++column) {
            for(int row = 0; row < this.height; ++row) {
               ItemStack itemStack = inv.getStackInSlot(column + columnOffset + (row + rowOffset) * inv.getWidth());
               if (ingredients.length > row && ingredients[row].length > column) {
                  IIngredient ingredient = ingredients[row][column];
                  boolean needsContainerItem = true;
                  if (ingredient != null) {
                     if (ingredient.hasNewTransformers()) {
                        IItemStack remainingItem = null;

                        try {
                           remainingItem = ingredient.applyNewTransform(CraftTweakerMC.getIItemStack(itemStack));
                        } catch (Exception var14) {
                           CraftTweakerAPI.logError("Could not execute NewRecipeTransformer on " + ingredient.toCommandString() + ":", var14);
                        }

                        if (remainingItem != ItemStackUnknown.INSTANCE) {
                           out.set(column + columnOffset + (row + rowOffset) * inv.getWidth(), CraftTweakerMC.getItemStack(remainingItem));
                           needsContainerItem = false;
                        }
                     }

                     if (ingredient.hasTransformers()) {
                        itemStack.setCount(itemStack.getCount() + 1);
                        needsContainerItem = false;
                     }

                     if (needsContainerItem) {
                        out.set(column + columnOffset + (row + rowOffset) * inv.getWidth(), ForgeHooks.getContainerItem(itemStack));
                     }
                  }
               }
            }
         }

         return out;
      }
   }

   public IIngredient[][] getIIngredients() {
      return this.ingredients;
   }

   private Pair<Integer, Integer> calculateOffset(InventoryCrafting inv) {
      Pair<Integer, Integer> offset = this.checkRecipe(this.ingredients, inv);
      if (offset == offsetInvalid && this.isMirrored) {
         offset = this.checkRecipe((IIngredient[][])ArrayUtil.inverse(this.ingredients, this.height), inv);
         if (offset != offsetInvalid) {
            return offset;
         } else {
            IIngredient[][] ingredients = new IIngredient[this.ingredients.length][];

            for(int i = 0; i < ingredients.length; ++i) {
               ingredients[i] = (IIngredient[])ArrayUtil.inverse(this.ingredients[i], this.width);
            }

            offset = this.checkRecipe(ingredients, inv);
            return offset != offsetInvalid ? offset : this.checkRecipe((IIngredient[][])ArrayUtil.inverse(ingredients, this.height), inv);
         }
      } else {
         return offset;
      }
   }

   private Pair<Integer, Integer> checkRecipe(IIngredient[][] ingredients, InventoryCrafting inv) {
      boolean[] visited = new boolean[inv.getSizeInventory()];

      for(int rowOffset = 0; rowOffset <= inv.getHeight() - this.height; ++rowOffset) {
         label83:
         for(int columnOffset = 0; columnOffset <= inv.getWidth() - this.width; ++columnOffset) {
            int row = 0;

            while(true) {
               if (row >= ingredients.length) {
                  for(int slot = 0; slot < visited.length; ++slot) {
                     if (!visited[slot] && !inv.getStackInSlot(slot).isEmpty()) {
                        return offsetInvalid;
                     }
                  }

                  for(int slot = 0; slot < inv.getSizeInventory(); ++slot) {
                     int row = slot / inv.getWidth() - rowOffset;
                     int column = slot % inv.getWidth() - columnOffset;
                     if ((row < 0 || column < 0) && !inv.getStackInSlot(slot).isEmpty()) {
                        return offsetInvalid;
                     }
                  }

                  return new Pair<Integer, Integer>(rowOffset, columnOffset);
               }

               for(int column = 0; column < ingredients[row].length; ++column) {
                  ItemStack itemStack = inv.getStackInRowAndColumn(column + columnOffset, row + rowOffset);
                  if (ingredients[row][column] == null) {
                     if (!itemStack.isEmpty()) {
                        return offsetInvalid;
                     }
                  } else {
                     if (itemStack.isEmpty() || !ingredients[row][column].matches(CraftTweakerMC.getIItemStackForMatching(itemStack))) {
                        continue label83;
                     }

                     visited[column + columnOffset + (row + rowOffset) * inv.getWidth()] = true;
                  }
               }

               ++row;
            }
         }
      }

      return offsetInvalid;
   }

   public String toCommandString() {
      StringBuilder commandString = new StringBuilder("recipes.add");
      commandString.append(this.hidden ? "Hidden" : "");
      commandString.append("Shaped(\"");
      commandString.append(this.getName()).append("\", ");
      commandString.append(this.output.toString()).append(", [");
      if (this.height > 0 && this.width > 0) {
         for(int row = 0; row < this.height; ++row) {
            commandString.append("[");

            for(int column = 0; column < this.width; ++column) {
               IIngredient ingredient = this.ingredients[row][column];
               commandString.append(ingredient == null ? "null" : ingredient.toCommandString()).append(", ");
            }

            commandString.deleteCharAt(commandString.length() - 1);
            commandString.deleteCharAt(commandString.length() - 1);
            commandString.append("], ");
         }

         commandString.deleteCharAt(commandString.length() - 1);
         commandString.deleteCharAt(commandString.length() - 1);
      }

      return commandString.append("]);").toString();
   }

   public boolean hasTransformers() {
      for(IIngredient[] row : this.ingredients) {
         for(IIngredient ingredient : row) {
            if (ingredient != null && ingredient.hasTransformers()) {
               return true;
            }
         }
      }

      return false;
   }

   public IIngredient[] getIngredients1D() {
      IIngredient[] out = new IIngredient[this.ingredientList.size()];
      int index = 0;

      for(IIngredient[] row : this.ingredients) {
         for(IIngredient ingredient : row) {
            out[index++] = ingredient;
         }
      }

      return out;
   }

   public IIngredient[][] getIngredients2D() {
      return this.ingredients;
   }

   public boolean isShaped() {
      return true;
   }

   public int getRecipeWidth() {
      return this.width;
   }

   public int getRecipeHeight() {
      return this.height;
   }
}
