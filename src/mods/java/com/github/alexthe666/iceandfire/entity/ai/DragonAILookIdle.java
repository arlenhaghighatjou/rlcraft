package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import net.minecraft.entity.ai.EntityAIBase;

public class DragonAILookIdle extends EntityAIBase {
   private EntityDragonBase dragon;
   private double lookX;
   private double lookZ;
   private int idleTime;

   public DragonAILookIdle(EntityDragonBase prehistoric) {
      this.dragon = prehistoric;
      this.setMutexBits(3);
   }

   public boolean shouldExecute() {
      if (this.dragon.canMove() && this.dragon.getAnimation() != EntityDragonBase.ANIMATION_SHAKEPREY) {
         return this.dragon.getRNG().nextFloat() < 0.02F;
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      return this.idleTime >= 0;
   }

   public void startExecuting() {
      double d0 = 6.283185307179586D * this.dragon.getRNG().nextDouble();
      this.lookX = Math.cos(d0);
      this.lookZ = Math.sin(d0);
      this.idleTime = 20 + this.dragon.getRNG().nextInt(20);
   }

   public void updateTask() {
      --this.idleTime;
      this.dragon.getLookHelper().setLookPosition(this.dragon.posX + this.lookX, this.dragon.posY + (double)this.dragon.getEyeHeight(), this.dragon.posZ + this.lookZ, (float)this.dragon.getHorizontalFaceSpeed(), (float)this.dragon.getVerticalFaceSpeed());
   }
}
