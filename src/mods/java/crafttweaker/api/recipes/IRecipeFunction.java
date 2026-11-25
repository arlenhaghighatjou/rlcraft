package crafttweaker.api.recipes;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import java.util.Map;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("crafttweaker.recipes.IRecipeFunction")
@ZenRegister
public interface IRecipeFunction {
   IItemStack process(IItemStack var1, Map<String, IItemStack> var2, ICraftingInfo var3);
}
