package crafttweaker.api.container;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.IterableSimple;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.container.IContainer")
@IterableSimple("crafttweaker.item.IItemStack")
@ZenRegister
public interface IContainer extends Iterable<IItemStack> {
   @ZenGetter("containerSize")
   int getContainerSize();

   @ZenMethod
   IItemStack getStack(int var1);

   @ZenMethod
   void setStack(int var1, IItemStack var2);

   @ZenMethod
   @ZenCaster
   String asString();

   Object getInternal();
}
