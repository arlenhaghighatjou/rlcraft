package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.event.PlayerPickupItemEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class MCPlayerPickupItemEvent implements PlayerPickupItemEvent {
   private final EntityItemPickupEvent event;

   public MCPlayerPickupItemEvent(EntityItemPickupEvent event) {
      this.event = event;
   }

   public IEntityItem getItem() {
      return CraftTweakerMC.getIEntityItem(this.event.getItem());
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }

   public String getResult() {
      return String.valueOf(this.event.getResult());
   }

   public void setDenied() {
      this.event.setResult(Result.DENY);
   }

   public void setDefault() {
      this.event.setResult(Result.DEFAULT);
   }

   public void setAllowed() {
      this.event.setResult(Result.ALLOW);
   }
}
