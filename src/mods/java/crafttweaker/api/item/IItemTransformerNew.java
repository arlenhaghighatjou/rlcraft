package crafttweaker.api.item;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.item.IItemTransformerNew")
@ZenRegister
public interface IItemTransformerNew {
   @ZenMethod
   IItemStack transform(IItemStack var1);
}
