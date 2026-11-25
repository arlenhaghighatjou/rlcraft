package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenClass("crafttweaker.event.PlayerWakeUpEvent")
@ZenRegister
public interface PlayerWakeUpEvent extends IPlayerEvent {
   @ZenGetter("shouldSetSpawn")
   boolean shouldSetSpawn();

   @ZenGetter("shouldUpdateWorld")
   boolean shouldUpdateWorld();

   @ZenGetter("shouldWakeImmediately")
   boolean shouldWakeImmediately();
}
