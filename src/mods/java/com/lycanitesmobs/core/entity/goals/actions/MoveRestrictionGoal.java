package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MoveRestrictionGoal extends EntityAIBase {
   private BaseCreatureEntity host;
   private double speed = 1.0D;
   private double movePosX;
   private double movePosY;
   private double movePosZ;

   public MoveRestrictionGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
      this.setMutexBits(3);
   }

   public MoveRestrictionGoal setSpeed(double setSpeed) {
      this.speed = setSpeed;
      return this;
   }

   public boolean shouldExecute() {
      if (this.host.hasHome()) {
         return false;
      } else {
         BlockPos chunkcoordinates = this.host.getHomePosition();
         Vec3d vec3 = RandomPositionGenerator.findRandomTargetTowards(this.host, 16, 7, new Vec3d((double)chunkcoordinates.getX(), (double)chunkcoordinates.getY(), (double)chunkcoordinates.getZ()));
         if (vec3 == null) {
            return false;
         } else {
            this.movePosX = vec3.x;
            this.movePosY = vec3.y;
            this.movePosZ = vec3.z;
            return true;
         }
      }
   }

   public boolean shouldContinueExecuting() {
      return !this.host.getNavigator().noPath();
   }

   public void startExecuting() {
      this.host.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.speed);
   }
}
