package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerLoggedOutEvent;
import crafttweaker.api.player.IPlayer;

public class MCPlayerLoggedOutEvent implements PlayerLoggedOutEvent {
   private final IPlayer player;

   public MCPlayerLoggedOutEvent(IPlayer player) {
      this.player = player;
   }

   public IPlayer getPlayer() {
      return this.player;
   }
}
