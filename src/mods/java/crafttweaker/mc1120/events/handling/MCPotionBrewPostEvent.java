package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PotionBrewPostEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent.Post;

public class MCPotionBrewPostEvent extends MCPotionBrewEvent implements PotionBrewPostEvent {
   public MCPotionBrewPostEvent(Post event) {
      super(event);
   }
}
