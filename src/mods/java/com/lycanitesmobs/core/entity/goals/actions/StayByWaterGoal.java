package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class StayByWaterGoal extends EntityAIBase {
   private BaseCreatureEntity host;
   private double speed = 1.0D;
   private double strayDistance = 64.0D;
   private BlockPos waterPos;
   private boolean hasWaterPos = false;
   private int waterSearchRate = 0;
   private int updateRate = 0;

   public StayByWaterGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
      this.setMutexBits(1);
   }

   public StayByWaterGoal setSpeed(double setSpeed) {
      this.speed = setSpeed;
      return this;
   }

   public StayByWaterGoal setStrayDistance(double strayDistance) {
      this.strayDistance = strayDistance;
      return this;
   }

   public boolean shouldExecute() {
      if (this.host.canBreatheAir()) {
         return false;
      } else {
         boolean inWater = this.host.isInWater();
         if (inWater) {
            IBlockState waterBlock = this.host.getEntityWorld().getBlockState(this.host.getPosition());
            if (!this.host.isLavaCreature && waterBlock.getMaterial() == Material.WATER || this.host.isLavaCreature && waterBlock.getMaterial() == Material.LAVA) {
               this.waterPos = this.host.getPosition();
               this.hasWaterPos = true;
               return false;
            }
         } else {
            if (this.hasWaterPos && !this.isValidWaterPosition(this.waterPos)) {
               this.hasWaterPos = false;
            }

            if (this.waterSearchRate-- <= 0) {
               if (!this.hasWaterPos) {
                  this.waterSearchRate = 80;
               } else {
                  this.waterSearchRate = 200;
               }

               double closestDistance = 99999.0D;
               if (this.hasWaterPos) {
                  closestDistance = this.getDistanceFromWater();
               }

               int searchRangeX = 32;
               int searchRangeY = 8;
               int searchRangeZ = 32;
               BlockPos hostPos = this.host.getPosition();

               for(int searchX = hostPos.getX() - searchRangeX; searchX <= hostPos.getX() + searchRangeX; ++searchX) {
                  for(int searchY = hostPos.getY() - searchRangeY; searchY <= hostPos.getY() + searchRangeY; ++searchY) {
                     for(int searchZ = hostPos.getZ() - searchRangeZ; searchZ <= hostPos.getZ() + searchRangeZ; ++searchZ) {
                        BlockPos searchPos = new BlockPos(searchX, searchY, searchZ);
                        double searchDistance = this.host.getDistanceSq(searchPos);
                        if ((!this.hasWaterPos || searchDistance < closestDistance) && this.isValidWaterPosition(searchPos)) {
                           boolean enoughSpace = Math.round(this.host.width) <= 1;
                           if (!enoughSpace) {
                              enoughSpace = true;
                              int neededSpace = Math.round(this.host.width + 0.5F);

                              for(int adjX = searchX - neededSpace; adjX <= searchX + neededSpace; ++adjX) {
                                 for(int adjZ = searchZ - neededSpace; adjZ <= searchZ + neededSpace; ++adjZ) {
                                    if (this.host.getEntityWorld().getBlockState(new BlockPos(adjX, searchY, adjZ)).getMaterial().isSolid()) {
                                       enoughSpace = false;
                                       break;
                                    }
                                 }
                              }
                           }

                           if (enoughSpace) {
                              closestDistance = searchDistance;
                              this.waterPos = searchPos;
                              this.hasWaterPos = true;
                           }
                        }
                     }
                  }
               }
            }
         }

         if (!this.host.isLavaCreature && this.host.waterContact() && this.getDistanceFromWater() <= this.strayDistance) {
            return false;
         } else if (this.host.hasAttackTarget() && this.host.getAir() > -100 && this.getDistanceFromWater() <= this.strayDistance) {
            return false;
         } else {
            return this.hasWaterPos;
         }
      }
   }

   public boolean isValidWaterPosition(BlockPos pos) {
      if (!this.host.canBreatheUnderwater()) {
         return false;
      } else if (!this.host.waterDamage() && this.host.getEntityWorld().getBlockState(pos).getMaterial() == Material.WATER) {
         return true;
      } else {
         return (this.host.isImmuneToFire() || this.host.isLavaCreature) && this.host.getEntityWorld().getBlockState(pos).getMaterial() == Material.LAVA;
      }
   }

   public void startExecuting() {
      this.updateRate = 0;
   }

   public void updateTask() {
      if (this.updateRate-- <= 0) {
         this.updateRate = 20;
         if (!this.host.useDirectNavigator()) {
            this.host.getNavigator().tryMoveToXYZ((double)this.waterPos.getX() + 0.5D, (double)this.waterPos.getY(), (double)this.waterPos.getZ() + 0.5D, this.speed);
         } else {
            this.host.directNavigator.setTargetPosition(this.waterPos, this.speed);
         }
      }

   }

   public void resetTask() {
      this.host.clearMovement();
   }

   public double getDistanceFromWater() {
      return !this.hasWaterPos ? 0.0D : this.host.getDistanceSq(this.waterPos);
   }
}
