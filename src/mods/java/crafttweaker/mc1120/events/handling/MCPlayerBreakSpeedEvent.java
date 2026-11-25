package crafttweaker.mc1120.events.handling;

import crafttweaker.api.block.IBlockState;
import crafttweaker.api.event.PlayerBreakSpeedEvent;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;

public class MCPlayerBreakSpeedEvent implements PlayerBreakSpeedEvent {
   private final BreakSpeed event;

   public MCPlayerBreakSpeedEvent(BreakSpeed event) {
      this.event = event;
   }

   public IBlockState getBlockState() {
      return CraftTweakerMC.getBlockState(this.event.getState());
   }

   public float getOriginalSpeed() {
      return this.event.getOriginalSpeed();
   }

   public float getNewSpeed() {
      return this.event.getNewSpeed();
   }

   public void setNewSpeed(float newSpeed) {
      this.event.setNewSpeed(newSpeed);
   }

   public IBlockPos getPosition() {
      return CraftTweakerMC.getIBlockPos(this.event.getPos());
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
