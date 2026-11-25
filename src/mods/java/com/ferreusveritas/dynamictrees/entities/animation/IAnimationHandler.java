package com.ferreusveritas.dynamictrees.entities.animation;

import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IAnimationHandler {
   String getName();

   void initMotion(EntityFallingTree var1);

   void handleMotion(EntityFallingTree var1);

   void dropPayload(EntityFallingTree var1);

   boolean shouldDie(EntityFallingTree var1);

   @SideOnly(Side.CLIENT)
   void renderTransform(EntityFallingTree var1, float var2, float var3);

   @SideOnly(Side.CLIENT)
   boolean shouldRender(EntityFallingTree var1);
}
