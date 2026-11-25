package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.event.PlayerSleepInBedEvent")
@ZenRegister
public interface PlayerSleepInBedEvent extends IPlayerEvent, IEventPositionable {
   @ZenGetter("result")
   String getResult();

   @ZenSetter("result")
   void setResult(String var1);
}
