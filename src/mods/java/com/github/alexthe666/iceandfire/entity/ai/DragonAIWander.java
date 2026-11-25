package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public class DragonAIWander extends EntityAIBase {
   private EntityDragonBase dragon;
   private double xPosition;
   private double yPosition;
   private double zPosition;
   private double speed;
   private int executionChance;
   private boolean mustUpdate;

   public DragonAIWander(EntityDragonBase creatureIn, double speedIn) {
      this(creatureIn, speedIn, 20);
   }

   public DragonAIWander(EntityDragonBase creatureIn, double speedIn, int chance) {
      this.dragon = creatureIn;
      this.speed = speedIn;
      this.executionChance = chance;
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (!this.dragon.canMove()) {
         return false;
      } else if (this.dragon.getControllingPassenger() != null) {
         return false;
      } else if (!this.dragon.isFlying() && !this.dragon.isHovering()) {
         if (!this.mustUpdate && this.dragon.getRNG().nextInt(this.executionChance) != 0) {
            return false;
         } else {
            Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.dragon, 10, 7);
            if (vec3d == null) {
               return false;
            } else {
               this.xPosition = vec3d.x;
               this.yPosition = vec3d.y;
               this.zPosition = vec3d.z;
               this.mustUpdate = false;
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      return !this.dragon.getNavigator().noPath();
   }

   public void startExecuting() {
      this.dragon.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
   }

   public void makeUpdate() {
      this.mustUpdate = true;
   }

   public void setExecutionChance(int newchance) {
      this.executionChance = newchance;
   }
}
