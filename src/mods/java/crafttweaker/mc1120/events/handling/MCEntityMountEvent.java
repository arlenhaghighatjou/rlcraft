package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.event.EntityMountEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IWorld;

public class MCEntityMountEvent implements EntityMountEvent {
   private net.minecraftforge.event.entity.EntityMountEvent event;

   public MCEntityMountEvent(net.minecraftforge.event.entity.EntityMountEvent event) {
      this.event = event;
   }

   public IEntity getMountingEntity() {
      return CraftTweakerMC.getIEntity(this.event.getEntityMounting());
   }

   public IEntity getMountedEntity() {
      return CraftTweakerMC.getIEntity(this.event.getEntityBeingMounted());
   }

   public boolean isMounting() {
      return this.event.isMounting();
   }

   public boolean isDismounting() {
      return this.event.isDismounting();
   }

   public IWorld getWorld() {
      return CraftTweakerMC.getIWorld(this.event.getWorldObj());
   }

   public IEntity getEntity() {
      return this.getMountedEntity();
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
