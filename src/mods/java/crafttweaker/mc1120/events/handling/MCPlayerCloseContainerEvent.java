package crafttweaker.mc1120.events.handling;

import crafttweaker.api.container.IContainer;
import crafttweaker.api.event.PlayerCloseContainerEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Close;

public class MCPlayerCloseContainerEvent implements PlayerCloseContainerEvent {
   private final Close event;

   public MCPlayerCloseContainerEvent(Close event) {
      this.event = event;
   }

   public IContainer getContainer() {
      return CraftTweakerMC.getIContainer(this.event.getContainer());
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }
}
