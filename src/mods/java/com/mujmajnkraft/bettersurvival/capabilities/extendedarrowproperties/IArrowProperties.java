package com.mujmajnkraft.bettersurvival.capabilities.extendedarrowproperties;

import net.minecraft.entity.projectile.EntityArrow;

public interface IArrowProperties {
   void setExplosion(float var1, boolean var2);

   void setNoDrag(boolean var1);

   void setCanRecover(boolean var1);

   void hitEntity(EntityArrow var1);

   float getExplosionPower();

   boolean getCanDestroyBlocks();

   boolean getNoDrag();

   boolean getCanRecover();
}
