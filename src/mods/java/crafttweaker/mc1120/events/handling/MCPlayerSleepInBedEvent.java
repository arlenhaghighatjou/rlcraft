package crafttweaker.mc1120.events.handling;

import crafttweaker.api.event.PlayerSleepInBedEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import net.minecraft.entity.player.EntityPlayer.SleepResult;

public class MCPlayerSleepInBedEvent implements PlayerSleepInBedEvent {
   private final net.minecraftforge.event.entity.player.PlayerSleepInBedEvent event;

   public MCPlayerSleepInBedEvent(net.minecraftforge.event.entity.player.PlayerSleepInBedEvent event) {
      this.event = event;
   }

   public IBlockPos getPosition() {
      return CraftTweakerMC.getIBlockPos(this.event.getPos());
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }

   public String getResult() {
      return this.event.getResultStatus().name();
   }

   public void setResult(String result) {
      this.event.setResult(SleepResult.valueOf(result));
   }
}
