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
import java.util.HashMap;
import java.util.Map;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class MCRecipeShapeless extends MCRecipeBase {
   private final IIngredient[] ingredients;

   public MCRecipeShapeless(IIngredient[] ingredients, IItemStack output, IRecipeFunction recipeFunction, IRecipeAction recipeAction, boolean isHidden) {
      super(output, createIngredientList(ingredients), recipeFunction, recipeAction, isHidden);
      this.ingredients = ingredients;
   }

   private static NonNullList<Ingredient> createIngredientList(IIngredient[] ingredients) {
      NonNullList<Ingredient> ingredientList = NonNullList.withSize(ingredients.length, Ingredient.EMPTY);

      for(int index = 0; index < ingredients.length; ++index) {
         IIngredient ingredient = ingredients[index];
         if (ingredient != null) {
            ingredientList.set(index, CraftTweakerMC.getIngredient(ingredients[index]));
         }
      }

      return ingredientList;
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      boolean[] visited = new boolean[inv.getSizeInventory()];
      boolean matches = true;

      for(int ingredientIndex = 0; ingredientIndex < this.ingredients.length; ++ingredientIndex) {
         IIngredient ingredient = this.ingredients[ingredientIndex];
         int slot = 0;

         while(true) {
            if (slot >= inv.getSizeInventory()) {
               matches = false;
               break;
            }

            ItemStack stackInSlot = inv.getStackInSlot(slot);
            if (!visited[slot] && !stackInSlot.isEmpty() && ingredient.matches(CraftTweakerMC.getIItemStackForMatching(stackInSlot))) {
               visited[slot] = true;
               break;
            }

            ++slot;
         }
      }

      for(int slot = 0; slot < visited.length; ++slot) {
         if (!visited[slot] && !inv.getStackInSlot(slot).isEmpty()) {
            return false;
         }
      }

      return matches;
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      if (this.recipeFunction == null) {
         return this.outputStack.copy();
      } else {
         Map<String, IItemStack> marks = new HashMap();
         boolean[] visited = new boolean[inv.getSizeInventory()];

         for(int ingredientIndex = 0; ingredientIndex < this.ingredients.length; ++ingredientIndex) {
            IIngredient ingredient = this.ingredients[ingredientIndex];
            if (ingredient != null && ingredient.getMark() != null) {
               for(int slot = 0; slot < inv.getSizeInventory(); ++slot) {
                  ItemStack stackInSlot = inv.getStackInSlot(slot);
                  if (!stackInSlot.isEmpty() && !visited[slot]) {
                     IItemStack stack = CraftTweakerMC.getIItemStack(stackInSlot);
                     if (ingredient.matches(stack)) {
                        marks.put(ingredient.getMark(), stack);
                        visited[slot] = true;
                        break;
                     }
                  }
               }
            }
         }

         IItemStack out = null;

         try {
            out = this.recipeFunction.process(this.output, marks, new CraftingInfo(new MCCraftingInventorySquared(inv), (IWorld)null));
         } catch (Exception var9) {
            CraftTweakerAPI.logError("Could not execute RecipeFunction: ", var9);
         }

         return CraftTweakerMC.getItemStack(out);
      }
   }

   public boolean canFit(int width, int height) {
      return width * height >= this.ingredients.length;
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      NonNullList<ItemStack> out = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
      boolean[] visited = new boolean[inv.getSizeInventory()];

      for(int ingredientIndex = 0; ingredientIndex < this.ingredients.length; ++ingredientIndex) {
         for(int slot = 0; slot < inv.getSizeInventory(); ++slot) {
            ItemStack stackInSlot = inv.getStackInSlot(slot);
            if (!stackInSlot.isEmpty() && !visited[slot]) {
               IItemStack stack = CraftTweakerMC.getIItemStack(stackInSlot);
               IIngredient ingredient = this.ingredients[ingredientIndex];
               if (ingredient.matches(stack)) {
                  boolean needsContainerItem = true;
                  if (ingredient.hasNewTransformers()) {
                     IItemStack remainingItem = null;

                     try {
                        remainingItem = ingredient.applyNewTransform(stack);
                     } catch (Exception var12) {
                        CraftTweakerAPI.logError("Could not execute NewRecipeTransformer on " + ingredient.toCommandString() + ":", var12);
                     }

                     if (remainingItem != ItemStackUnknown.INSTANCE) {
                        out.set(slot, CraftTweakerMC.getItemStack(remainingItem));
                        needsContainerItem = false;
                     }
                  }

                  if (ingredient.hasTransformers()) {
                     stackInSlot.setCount(stackInSlot.getCount() + 1);
                     needsContainerItem = false;
                  }

                  if (needsContainerItem) {
                     out.set(slot, ForgeHooks.getContainerItem(stackInSlot));
                  }

                  visited[slot] = true;
                  break;
               }
            }
         }
      }

      return out;
   }

   public String toCommandString() {
      StringBuilder commandString = new StringBuilder("recipes.add");
      commandString.append(this.hidden ? "Hidden" : "");
      commandString.append("Shapeless(\"");
      commandString.append(this.getName()).append("\", ");
      commandString.append(this.output.toString()).append(", [");
      if (this.ingredients.length > 0) {
         for(IIngredient ingredient : this.ingredients) {
            commandString.append(ingredient.toCommandString()).append(", ");
         }

         commandString.deleteCharAt(commandString.length() - 1);
         commandString.deleteCharAt(commandString.length() - 1);
      }

      commandString.append("]);");
      return commandString.toString();
   }

   public boolean hasTransformers() {
      for(IIngredient ingredient : this.ingredients) {
         if (ingredient != null && ingredient.hasTransformers()) {
            return true;
         }
      }

      return false;
   }

   public IIngredient[] getIngredients1D() {
      return this.ingredients;
   }

   public IIngredient[][] getIngredients2D() {
      return new IIngredient[][]{this.ingredients};
   }

   public boolean isShaped() {
      return false;
   }

   public void applyTransformers(InventoryCrafting inventory, IPlayer byPlayer) {
      boolean[] visited = new boolean[inventory.getSizeInventory()];

      for(int ingredientIndex = 0; ingredientIndex < this.ingredients.length; ++ingredientIndex) {
         IIngredient ingredient = this.ingredients[ingredientIndex];

         for(int slot = 0; slot < inventory.getSizeInventory(); ++slot) {
            ItemStack stackInSlot = inventory.getStackInSlot(slot);
            if (!stackInSlot.isEmpty() && !visited[slot]) {
               IItemStack stack = CraftTweakerMC.getIItemStack(stackInSlot);
               if (ingredient.matches(stack)) {
                  IItemStack out = null;

                  try {
                     out = ingredient.applyTransform(stack, byPlayer);
                  } catch (Exception var11) {
                     CraftTweakerAPI.logError("Could not execute RecipeTransformer on " + ingredient.toCommandString() + ":", var11);
                  }

                  inventory.setInventorySlotContents(slot, CraftTweakerMC.getItemStack(out));
                  visited[slot] = true;
                  break;
               }
            }
         }
      }

   }

   public MCRecipeShapeless update() {
      this.ingredientList = createIngredientList(this.ingredients);
      return this;
   }
}
