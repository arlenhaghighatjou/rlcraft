package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PotionEffectExpiryEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionExpiryEvent;

public class MCPotionEffectExpiryEvent extends MCPotionEffectEvent implements PotionEffectExpiryEvent {
   public MCPotionEffectExpiryEvent(PotionExpiryEvent event) {
      super(event);
   }
}
