package crafttweaker.mc1120.events.handling;

import crafttweaker.api.block.IBlock;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.event.PlayerFillBucketEvent;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IRayTraceResult;
import crafttweaker.api.world.IWorld;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class MCPlayerFillBucketEvent implements PlayerFillBucketEvent {
   private final FillBucketEvent event;

   public MCPlayerFillBucketEvent(FillBucketEvent ev) {
      this.event = ev;
   }

   public IItemStack getResult() {
      return CraftTweakerMC.getIItemStack(this.event.getFilledBucket());
   }

   public void setResult(IItemStack result) {
      this.event.setFilledBucket(CraftTweakerMC.getItemStack(result));
      this.event.setResult(Result.ALLOW);
   }

   public IItemStack getEmptyBucket() {
      return CraftTweakerMC.getIItemStack(this.event.getEmptyBucket());
   }

   public IWorld getWorld() {
      return CraftTweakerMC.getIWorld(this.event.getWorld());
   }

   public IBlock getBlock() {
      IBlockState blockState = this.getBlockState();
      return blockState == null ? null : blockState.getBlock();
   }

   public IBlockState getBlockState() {
      RayTraceResult target = this.event.getTarget();
      return target == null ? null : CraftTweakerMC.getBlockState(this.event.getWorld().getBlockState(target.getBlockPos()));
   }

   public int getDimension() {
      return this.getWorld().getDimension();
   }

   public IBlockPos getPosition() {
      RayTraceResult target = this.event.getTarget();
      return CraftTweakerMC.getIBlockPos(target == null ? this.event.getEntityPlayer().getPosition() : target.getBlockPos());
   }

   public IRayTraceResult getRayTraceResult() {
      return CraftTweakerMC.getIRayTraceResult(this.event.getTarget());
   }

   public boolean isCanceled() {
      return this.event.isCanceled();
   }

   public void setCanceled(boolean canceled) {
      this.event.setCanceled(canceled);
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
}
