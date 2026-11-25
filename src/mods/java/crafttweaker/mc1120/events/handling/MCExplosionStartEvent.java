package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.ExplosionStartEvent;
import net.minecraftforge.event.world.ExplosionEvent.Start;

public class MCExplosionStartEvent extends MCExplosionEvent implements ExplosionStartEvent {
   private Start event;

   public MCExplosionStartEvent(Start event) {
      super(event);
      this.event = event;
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
