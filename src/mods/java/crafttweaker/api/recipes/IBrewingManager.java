package crafttweaker.api.recipes;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenMethod;

public interface IBrewingManager {
   @ZenMethod
   void addBrew(IIngredient var1, IIngredient var2, IItemStack var3, @Optional boolean var4);

   @ZenMethod
   void addBrew(IIngredient var1, IIngredient[] var2, IItemStack var3, @Optional boolean var4);

   @ZenMethod
   void removeRecipe(IItemStack var1, IItemStack var2);

   @ZenMethod
   void removeRecipe(IItemStack var1);
}
