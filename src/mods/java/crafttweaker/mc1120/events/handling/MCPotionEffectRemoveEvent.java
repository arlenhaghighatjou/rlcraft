package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PotionEffectRemoveEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.potions.IPotion;
import net.minecraftforge.event.entity.living.PotionEvent.PotionRemoveEvent;

public class MCPotionEffectRemoveEvent extends MCPotionEffectEvent implements PotionEffectRemoveEvent {
   private final PotionRemoveEvent event;

   public MCPotionEffectRemoveEvent(PotionRemoveEvent event) {
      super(event);
      this.event = event;
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }

   public IPotion getPotion() {
      return CraftTweakerMC.getIPotion(this.event.getPotion());
   }
}
