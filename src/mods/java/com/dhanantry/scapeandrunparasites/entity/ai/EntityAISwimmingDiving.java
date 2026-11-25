package com.dhanantry.scapeandrunparasites.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAISwimmingDiving extends EntityAIBase {
   private final EntityLiving parent;
   private double yMotion;

   public EntityAISwimmingDiving(EntityLiving parentIn, double y) {
      this.parent = parentIn;
      this.setMutexBits(4);
      this.yMotion = y;
      if (parentIn.getNavigator() instanceof PathNavigateGround) {
         ((PathNavigateGround)parentIn.getNavigator()).setCanSwim(true);
      } else if (parentIn.getNavigator() instanceof PathNavigateFlying) {
         ((PathNavigateFlying)parentIn.getNavigator()).setCanFloat(true);
      }

   }

   public boolean shouldExecute() {
      if (!this.parent.isInWater() && !this.parent.isInLava()) {
         return false;
      } else {
         if (this.parent.getAttackTarget() != null) {
            EntityLivingBase target = this.parent.getAttackTarget();
            if ((target.isInWater() || target.isInLava()) && target.getDistanceSq(this.parent.posX, target.posY, this.parent.posZ) < 25.0D && target.posY - this.parent.posY < -1.0D) {
               this.parent.motionY -= this.yMotion;
               return false;
            }
         }

         return true;
      }
   }

   public void updateTask() {
      if (this.parent.getRNG().nextFloat() < 0.8F) {
         this.parent.getJumpHelper().setJumping();
      }

   }
}
