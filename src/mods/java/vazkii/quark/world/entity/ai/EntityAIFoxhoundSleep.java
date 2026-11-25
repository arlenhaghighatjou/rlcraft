package vazkii.quark.world.entity.ai;

import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.world.entity.EntityFoxhound;

public class EntityAIFoxhoundSleep extends EntityAIMoveToBlock {
   private final EntityFoxhound foxhound;
   private final boolean furnaceOnly;
   private boolean hadSlept = false;

   public EntityAIFoxhoundSleep(EntityFoxhound foxhound, double speed, boolean furnaceOnly) {
      super(foxhound, speed, 8);
      this.foxhound = foxhound;
      this.furnaceOnly = furnaceOnly;
   }

   public boolean shouldExecute() {
      return this.foxhound.isTamed() && !this.foxhound.isSitting() && super.shouldExecute();
   }

   public boolean shouldContinueExecuting() {
      return (!this.hadSlept || this.foxhound.isSleeping()) && super.shouldContinueExecuting();
   }

   public void startExecuting() {
      super.startExecuting();
      this.hadSlept = false;
      this.foxhound.getAISit().setSitting(false);
      this.foxhound.getAISleep().setSleeping(false);
      this.foxhound.setSitting(false);
      this.foxhound.setSleeping(false);
   }

   public void resetTask() {
      super.resetTask();
      this.hadSlept = false;
      this.foxhound.getAISit().setSitting(false);
      this.foxhound.getAISleep().setSleeping(false);
      this.foxhound.setSitting(false);
      this.foxhound.setSleeping(false);
   }

   public void updateTask() {
      super.updateTask();
      if (this.getIsAboveDestination() && this.foxhound.motionX <= 0.0D && this.foxhound.motionZ <= 0.0D) {
         if (!this.foxhound.isSitting()) {
            this.foxhound.getAISit().setSitting(true);
            this.foxhound.getAISleep().setSleeping(true);
            this.foxhound.setSitting(true);
            this.foxhound.setSleeping(true);
            this.hadSlept = true;
         }
      } else {
         this.foxhound.getAISit().setSitting(false);
         this.foxhound.getAISleep().setSleeping(false);
         this.foxhound.setSitting(false);
         this.foxhound.setSleeping(false);
      }

   }

   protected boolean shouldMoveTo(@Nonnull World world, @Nonnull BlockPos pos) {
      if (!world.isAirBlock(pos.up())) {
         return false;
      } else {
         IBlockState state = world.getBlockState(pos);
         TileEntity tileentity = world.getTileEntity(pos);
         if (this.furnaceOnly) {
            return tileentity instanceof TileEntityFurnace;
         } else {
            return state.getLightValue(world, pos) > 2;
         }
      }
   }
}
