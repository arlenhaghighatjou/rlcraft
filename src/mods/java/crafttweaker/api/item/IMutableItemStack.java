package crafttweaker.api.item;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.item.IMutableItemStack")
@ZenRegister
public interface IMutableItemStack extends IItemStack {
   @ZenMethod
   void shrink(int var1);

   @ZenMethod
   void grow(int var1);

   @ZenMethod
   boolean attemptDamageItem(int var1, @Optional IPlayer var2);

   @ZenMethod
   IItemStack copy();
}
