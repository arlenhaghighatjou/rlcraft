package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerWakeUpEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;

public class MCPlayerWakeUpEvent implements PlayerWakeUpEvent {
   private final net.minecraftforge.event.entity.player.PlayerWakeUpEvent event;

   public MCPlayerWakeUpEvent(net.minecraftforge.event.entity.player.PlayerWakeUpEvent event) {
      this.event = event;
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }

   public boolean shouldSetSpawn() {
      return this.event.shouldSetSpawn();
   }

   public boolean shouldUpdateWorld() {
      return this.event.updateWorld();
   }

   public boolean shouldWakeImmediately() {
      return this.event.wakeImmediately();
   }
}
