package crafttweaker.api.recipes;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IWorld;
import java.util.HashMap;
import java.util.Map;

/** @deprecated */
@Deprecated
public class ShapedRecipe implements ICraftingRecipe {
   private final int width;
   private final int height;
   private final byte[] posx;
   private final byte[] posy;
   private final boolean mirrored;
   private final IRecipeFunction function;
   private final IRecipeAction action;
   private final IItemStack output;
   private final IIngredient[] ingredients;
   private final String name;

   public ShapedRecipe(IItemStack output, IIngredient[][] ingredients, IRecipeFunction function, IRecipeAction action, boolean mirrored) {
      this("", output, ingredients, function, action, mirrored);
   }

   public ShapedRecipe(String name, IItemStack output, IIngredient[][] ingredients, IRecipeFunction function, IRecipeAction action, boolean mirrored) {
      int numIngredients = 0;

      for(IIngredient[] ingredient : ingredients) {
         for(IIngredient anIngredient : ingredient) {
            if (anIngredient != null) {
               ++numIngredients;
            }
         }
      }

      this.posx = new byte[numIngredients];
      this.posy = new byte[numIngredients];
      this.output = output;
      this.ingredients = new IIngredient[numIngredients];
      this.function = function;
      this.action = action;
      this.name = name;
      int width1 = 0;
      int height1 = ingredients.length;
      int ix = 0;

      for(int j = 0; j < ingredients.length; ++j) {
         IIngredient[] row = ingredients[j];
         width1 = Math.max(width1, row.length);

         for(int i = 0; i < row.length; ++i) {
            if (row[i] != null) {
               this.posx[ix] = (byte)i;
               this.posy[ix] = (byte)j;
               this.ingredients[ix] = row[i];
               ++ix;
            }
         }
      }

      this.width = width1;
      this.height = height1;
      this.mirrored = mirrored;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public boolean isMirrored() {
      return this.mirrored;
   }

   public IIngredient[] getIngredients() {
      return this.ingredients;
   }

   public byte[] getIngredientsX() {
      return this.posx;
   }

   public byte[] getIngredientsY() {
      return this.posy;
   }

   public IItemStack getOutput() {
      return this.output;
   }

   public String getName() {
      return this.name;
   }

   public String getFullResourceName() {
      return null;
   }

   public String getResourceDomain() {
      return null;
   }

   public boolean matches(ICraftingInventory inventory) {
      if (inventory.getStackCount() != this.ingredients.length) {
         return false;
      } else {
         for(int i = 0; i <= inventory.getWidth() - this.width; ++i) {
            for(int j = 0; j <= inventory.getHeight() - this.height; ++j) {
               int k = 0;

               while(true) {
                  if (k >= this.ingredients.length) {
                     return true;
                  }

                  IItemStack item = inventory.getStack(this.posy[k] + j, this.posx[k] + i);
                  if (item == null || !this.ingredients[k].matches(item)) {
                     break;
                  }

                  ++k;
               }
            }
         }

         if (this.mirrored) {
            for(int i = 0; i <= inventory.getWidth() - this.width; ++i) {
               for(int j = 0; j <= inventory.getHeight() - this.height; ++j) {
                  int k = 0;

                  while(true) {
                     if (k >= this.ingredients.length) {
                        return true;
                     }

                     IItemStack item = inventory.getStack(this.posy[k] + j, inventory.getWidth() - (this.posx[k] + i) - 1);
                     if (item == null || !this.ingredients[k].matches(item)) {
                        break;
                     }

                     ++k;
                  }
               }
            }
         }

         return false;
      }
   }

   public IItemStack getCraftingResult(ICraftingInventory inventory) {
      IItemStack[] stacks = new IItemStack[this.ingredients.length];

      for(int i = 0; i <= inventory.getWidth() - this.width; ++i) {
         for(int j = 0; j <= inventory.getHeight() - this.height; ++j) {
            int k = 0;

            while(true) {
               if (k >= this.ingredients.length) {
                  return this.doRecipe(inventory, stacks, i, j, this.output, false);
               }

               IItemStack item = inventory.getStack(this.posy[k] + j, this.posx[k] + i);
               if (item == null || !this.ingredients[k].matches(item)) {
                  break;
               }

               stacks[k] = item;
               ++k;
            }
         }
      }

      if (this.mirrored) {
         for(int i = 0; i <= inventory.getWidth() - this.width; ++i) {
            for(int j = 0; j <= inventory.getHeight() - this.height; ++j) {
               int k = 0;

               while(true) {
                  if (k >= this.ingredients.length) {
                     return this.doRecipe(inventory, stacks, i, j, this.output, true);
                  }

                  IItemStack item = inventory.getStack(this.posy[k] + j, inventory.getWidth() - (this.posx[k] + i) - 1);
                  if (item == null || !this.ingredients[k].matches(item)) {
                     break;
                  }

                  stacks[k] = item;
                  ++k;
               }
            }
         }
      }

      return null;
   }

   public boolean hasTransformers() {
      for(IIngredient ingredient : this.ingredients) {
         if (ingredient.hasNewTransformers()) {
            return true;
         }
      }

      return false;
   }

   public boolean hasRecipeAction() {
      return false;
   }

   public boolean hasRecipeFunction() {
      return false;
   }

   public void applyTransformers(ICraftingInventory inventory, IPlayer byPlayer) {
      IItemStack[] stacks = new IItemStack[this.ingredients.length];

      for(int i = 0; i <= inventory.getWidth() - this.width; ++i) {
         for(int j = 0; j <= inventory.getHeight() - this.height; ++j) {
            int k = 0;

            while(true) {
               if (k >= this.ingredients.length) {
                  this.doRecipeTransformers(inventory, stacks, i, j, false, byPlayer);
                  return;
               }

               IItemStack item = inventory.getStack(this.posy[k] + j, this.posx[k] + i);
               if (item == null || !this.ingredients[k].matches(item)) {
                  break;
               }

               stacks[k] = item;
               ++k;
            }
         }
      }

      if (this.mirrored) {
         for(int i = 0; i <= inventory.getWidth() - this.width; ++i) {
            for(int j = 0; j <= inventory.getHeight() - this.height; ++j) {
               int k = 0;

               while(true) {
                  if (k >= this.ingredients.length) {
                     this.doRecipeTransformers(inventory, stacks, i, j, true, byPlayer);
                     return;
                  }

                  IItemStack item = inventory.getStack(this.posy[k] + j, inventory.getWidth() - (this.posx[k] + i) - 1);
                  if (item == null || !this.ingredients[k].matches(item)) {
                     break;
                  }

                  stacks[k] = item;
                  ++k;
               }
            }
         }
      }

   }

   public IIngredient[] getIngredients1D() {
      return new IIngredient[0];
   }

   public IIngredient[][] getIngredients2D() {
      return new IIngredient[0][];
   }

   public boolean isHidden() {
      return false;
   }

   public boolean isShaped() {
      return false;
   }

   public String toCommandString() {
      StringBuilder result = new StringBuilder();
      result.append("recipes.addShaped(");
      if (this.output != null) {
         result.append(this.output);
      } else {
         result.append("null");
      }

      result.append(", [");
      IIngredient[][] ingredientsArray = new IIngredient[this.height][this.width];

      for(int i = 0; i < this.ingredients.length; ++i) {
         ingredientsArray[this.posy[i]][this.posx[i]] = this.ingredients[i];
      }

      for(int i = 0; i < ingredientsArray.length; ++i) {
         if (i > 0) {
            result.append(", ");
         }

         result.append("[");

         for(int j = 0; j < ingredientsArray[i].length; ++j) {
            if (j > 0) {
               result.append(", ");
            }

            result.append(ingredientsArray[i][j]);
         }

         result.append("]");
      }

      result.append("]);");
      return result.toString();
   }

   private IItemStack doRecipe(ICraftingInventory inventory, IItemStack[] stacks, int offx, int offy, IItemStack output, boolean mirrored) {
      if (this.function != null) {
         Map<String, IItemStack> tagged = new HashMap();

         for(int k = 0; k < this.ingredients.length; ++k) {
            if (this.ingredients[k].getMark() != null) {
               tagged.put(this.ingredients[k].getMark(), stacks[k]);
            }
         }

         output = this.function.process(output, tagged, new CraftingInfo(inventory, (IWorld)null));
         System.out.println("Ouput: " + output);
      }

      return output == null ? null : output;
   }

   private void doRecipeTransformers(ICraftingInventory inventory, IItemStack[] stacks, int offx, int offy, boolean mirrored, IPlayer byPlayer) {
      for(int i = 0; i < this.ingredients.length; ++i) {
         IItemStack transformed = this.ingredients[i].applyNewTransform(stacks[i]);
         if (transformed != stacks[i]) {
            if (mirrored) {
               inventory.setStack(offy + this.posy[i], inventory.getWidth() - (offx + this.posx[i]) - 1, transformed);
            } else {
               inventory.setStack(offy + this.posy[i], offx + this.posx[i], transformed);
            }
         }
      }

   }

   public IRecipeAction getAction() {
      return this.action;
   }
}
