package crafttweaker.api.recipes;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import java.util.List;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.recipes.IFurnaceManager")
@ZenRegister
public interface IFurnaceManager {
   @ZenMethod
   void remove(IIngredient var1, @Optional IIngredient var2);

   @ZenMethod
   void removeAll();

   @ZenMethod
   void addRecipe(IItemStack var1, IIngredient var2, @Optional double var3);

   @ZenMethod
   void setFuel(IIngredient var1, int var2);

   @ZenMethod
   int getFuel(IItemStack var1);

   @ZenMethod
   default IItemStack getSmeltingResult(IItemStack stack) {
      return null;
   }

   @ZenGetter("all")
   List<IFurnaceRecipe> getAll();
}
