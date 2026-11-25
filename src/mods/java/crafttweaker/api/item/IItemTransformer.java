package crafttweaker.api.item;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.item.IItemTransformer")
@ZenRegister
public interface IItemTransformer {
   @ZenMethod
   IItemStack transform(IItemStack var1, IPlayer var2);
}
