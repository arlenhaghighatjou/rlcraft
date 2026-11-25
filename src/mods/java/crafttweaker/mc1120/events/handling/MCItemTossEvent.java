package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.event.ItemTossEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;

public class MCItemTossEvent implements ItemTossEvent {
   private final net.minecraftforge.event.entity.item.ItemTossEvent event;

   public MCItemTossEvent(net.minecraftforge.event.entity.item.ItemTossEvent event) {
      this.event = event;
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getPlayer());
   }

   public IEntityItem getItem() {
      return CraftTweakerMC.getIEntityItem(this.event.getEntityItem());
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
