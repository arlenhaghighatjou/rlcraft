package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class MCRenderTickEvent implements RenderTickEvent {
   private final net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent event;

   public MCRenderTickEvent(net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent event) {
      this.event = event;
   }

   public float getRenderTickTime() {
      return this.event.renderTickTime;
   }

   public String getSide() {
      return Side.CLIENT.name().toUpperCase();
   }

   public String getPhase() {
      return this.event.phase.name().toUpperCase();
   }
}
