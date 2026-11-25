package crafttweaker.api.recipes;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.recipes.ICraftingInventory")
@ZenRegister
public interface ICraftingInventory {
   @ZenGetter("player")
   IPlayer getPlayer();

   @ZenGetter("size")
   int getSize();

   @ZenGetter("width")
   int getWidth();

   @ZenGetter("height")
   int getHeight();

   @ZenGetter("stackCount")
   int getStackCount();

   @ZenMethod
   IItemStack getStack(int var1);

   @ZenMethod
   IItemStack getStack(int var1, int var2);

   @ZenMethod
   void setStack(int var1, int var2, IItemStack var3);

   @ZenMethod
   void setStack(int var1, IItemStack var2);

   @ZenGetter("items")
   IItemStack[][] getItems();

   @ZenGetter("itemArray")
   IItemStack[] getItemArray();

   Object getInternal();
}
