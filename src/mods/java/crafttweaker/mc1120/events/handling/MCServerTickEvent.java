package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class MCServerTickEvent implements ServerTickEvent {
   private final net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent event;

   public MCServerTickEvent(net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent event) {
      this.event = event;
   }

   public String getSide() {
      return Side.SERVER.name().toUpperCase();
   }

   public String getPhase() {
      return this.event.phase.name().toUpperCase();
   }
}
