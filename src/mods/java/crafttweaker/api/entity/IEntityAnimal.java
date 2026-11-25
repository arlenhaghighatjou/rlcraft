package crafttweaker.api.entity;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.entity.IEntityAnimal")
@ZenRegister
public interface IEntityAnimal extends IEntityAgeable {
   @ZenMethod
   boolean isBreedingItem(IItemStack var1);

   @ZenMethod
   void setInLove(@Optional IPlayer var1);

   @ZenGetter("loveCause")
   IPlayer getLoveCause();

   @ZenGetter
   boolean isInLove();

   @ZenMethod
   void resetInLove();

   @ZenMethod
   boolean canMateWith(IEntityAnimal var1);
}
