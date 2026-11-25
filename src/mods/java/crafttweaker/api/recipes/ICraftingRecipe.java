package crafttweaker.api.recipes;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.recipes.ICraftingRecipe")
@ZenRegister
public interface ICraftingRecipe {
   @ZenMethod
   @ZenGetter("name")
   String getName();

   @ZenGetter("fullResourceDomain")
   String getFullResourceName();

   @ZenGetter("resourceDomain")
   String getResourceDomain();

   @ZenGetter("commandString")
   String toCommandString();

   @ZenGetter
   boolean hasTransformers();

   @ZenGetter
   boolean hasRecipeAction();

   @ZenGetter
   boolean hasRecipeFunction();

   /** @deprecated */
   @Deprecated
   boolean matches(ICraftingInventory var1);

   /** @deprecated */
   @Deprecated
   IItemStack getCraftingResult(ICraftingInventory var1);

   /** @deprecated */
   @Deprecated
   void applyTransformers(ICraftingInventory var1, IPlayer var2);

   @ZenGetter("ingredients1D")
   IIngredient[] getIngredients1D();

   @ZenGetter("ingredients2D")
   IIngredient[][] getIngredients2D();

   @ZenGetter("hidden")
   boolean isHidden();

   @ZenGetter("shaped")
   boolean isShaped();

   @ZenGetter("output")
   IItemStack getOutput();
}
