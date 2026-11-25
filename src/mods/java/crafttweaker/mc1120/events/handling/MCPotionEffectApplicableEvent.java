package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PotionEffectApplicableEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionApplicableEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class MCPotionEffectApplicableEvent extends MCPotionEffectEvent implements PotionEffectApplicableEvent {
   private final PotionApplicableEvent event;

   public MCPotionEffectApplicableEvent(PotionApplicableEvent event) {
      super(event);
      this.event = event;
   }

   public String getResult() {
      return String.valueOf(this.event.getResult());
   }

   public void setDenied() {
      this.event.setResult(Result.DENY);
   }

   public void setDefault() {
      this.event.setResult(Result.DEFAULT);
   }

   public void setAllowed() {
      this.event.setResult(Result.ALLOW);
   }
}
