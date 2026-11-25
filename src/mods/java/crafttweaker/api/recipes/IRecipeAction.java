package crafttweaker.api.recipes;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("crafttweaker.recipes.IRecipeAction")
@ZenRegister
public interface IRecipeAction {
   void process(IItemStack var1, ICraftingInfo var2, IPlayer var3);
}
