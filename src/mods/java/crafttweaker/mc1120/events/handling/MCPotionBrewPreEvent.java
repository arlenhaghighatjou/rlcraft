package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PotionBrewPreEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent.Pre;

public class MCPotionBrewPreEvent extends MCPotionBrewEvent implements PotionBrewPreEvent {
   private final Pre event;

   public MCPotionBrewPreEvent(Pre event) {
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
