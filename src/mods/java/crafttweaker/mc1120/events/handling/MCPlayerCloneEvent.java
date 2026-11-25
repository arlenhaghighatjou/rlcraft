package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerCloneEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;

public class MCPlayerCloneEvent implements PlayerCloneEvent {
   private final Clone event;

   public MCPlayerCloneEvent(Clone event) {
      this.event = event;
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }

   public boolean wasDeath() {
      return this.event.isWasDeath();
   }

   public IPlayer getOriginal() {
      return CraftTweakerMC.getIPlayer(this.event.getOriginal());
   }
}
