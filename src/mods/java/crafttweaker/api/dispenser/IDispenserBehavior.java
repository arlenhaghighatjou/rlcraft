package crafttweaker.api.dispenser;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("crafttweaker.dispenser.IDispenserBehavior")
@FunctionalInterface
public interface IDispenserBehavior {
   @ZenMethod
   IItemStack apply(IBlockSource var1, IItemStack var2);
}
