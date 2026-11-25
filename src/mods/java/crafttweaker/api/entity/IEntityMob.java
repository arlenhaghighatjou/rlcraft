package crafttweaker.api.entity;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.entity.IEntityMob")
@ZenRegister
public interface IEntityMob extends IEntityCreature {
   @ZenMethod
   boolean isPreventingPlayerRest(IPlayer var1);
}
