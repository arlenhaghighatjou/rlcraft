package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class MCClientTickEvent implements ClientTickEvent {
   private final net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent event;

   public MCClientTickEvent(net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent event) {
      this.event = event;
   }

   public String getSide() {
      return Side.CLIENT.name().toUpperCase();
   }

   public String getPhase() {
      return this.event.phase.name().toUpperCase();
   }
}
