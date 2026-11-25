package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class PlaceBlockGoal extends EntityAIBase {
   private BaseCreatureEntity host;
   private double speed = 1.0D;
   private double range = 8.0D;
   private double maxDistance = 64.0D;
   private boolean replaceSolid = false;
   private boolean replaceLiquid = true;
   private BlockPos pos;
   public IBlockState blockState;
   public int metadata = 0;
   private int repathTime = 0;

   public PlaceBlockGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
      this.setMutexBits(3);
   }

   public PlaceBlockGoal setSpeed(double setSpeed) {
      this.speed = setSpeed;
      return this;
   }

   public PlaceBlockGoal setRange(double setRange) {
      this.range = setRange;
      return this;
   }

   public PlaceBlockGoal setMaxDistance(double setMax) {
      this.maxDistance = setMax;
      return this;
   }

   public PlaceBlockGoal setBlockPlacement(IBlockState blockState, BlockPos pos) {
      this.blockState = blockState;
      this.pos = pos;
      return this;
   }

   public PlaceBlockGoal setMetadata(int setMetadata) {
      this.metadata = setMetadata;
      return this;
   }

   public PlaceBlockGoal setReplaceSolid(boolean bool) {
      this.replaceSolid = bool;
      return this;
   }

   public PlaceBlockGoal setReplaceLiquid(boolean bool) {
      this.replaceLiquid = bool;
      return this;
   }

   public boolean shouldExecute() {
      if (this.blockState == null) {
         return false;
      } else if (this.repathTime-- <= 0 && !this.canPlaceBlock(this.pos)) {
         this.blockState = null;
         this.repathTime = 20;
         return false;
      } else {
         this.repathTime = 0;
         return true;
      }
   }

   public void startExecuting() {
      if (!this.host.useDirectNavigator()) {
         this.host.getNavigator().tryMoveToXYZ((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), this.speed);
      } else {
         this.host.directNavigator.setTargetPosition(this.pos, this.speed);
      }

   }

   public void resetTask() {
      this.host.getNavigator().clearPath();
      this.host.directNavigator.clearTargetPosition(1.0D);
      this.blockState = null;
   }

   public void updateTask() {
      if (this.repathTime-- <= 0) {
         this.repathTime = 20;
         if (!this.host.useDirectNavigator()) {
            this.host.getNavigator().tryMoveToXYZ((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), this.speed);
         } else {
            this.host.directNavigator.setTargetPosition(this.pos, this.speed);
         }
      }

      this.host.getLookHelper().setLookPosition((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), 30.0F, 30.0F);
      if (Math.sqrt(this.host.getDistanceSq(this.pos)) <= this.range) {
         this.host.getEntityWorld().setBlockState(this.pos, this.blockState, 3);
         this.host.onBlockPlaced(this.pos, this.blockState);
         this.blockState = null;
         this.host.clearMovement();
      }

      if (this.host.getDistanceSq(new BlockPos(this.pos)) >= this.maxDistance * this.maxDistance) {
         this.blockState = null;
         this.host.clearMovement();
      }

   }

   public boolean canPlaceBlock(BlockPos pos) {
      IBlockState targetState = this.host.getEntityWorld().getBlockState(pos);
      if (targetState.getMaterial() != Material.WATER && targetState.getMaterial() != Material.LAVA) {
         if (targetState.getMaterial() != Material.AIR && !this.replaceSolid) {
            return false;
         }
      } else if (!this.replaceLiquid) {
         return false;
      }

      return this.host.useDirectNavigator() || this.host.getNavigator() == null || this.host.getNavigator().getPathToPos(pos) != null;
   }
}
