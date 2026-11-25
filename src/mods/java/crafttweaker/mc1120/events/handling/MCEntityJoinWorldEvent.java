package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.event.EntityJoinWorldEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IWorld;

public class MCEntityJoinWorldEvent implements EntityJoinWorldEvent {
   private final net.minecraftforge.event.entity.EntityJoinWorldEvent event;

   public MCEntityJoinWorldEvent(net.minecraftforge.event.entity.EntityJoinWorldEvent event) {
      this.event = event;
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

   public IWorld getWorld() {
      return CraftTweakerMC.getIWorld(this.event.getWorld());
   }
}
