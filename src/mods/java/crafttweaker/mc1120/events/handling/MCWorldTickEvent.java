package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.WorldTickEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IWorld;

public class MCWorldTickEvent implements WorldTickEvent {
   private final net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent event;

   public MCWorldTickEvent(net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent event) {
      this.event = event;
   }

   public IWorld getWorld() {
      return CraftTweakerMC.getIWorld(this.event.world);
   }

   public String getSide() {
      return this.event.side.name().toUpperCase();
   }

   public String getPhase() {
      return this.event.phase.name().toUpperCase();
   }
}
