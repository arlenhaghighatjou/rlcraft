package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.event.PlayerInteractEntityEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;

public class MCPlayerInteractEntityEvent extends MCPlayerInteractEvent implements PlayerInteractEntityEvent {
   private final EntityInteract event;

   public MCPlayerInteractEntityEvent(EntityInteract event) {
      super(event);
      this.event = event;
   }

   public IEntity getTarget() {
      return CraftTweakerMC.getIEntity(this.event.getTarget());
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
