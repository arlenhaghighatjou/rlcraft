package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class EntityAIFlightLimits extends EntityAIBase {
   private final EntityParasiteBase parent;
   private int limit;
   private boolean fLimit;

   public EntityAIFlightLimits(EntityParasiteBase in, int limit, boolean flighLimit) {
      this.parent = in;
      this.limit = limit;
      this.fLimit = flighLimit;
   }

   public boolean shouldExecute() {
      return true;
   }

   public void updateTask() {
      if (this.fLimit) {
         if (this.parent.getAttackTarget() != null) {
            if (this.parent.getAttackTarget().posY + (double)this.limit > this.parent.posY) {
               this.parent.motionY -= 0.04D;
            }
         } else {
            boolean ground = this.howMuchNeg(this.parent.getPosition().down(1), 1);
            if (ground) {
               this.parent.motionY -= 0.04D;
            }
         }
      } else {
         boolean ground = this.howMuchPos(this.parent.getPosition().down(1), 1);
         if (ground) {
            this.parent.motionY += 0.04D;
         }
      }

   }

   private boolean howMuchNeg(BlockPos y, int count) {
      if (y.getY() < 1) {
         return false;
      } else if (count <= this.limit) {
         if (this.parent.world.getBlockState(y).getBlock() == Blocks.AIR) {
            BlockPos var10001 = y.down();
            ++count;
            return this.howMuchNeg(var10001, count);
         } else {
            return false;
         }
      } else {
         return count > this.limit ? true : true;
      }
   }

   private boolean howMuchPos(BlockPos y, int count) {
      if (y.getY() < 1) {
         return false;
      } else if (count <= this.limit) {
         if (this.parent.world.getBlockState(y).getBlock() == Blocks.AIR) {
            BlockPos var10001 = y.down();
            ++count;
            return this.howMuchPos(var10001, count);
         } else {
            return true;
         }
      } else {
         return count <= this.limit;
      }
   }
}
