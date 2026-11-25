package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerRespawnEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;

public class MCPlayerRespawnEvent implements PlayerRespawnEvent {
   private final net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event;

   public MCPlayerRespawnEvent(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) {
      this.event = event;
   }

   public boolean isEndConquered() {
      return this.event.isEndConquered();
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.player);
   }
}
