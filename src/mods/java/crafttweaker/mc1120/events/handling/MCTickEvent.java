package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.ITickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MCTickEvent implements ITickEvent {
   private final TickEvent event;

   public MCTickEvent(TickEvent event) {
      this.event = event;
   }

   public String getPhase() {
      return this.event.phase.toString().toUpperCase();
   }

   public String getSide() {
      return this.event.side.toString().toUpperCase();
   }
}
