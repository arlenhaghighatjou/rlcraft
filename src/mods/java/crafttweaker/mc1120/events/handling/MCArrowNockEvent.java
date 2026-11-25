package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.ArrowNockEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class MCArrowNockEvent implements ArrowNockEvent {
   private final net.minecraftforge.event.entity.player.ArrowNockEvent event;

   public MCArrowNockEvent(net.minecraftforge.event.entity.player.ArrowNockEvent event) {
      this.event = event;
   }

   public IItemStack getBow() {
      return CraftTweakerMC.getIItemStack(this.event.getBow());
   }

   public String getHand() {
      return this.event.getHand().name();
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
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
