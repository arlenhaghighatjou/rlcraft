package com.lycanitesmobs.core.entity.navigate;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class DirectNavigator {
   BaseCreatureEntity host;
   public BlockPos targetPosition;
   public double flyingSpeed = 1.0D;
   public boolean faceMovement = true;
   public double speedModifier = 1.0D;
   private double randomStrafeAngle = 0.0D;

   public DirectNavigator(BaseCreatureEntity setHost) {
      this.host = setHost;
   }

   public DirectNavigator setSpeed(double setSpeed) {
      this.flyingSpeed = setSpeed;
      return this;
   }

   public DirectNavigator setFacing(boolean facing) {
      this.faceMovement = facing;
      return this;
   }

   public boolean setTargetPosition(BlockPos targetPosition, double setSpeedMod) {
      if (this.isTargetPositionValid(targetPosition)) {
         this.targetPosition = targetPosition;
         this.speedModifier = setSpeedMod;
         return true;
      } else {
         return false;
      }
   }

   public boolean setTargetPosition(Entity targetEntity, double setSpeedMod) {
      return this.setTargetPosition(new BlockPos((int)targetEntity.posX, (int)targetEntity.posY, (int)targetEntity.posZ), setSpeedMod);
   }

   public boolean clearTargetPosition(double setSpeedMod) {
      return this.setTargetPosition((BlockPos)null, setSpeedMod);
   }

   public boolean isTargetPositionValid() {
      return this.isTargetPositionValid(this.targetPosition);
   }

   public boolean isTargetPositionValid(BlockPos targetPosition) {
      return true;
   }

   public double distanceToTargetPosition() {
      return this.host.getDistance((double)this.targetPosition.getX(), (double)this.targetPosition.getY(), (double)this.targetPosition.getZ());
   }

   public boolean atTargetPosition() {
      if (this.targetPosition != null) {
         double speed = this.host.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 2.0D;
         return this.distanceToTargetPosition() <= (double)this.host.width + speed;
      } else {
         return true;
      }
   }

   public void updateFlight() {
      if (this.targetPosition != null && !this.atTargetPosition()) {
         double speed = this.host.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 2.0D;
         BlockPos pos = this.host.getFacingPosition((double)this.targetPosition.getX(), (double)this.targetPosition.getY(), (double)this.targetPosition.getZ(), 1.0D, this.randomStrafeAngle);
         double dirX = (double)pos.getX() - this.host.posX;
         double dirY = (double)this.targetPosition.getY() + 0.1D - this.host.posY;
         double dirZ = (double)pos.getZ() - this.host.posZ;
         this.host.motionX += (Math.signum(dirX) * speed - this.host.motionX) * 0.10000000149011612D * 0.3D * this.speedModifier;
         this.host.motionY += (Math.signum(dirY) * speed - this.host.motionY) * 0.10000000149011612D * 0.3D * this.speedModifier;
         this.host.motionZ += (Math.signum(dirZ) * speed - this.host.motionZ) * 0.10000000149011612D * 0.3D * this.speedModifier;
         float fullAngle = (float)(Math.atan2(this.host.motionZ, this.host.motionX) * 180.0D / 3.141592653589793D) - 90.0F;
         float angle = MathHelper.wrapDegrees(fullAngle - this.host.rotationYaw);
         this.host.moveForward = 0.5F;
         if (this.faceMovement && this.host.getAttackTarget() != null && (this.host.motionX > 0.02500000037252903D || this.host.motionZ > 0.02500000037252903D)) {
            this.host.rotationYaw += angle;
         }

      }
   }

   public void flightMovement(float moveStrafe, float moveForward) {
      if (this.host.isInWater()) {
         this.host.moveSwimmingWithHeading(moveStrafe, moveForward);
         this.host.move(MoverType.SELF, this.host.motionX, this.host.motionY, this.host.motionZ);
         this.host.motionX *= 0.800000011920929D;
         this.host.motionY *= 0.800000011920929D;
         this.host.motionZ *= 0.800000011920929D;
      } else if (this.host.lavaContact()) {
         this.host.moveSwimmingWithHeading(moveStrafe, moveForward);
         this.host.move(MoverType.SELF, this.host.motionX, this.host.motionY, this.host.motionZ);
         this.host.motionX *= 0.5D;
         this.host.motionY *= 0.5D;
         this.host.motionZ *= 0.5D;
      } else {
         float motion = 0.91F;
         if (this.host.onGround) {
            motion = 0.54600006F;
            Block block = this.host.getEntityWorld().getBlockState(new BlockPos(MathHelper.floor(this.host.posX), MathHelper.floor(this.host.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.host.posZ))).getBlock();
            if (block != null) {
               motion = block.slipperiness * 0.91F;
            }
         }

         float flyingMotion = 0.16277136F / (motion * motion * motion);
         this.host.moveFlying(moveStrafe, moveForward, this.host.onGround ? 0.1F * flyingMotion : (float)(0.019999999552965164D * this.speedModifier));
         motion = 0.91F;
         if (this.host.onGround) {
            motion = 0.54600006F;
            Block block = this.host.getEntityWorld().getBlockState(new BlockPos(MathHelper.floor(this.host.posX), MathHelper.floor(this.host.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.host.posZ))).getBlock();
            if (block != null) {
               motion = block.slipperiness * 0.91F;
            }
         }

         if (this.host != null && this.host.getEntityBoundingBox() != null) {
            this.host.move(MoverType.SELF, this.host.motionX, this.host.motionY, this.host.motionZ);
         }

         this.host.motionX *= (double)motion;
         this.host.motionY *= (double)motion;
         this.host.motionZ *= (double)motion;
      }

      this.host.prevLimbSwingAmount = this.host.limbSwingAmount;
      double deltaX = this.host.posX - this.host.prevPosX;
      double deltaZ = this.host.posZ - this.host.prevPosZ;
      float var7 = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 4.0F;
      if (var7 > 1.0F) {
         var7 = 1.0F;
      }

      this.host.limbSwingAmount += (var7 - this.host.limbSwingAmount) * 0.4F;
      this.host.limbSwing += this.host.limbSwingAmount;
   }

   protected void adjustRotationToWaypoint() {
      double distX = (double)this.targetPosition.getX() - this.host.posX;
      double distZ = (double)this.targetPosition.getZ() - this.host.posZ;
      float fullAngle = (float)(Math.atan2(distZ, distX) * 180.0D / 3.141592653589793D);
      float angle = MathHelper.wrapDegrees(fullAngle - this.host.rotationYaw);
      if (angle > 30.0F) {
         angle = 30.0F;
      }

      if (angle < -30.0F) {
         angle = -30.0F;
      }

      this.host.renderYawOffset = this.host.rotationYaw += angle;
   }

   public void adjustRotationToTarget(BlockPos target) {
      double distX = (double)target.getX() - this.host.posX;
      double distZ = (double)target.getZ() - this.host.posZ;
      float fullAngle = (float)(Math.atan2(distZ, distX) * 180.0D / 3.141592653589793D) - 90.0F;
      float angle = MathHelper.wrapDegrees(fullAngle - this.host.rotationYaw);
      this.host.rotationYaw += angle;
   }
}
