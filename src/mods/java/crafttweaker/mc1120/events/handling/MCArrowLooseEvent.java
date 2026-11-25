package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.ArrowLooseEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IWorld;

public class MCArrowLooseEvent implements ArrowLooseEvent {
   private final net.minecraftforge.event.entity.player.ArrowLooseEvent event;

   public MCArrowLooseEvent(net.minecraftforge.event.entity.player.ArrowLooseEvent event) {
      this.event = event;
   }

   public IItemStack getBow() {
      return CraftTweakerMC.getIItemStack(this.event.getBow());
   }

   public void setCharge(int charge) {
      this.event.setCharge(charge);
   }

   public int getCharge() {
      return this.event.getCharge();
   }

   public IWorld getWorld() {
      return CraftTweakerMC.getIWorld(this.event.getWorld());
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
}
