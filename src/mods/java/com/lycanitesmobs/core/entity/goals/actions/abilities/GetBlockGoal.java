package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.TargetSorterNearest;
import com.lycanitesmobs.core.info.ObjectLists;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class GetBlockGoal extends EntityAIBase {
   private BaseCreatureEntity host;
   private BlockPos target;
   private int targetingTime = 0;
   private TargetSorterNearest targetSorter;
   private int distanceMax = 8;
   double speed = 1.0D;
   private boolean checkSight = true;
   private int cantSeeTime = 0;
   protected int cantSeeTimeMax = 60;
   private int updateRate = 0;
   public Block targetBlock = Blocks.TORCH;
   public String targetBlockName = "";
   public boolean tamedLooting = true;

   public GetBlockGoal(BaseCreatureEntity setHost) {
      this.setMutexBits(3);
      this.host = setHost;
      this.targetSorter = new TargetSorterNearest(setHost);
   }

   public GetBlockGoal setDistanceMax(int setInt) {
      this.distanceMax = setInt;
      return this;
   }

   public GetBlockGoal setSpeed(double setDouble) {
      this.speed = setDouble;
      return this;
   }

   public GetBlockGoal setCheckSight(boolean setBool) {
      this.checkSight = setBool;
      return this;
   }

   public GetBlockGoal setBlock(Block block) {
      this.targetBlock = block;
      return this;
   }

   public GetBlockGoal setBlockName(String name) {
      this.targetBlockName = name.toLowerCase();
      return this;
   }

   public GetBlockGoal setTamedLooting(boolean bool) {
      this.tamedLooting = bool;
      return this;
   }

   public boolean shouldExecute() {
      if (this.host.canPickupItems() && this.host.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
         if (!this.host.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
            return false;
         } else if (!this.tamedLooting && this.host instanceof TameableCreatureEntity && this.host.isTamed()) {
            return false;
         } else if (this.targetingTime-- > 0) {
            return false;
         } else {
            this.targetingTime = 60;
            byte heightDistance = 2;
            ArrayList possibleTargets = new ArrayList();

            for(int x = (int)this.host.posX - this.distanceMax; x < (int)this.host.posX + this.distanceMax; ++x) {
               for(int y = (int)this.host.posY - heightDistance; y < (int)this.host.posY + heightDistance; ++y) {
                  for(int z = (int)this.host.posZ - this.distanceMax; z < (int)this.host.posZ + this.distanceMax; ++z) {
                     Block searchBlock = this.host.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
                     if (searchBlock != null && searchBlock != Blocks.AIR && searchBlock != Blocks.REDSTONE_TORCH) {
                        BlockPos possibleTarget = null;
                        if (!"".equalsIgnoreCase(this.targetBlockName)) {
                           if (ObjectLists.isName(searchBlock, this.targetBlockName)) {
                              possibleTarget = new BlockPos(x, y, z);
                           }
                        } else if (searchBlock == this.targetBlock) {
                           possibleTarget = new BlockPos(x + 1, y, z);
                        }

                        if (possibleTarget != null) {
                           possibleTargets.add(possibleTarget);
                        }
                     }
                  }
               }
            }

            if (possibleTargets.isEmpty()) {
               return false;
            } else {
               Collections.sort(possibleTargets, this.targetSorter);
               this.target = (BlockPos)possibleTargets.get(0);
               return this.shouldContinueExecuting();
            }
         }
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      if (this.target == null) {
         return false;
      } else if (!this.host.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
         return false;
      } else {
         double distance = this.host.getDistanceSq((double)this.target.getX(), (double)this.target.getY(), (double)this.target.getZ());
         return distance <= (double)this.distanceMax;
      }
   }

   public void resetTask() {
      this.target = null;
      this.host.clearMovement();
   }

   public void startExecuting() {
      this.updateRate = 0;
   }

   public void updateTask() {
      if (this.updateRate-- <= 0) {
         this.updateRate = 10;
         if (!this.host.useDirectNavigator()) {
            this.host.getNavigator().tryMoveToXYZ((double)this.target.getX(), (double)this.target.getY(), (double)this.target.getZ(), this.speed);
         } else {
            this.host.directNavigator.setTargetPosition(this.target, this.speed);
         }
      }

   }
}
