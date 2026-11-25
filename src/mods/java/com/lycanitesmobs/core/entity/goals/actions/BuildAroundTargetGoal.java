package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.BaseGoal;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class BuildAroundTargetGoal extends BaseGoal {
   private Block block = null;
   private int tickRate = 40;
   private int currentTick = 0;
   private int range = 2;
   private boolean enclose = false;

   public BuildAroundTargetGoal(BaseCreatureEntity setHost) {
      super(setHost);
   }

   public BuildAroundTargetGoal setBlock(Block block) {
      this.block = block;
      return this;
   }

   public BuildAroundTargetGoal setTickRate(int tickRate) {
      this.tickRate = tickRate;
      return this;
   }

   public BuildAroundTargetGoal setRange(int range) {
      this.range = range;
      return this;
   }

   public BuildAroundTargetGoal setEnclose(boolean enclose) {
      this.enclose = enclose;
      return this;
   }

   public boolean shouldExecute() {
      if (!super.shouldExecute()) {
         return false;
      } else {
         return this.block == null || this.getTarget() != null;
      }
   }

   public void startExecuting() {
      this.currentTick = 0;
   }

   public void updateTask() {
      if (this.currentTick++ >= this.tickRate) {
         this.currentTick = 0;
         int range = this.range;
         int enclosement = 0;
         BlockPos targetPos = this.getTarget().getPosition();
         if (this.enclose) {
            int smallestRange = range;

            for(int x = -range; x <= range; ++x) {
               for(int z = -range; z <= range; ++z) {
                  BlockPos blockPos = targetPos.add(x, 0, z);
                  IBlockState blockState = this.host.getEntityWorld().getBlockState(blockPos);
                  if (blockState.getBlock() == this.block) {
                     smallestRange = Math.min(Math.max(Math.abs(x), Math.abs(z)), smallestRange);
                  }
               }
            }

            enclosement = range - (range + 1 - smallestRange);
         }

         for(int currentRange = range; currentRange >= enclosement; --currentRange) {
            for(int x = -currentRange; x <= currentRange; ++x) {
               for(int z = -currentRange; z <= currentRange; ++z) {
                  if (Math.abs(x) == currentRange || Math.abs(z) == currentRange) {
                     BlockPos blockPos = targetPos.add(x, 0, z);
                     IBlockState blockState = this.host.getEntityWorld().getBlockState(blockPos);
                     if (blockState.getBlock() == Blocks.AIR || blockState.getBlock() == this.block) {
                        this.host.getEntityWorld().setBlockState(blockPos, this.block.getDefaultState());
                     }
                  }
               }
            }
         }

         this.host.triggerAttackCooldown();
         this.host.playAttackSound();
      }
   }
}
