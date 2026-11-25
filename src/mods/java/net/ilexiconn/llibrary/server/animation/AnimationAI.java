package net.ilexiconn.llibrary.server.animation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;

public abstract class AnimationAI<T extends Entity & IAnimatedEntity> extends EntityAIBase {
   protected T entity;

   public AnimationAI(T entity) {
      this.entity = entity;
      this.setMutexBits(7);
   }

   public abstract Animation getAnimation();

   public boolean isAutomatic() {
      return false;
   }

   public boolean shouldAnimate() {
      return false;
   }

   public boolean shouldExecute() {
      if (this.isAutomatic()) {
         return ((IAnimatedEntity)this.entity).getAnimation() == this.getAnimation();
      } else {
         return this.shouldAnimate();
      }
   }

   public void startExecuting() {
      if (!this.isAutomatic()) {
         AnimationHandler.INSTANCE.sendAnimationMessage(this.entity, this.getAnimation());
      }

      ((IAnimatedEntity)this.entity).setAnimationTick(0);
   }

   public boolean shouldContinueExecuting() {
      return ((IAnimatedEntity)this.entity).getAnimationTick() < this.getAnimation().getDuration();
   }

   public void resetTask() {
      AnimationHandler.INSTANCE.sendAnimationMessage(this.entity, IAnimatedEntity.NO_ANIMATION);
   }
}
