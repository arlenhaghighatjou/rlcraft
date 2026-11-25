package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.SleepingTimeCheckEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class MCSleepingTimeCheckEvent implements SleepingTimeCheckEvent {
   private net.minecraftforge.event.entity.player.SleepingTimeCheckEvent event;

   public MCSleepingTimeCheckEvent(net.minecraftforge.event.entity.player.SleepingTimeCheckEvent event) {
      this.event = event;
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

   public IBlockPos getPosition() {
      return CraftTweakerMC.getIBlockPos(this.event.getSleepingLocation());
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }
}
