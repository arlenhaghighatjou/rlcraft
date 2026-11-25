package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.event.ItemExpireEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;

public class MCItemExpireEvent implements ItemExpireEvent {
   private final net.minecraftforge.event.entity.item.ItemExpireEvent event;

   public MCItemExpireEvent(net.minecraftforge.event.entity.item.ItemExpireEvent event) {
      this.event = event;
   }

   public IEntityItem getItem() {
      return CraftTweakerMC.getIEntityItem(this.event.getEntityItem());
   }

   public int getExtraLife() {
      return this.event.getExtraLife();
   }

   public void setExtraLife(int extraLife) {
      this.event.setExtraLife(extraLife);
   }

   public IEntity getEntity() {
      return this.getItem();
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
