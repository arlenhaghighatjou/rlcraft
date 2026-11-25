package crafttweaker.mc1120.events.handling;

import crafttweaker.api.entity.IEntityXp;
import crafttweaker.api.event.PlayerPickupXpEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;

public class MCPlayerPickupXpEvent implements PlayerPickupXpEvent {
   private final net.minecraftforge.event.entity.player.PlayerPickupXpEvent event;

   public MCPlayerPickupXpEvent(net.minecraftforge.event.entity.player.PlayerPickupXpEvent event) {
      this.event = event;
   }

   public IEntityXp getEntityXp() {
      return CraftTweakerMC.getIEntityXp(this.event.getOrb());
   }

   public float getXp() {
      return (float)this.event.getOrb().getXpValue();
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }
}
