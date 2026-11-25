package com.ferreusveritas.dynamictrees.entities.animation;

import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AnimationHandlerVoid implements IAnimationHandler {
   public AnimationHandlerVoid() {
   }

   public String getName() {
      return "void";
   }

   public boolean shouldDie(EntityFallingTree entity) {
      return true;
   }

   public void renderTransform(EntityFallingTree entity, float entityYaw, float partialTicks) {
   }

   public void initMotion(EntityFallingTree entity) {
      EntityFallingTree.standardDropLogsPayload(entity);
      EntityFallingTree.standardDropLeavesPayLoad(entity);
      entity.cleanupRootyDirt();
   }

   public void handleMotion(EntityFallingTree entity) {
   }

   public void dropPayload(EntityFallingTree entity) {
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldRender(EntityFallingTree entity) {
      return false;
   }
}
