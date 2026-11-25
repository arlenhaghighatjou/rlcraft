package crafttweaker.mc1120.events.handling;

import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.event.PlayerBonemealEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IWorld;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class MCPlayerBonemealEvent implements PlayerBonemealEvent {
   private final BonemealEvent event;

   public MCPlayerBonemealEvent(BonemealEvent event) {
      this.event = event;
   }

   public void process() {
      this.event.setResult(Result.ALLOW);
   }

   public boolean isProcessed() {
      return this.event.getResult() == Result.ALLOW;
   }

   public IWorld getWorld() {
      return CraftTweakerMC.getIWorld(this.event.getWorld());
   }

   public IBlock getBlock() {
      return this.getBlockState().getBlock();
   }

   public IBlockState getBlockState() {
      return CraftTweakerMC.getBlockState(this.event.getBlock());
   }

   public IBlockPos getPosition() {
      return CraftTweakerMC.getIBlockPos(this.event.getPos());
   }

   public int getDimension() {
      return this.getWorld().getDimension();
   }

   public IItemStack getItem() {
      return CraftTweakerMC.getIItemStack(this.event.getStack());
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
