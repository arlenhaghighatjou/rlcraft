package crafttweaker.mc1120.events.handling;

import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.event.PlayerUseHoeEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class MCPlayerUseHoeEvent implements PlayerUseHoeEvent {
   private final UseHoeEvent event;

   public MCPlayerUseHoeEvent(UseHoeEvent event) {
      this.event = event;
   }

   public void process() {
      this.event.setResult(Result.ALLOW);
   }

   public boolean isProcessed() {
      return this.event.getResult() == Result.ALLOW;
   }

   public IPlayer getPlayer() {
      return CraftTweakerMC.getIPlayer(this.event.getEntityPlayer());
   }

   public IItemStack getItem() {
      return CraftTweakerMC.getIItemStack(this.event.getCurrent());
   }

   public IWorld getBlocks() {
      return CraftTweakerMC.getIWorld(this.event.getWorld());
   }

   public int getDimension() {
      return this.getBlocks().getDimension();
   }

   public IBlock getBlock() {
      return this.getBlockState().getBlock();
   }

   public IBlockState getBlockState() {
      return CraftTweakerMC.getBlockState(this.event.getWorld().getBlockState(this.event.getPos()));
   }

   public IBlockPos getPosition() {
      return CraftTweakerMC.getIBlockPos(this.event.getPos());
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
   }
}
