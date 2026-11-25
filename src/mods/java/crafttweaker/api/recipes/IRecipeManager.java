package crafttweaker.api.recipes;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import java.util.List;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.recipes.IRecipeManager")
@ZenRegister
public interface IRecipeManager {
   @ZenGetter("all")
   List<ICraftingRecipe> getAll();

   @ZenMethod
   List<ICraftingRecipe> getRecipesFor(IIngredient var1);

   @ZenMethod
   void addShaped(IItemStack var1, IIngredient[][] var2, @Optional IRecipeFunction var3, @Optional IRecipeAction var4);

   @ZenDoc("Adds a shaped recipe.")
   @ZenMethod
   void addShaped(String var1, IItemStack var2, IIngredient[][] var3, @Optional IRecipeFunction var4, @Optional IRecipeAction var5);

   @ZenMethod
   void addShapedMirrored(IItemStack var1, IIngredient[][] var2, @Optional IRecipeFunction var3, @Optional IRecipeAction var4);

   @ZenMethod
   void addShapedMirrored(String var1, IItemStack var2, IIngredient[][] var3, @Optional IRecipeFunction var4, @Optional IRecipeAction var5);

   @ZenMethod
   void addShapeless(IItemStack var1, IIngredient[] var2, @Optional IRecipeFunction var3, @Optional IRecipeAction var4);

   @ZenMethod
   void addShapeless(String var1, IItemStack var2, IIngredient[] var3, @Optional IRecipeFunction var4, @Optional IRecipeAction var5);

   @ZenMethod
   void addHiddenShapeless(String var1, IItemStack var2, IIngredient[] var3, @Optional IRecipeFunction var4, @Optional IRecipeAction var5);

   @ZenMethod
   void addHiddenShaped(String var1, IItemStack var2, IIngredient[][] var3, @Optional IRecipeFunction var4, @Optional IRecipeAction var5, @Optional boolean var6);

   @ZenMethod
   void removeAll();

   @ZenMethod
   void remove(IIngredient var1, @Optional boolean var2);

   @ZenMethod
   void removeByRecipeName(String var1, @Optional IItemStack var2);

   @ZenMethod
   void removeByRegex(String var1, @Optional IItemStack var2);

   @ZenMethod
   void removeByMod(String var1);

   @ZenMethod
   void removeShaped(IIngredient var1, @Optional IIngredient[][] var2);

   @ZenMethod
   void removeShapeless(IIngredient var1, @Optional IIngredient[] var2, @Optional boolean var3);

   @ZenMethod
   default void removeByInput(IIngredient input) {
      CraftTweakerAPI.logError("Default method IRecipeManager#removeByInput(IIngredient) is not overwritten in " + this.getClass() + " please report to the author!");
   }

   @ZenMethod
   IItemStack craft(IItemStack[][] var1);

   @ZenMethod
   void replaceAllOccurences(IIngredient var1, IIngredient var2, @Optional IIngredient var3);

   @ZenMethod
   default void replaceAllOccurrences(IIngredient toReplace, IIngredient replaceWith, @Optional IIngredient forOutput) {
      this.replaceAllOccurences(toReplace, replaceWith, forOutput);
   }
}
