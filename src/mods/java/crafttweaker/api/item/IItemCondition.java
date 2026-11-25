package crafttweaker.api.item;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("crafttweaker.item.IItemCondition")
@ZenRegister
public interface IItemCondition {
   boolean matches(IItemStack var1);
}
