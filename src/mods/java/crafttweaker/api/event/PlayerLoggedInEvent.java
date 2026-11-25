package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("crafttweaker.event.PlayerLoggedInEvent")
@ZenRegister
public interface PlayerLoggedInEvent extends IPlayerEvent {
}
