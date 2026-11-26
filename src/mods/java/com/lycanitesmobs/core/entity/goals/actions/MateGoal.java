package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class MateGoal extends EntityAIBase {
   private AgeableCreatureEntity host;
   private AgeableCreatureEntity partner;
   private double speed = 1.0D;
   private Class targetClass;
   private int mateTime;
   private int mateTimeMax = 60;
   private double mateDistance = 9.0D;

   public MateGoal(AgeableCreatureEntity setHost) {
      this.host = setHost;
      this.targetClass = this.host.getClass();
      this.setMutexBits(3);
   }

   public MateGoal setSpeed(double setSpeed) {
      this.speed = setSpeed;
      return this;
   }

   public MateGoal setMateDistance(double setDouble) {
      this.mateDistance = setDouble * setDouble;
      return this;
   }

   public MateGoal setTargetClass(Class setTargetClass) {
      this.targetClass = setTargetClass;
      return this;
   }

   public MateGoal setMateTime(int setTime) {
      this.mateTimeMax = setTime;
      return this;
   }

   public boolean shouldExecute() {
      if (!this.host.canMate()) {
         return false;
      } else {
         this.partner = this.getPartner();
         return this.partner != null;
      }
   }

   public boolean shouldContinueExecuting() {
      return this.partner != null && this.partner.isEntityAlive() && this.partner.isInLove() && this.mateTime < this.mateTimeMax;
   }

   public void resetTask() {
      this.partner = null;
      this.mateTime = 0;
   }

   public void updateTask() {
      this.host.getLookHelper().setLookPositionWithEntity(this.partner, 10.0F, (float)this.host.getVerticalFaceSpeed());
      if (!this.host.useDirectNavigator()) {
         this.host.getNavigator().tryMoveToEntityLiving(this.partner, this.speed);
      } else {
         this.host.directNavigator.setTargetPosition(new BlockPos((int)this.partner.posX, (int)this.partner.posY, (int)this.partner.posZ), this.speed);
      }

      if (this.host.getDistanceSq(this.partner) < this.mateDistance + this.host.getPhysicalRange()) {
         ++this.mateTime;
         if (this.mateTime >= this.mateTimeMax) {
            this.host.procreate(this.partner);
         }
      }

   }

   private AgeableCreatureEntity getPartner() {
      float distance = 8.0F;
      List<EntityLivingBase> possibleMates = this.host.getEntityWorld().getEntitiesWithinAABB(this.targetClass, this.host.getEntityBoundingBox().grow((double)distance, (double)distance, (double)distance));
      double closestDistance = Double.MAX_VALUE;
      AgeableCreatureEntity newMate = null;

      for(EntityLivingBase nextEntity : possibleMates) {
         if (nextEntity instanceof AgeableCreatureEntity) {
            AgeableCreatureEntity testMate = (AgeableCreatureEntity)nextEntity;
            if (this.host.canBreedWith(testMate) && (double)this.host.getDistance(testMate) < closestDistance) {
               newMate = testMate;
               closestDistance = (double)this.host.getDistance(testMate);
            }
         }
      }

      return newMate;
   }
}
