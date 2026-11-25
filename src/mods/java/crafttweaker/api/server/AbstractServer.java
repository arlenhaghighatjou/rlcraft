package crafttweaker.api.server;

import crafttweaker.CrafttweakerImplementationAPI;
import crafttweaker.api.event.IEventHandle;
import crafttweaker.api.event.PlayerLoggedInEvent;
import crafttweaker.api.event.PlayerLoggedOutEvent;
import crafttweaker.util.IEventHandler;

public abstract class AbstractServer implements IServer {
   public AbstractServer() {
   }

   public final IEventHandle onPlayerLoggedIn(IEventHandler<PlayerLoggedInEvent> ev) {
      return CrafttweakerImplementationAPI.events.onPlayerLoggedIn(ev);
   }

   public final IEventHandle onPlayerLoggedOut(IEventHandler<PlayerLoggedOutEvent> ev) {
      return CrafttweakerImplementationAPI.events.onPlayerLoggedOut(ev);
   }
}
