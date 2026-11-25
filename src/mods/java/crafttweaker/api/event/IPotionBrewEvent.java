package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.event.PotionBrewEvent")
@ZenRegister
public interface IPotionBrewEvent {
   @ZenGetter("length")
   int getLength();

   @ZenMethod
   IItemStack getItem(int var1);

   @ZenMethod
   void setItem(int var1, IItemStack var2);
}
