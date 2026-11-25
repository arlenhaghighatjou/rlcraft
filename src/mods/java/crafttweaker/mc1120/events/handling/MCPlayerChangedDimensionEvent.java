package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerChangedDimensionEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IWorld;

public class MCPlayerChangedDimensionEvent implements PlayerChangedDimensionEvent {
   private final net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event;

   public MCPlayerChangedDimensionEvent(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
      this.event = event;
   }

   public int getFrom() {
      return this.event.fromDim;
   }

   public int getTo() {
      return this.event.toDim;
   }

   public IWorld getFromWorld() {
      return CraftTweakerMC.getWorldByID(this.getFrom());
   }

   public IWorld getToWorld() {
      return CraftTweakerMC.getWorldByID(this.getTo());
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.player);
   }
}
