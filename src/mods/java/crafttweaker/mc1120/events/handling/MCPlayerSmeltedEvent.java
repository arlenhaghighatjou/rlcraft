package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerSmeltedEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.player.IPlayer;

public class MCPlayerSmeltedEvent implements PlayerSmeltedEvent {
   private final IPlayer player;
   private final IItemStack output;

   public MCPlayerSmeltedEvent(IPlayer player, IItemStack output) {
      this.player = player;
      this.output = output;
   }

   public IPlayer getPlayer() {
      return this.player;
   }

   public IItemStack getOutput() {
      return this.output;
   }
}
