package crafttweaker.mc1120.events.handling;

import crafttweaker.api.container.IContainer;
import crafttweaker.api.event.PlayerOpenContainerEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Open;

public class MCPlayerOpenContainerEvent implements PlayerOpenContainerEvent {
   private final Open event;

   public MCPlayerOpenContainerEvent(Open event) {
      this.event = event;
   }

   public IContainer getContainer() {
      return CraftTweakerMC.getIContainer(this.event.getContainer());
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }
}
