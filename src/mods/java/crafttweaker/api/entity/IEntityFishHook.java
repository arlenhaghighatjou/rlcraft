package crafttweaker.api.entity;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.entity.IEntityFishHook")
@ZenRegister
public interface IEntityFishHook extends IEntity {
   @ZenMethod
   @ZenGetter("state")
   String getState();

   @ZenMethod
   @ZenSetter("state")
   void setState(String var1);

   @ZenMethod
   @ZenGetter("caughtEntity")
   IEntity getCaughtEntity();

   @ZenMethod
   @ZenSetter("caughtEntity")
   void setCaughtEntity(IEntity var1);

   @ZenMethod
   @ZenGetter("angler")
   IPlayer getAngler();

   @ZenMethod
   @ZenSetter("lureSpeed")
   void setLureSpeed(int var1);

   @ZenMethod
   @ZenSetter("luck")
   void setLuck(int var1);
}
