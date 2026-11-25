package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.event.EntityTravelToDimensionEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;

public class MCEntityTravelToDimensionEvent implements EntityTravelToDimensionEvent {
   private net.minecraftforge.event.entity.EntityTravelToDimensionEvent event;

   public MCEntityTravelToDimensionEvent(net.minecraftforge.event.entity.EntityTravelToDimensionEvent event) {
      this.event = event;
   }

   public int getDimension() {
      return this.event.getDimension();
   }

   public IEntity getEntity() {
      return CraftTweakerMC.getIEntity(this.event.getEntity());
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
