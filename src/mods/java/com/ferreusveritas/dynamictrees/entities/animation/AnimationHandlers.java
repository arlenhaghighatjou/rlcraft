package com.ferreusveritas.dynamictrees.entities.animation;

import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import net.minecraft.util.EnumFacing;

public class AnimationHandlers {
   public static final IAnimationHandler voidAnimationHandler = new AnimationHandlerVoid();
   public static final IAnimationHandler defaultAnimationHandler = new AnimationHandlerPhysics() {
      public String getName() {
         return "default";
      }

      public void initMotion(EntityFallingTree entity) {
         super.initMotion(entity);
         EnumFacing cutDir = entity.getDestroyData().cutDir;
         entity.motionX += (double)cutDir.getOpposite().getFrontOffsetX() * 0.1D;
         entity.motionY += (double)cutDir.getOpposite().getFrontOffsetY() * 0.1D;
         entity.motionZ += (double)cutDir.getOpposite().getFrontOffsetZ() * 0.1D;
      }
   };
   public static final IAnimationHandler blastAnimationHandler = new AnimationHandlerPhysics() {
      public String getName() {
         return "blast";
      }

      public void initMotion(EntityFallingTree entity) {
         super.initMotion(entity);
      }

      public boolean shouldDie(EntityFallingTree entity) {
         return entity.landed || entity.ticksExisted > 200;
      }
   };
   public static final IAnimationHandler falloverAnimationHandler = new AnimationHandlerFallover();

   public AnimationHandlers() {
   }
}
