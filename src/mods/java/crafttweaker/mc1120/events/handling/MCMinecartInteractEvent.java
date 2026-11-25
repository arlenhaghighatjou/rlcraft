package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntity;
import crafttweaker.api.event.MinecartInteractEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;

public class MCMinecartInteractEvent implements MinecartInteractEvent {
   private net.minecraftforge.event.entity.minecart.MinecartInteractEvent event;

   public MCMinecartInteractEvent(net.minecraftforge.event.entity.minecart.MinecartInteractEvent event) {
      this.event = event;
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getPlayer());
   }

   public IItemStack getItem() {
      return CraftTweakerMC.getIItemStack(this.event.getItem());
   }

   public String getHand() {
      return String.valueOf(this.event.getHand());
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
