package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PotionEffectAddedEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.potions.IPotionEffect;
import net.minecraftforge.event.entity.living.PotionEvent.PotionAddedEvent;

public class MCPotionEffectAddedEvent extends MCPotionEffectEvent implements PotionEffectAddedEvent {
   private final PotionAddedEvent event;

   public MCPotionEffectAddedEvent(PotionAddedEvent event) {
      super(event);
      this.event = event;
   }

   public IPotionEffect getOldEffect() {
      return CraftTweakerMC.getIPotionEffect(this.event.getOldPotionEffect());
   }
}
