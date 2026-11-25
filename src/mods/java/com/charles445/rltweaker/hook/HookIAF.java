package com.charles445.rltweaker.hook;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.DamageSource;

public class HookIAF {
   public HookIAF() {
   }

   public static int decrementToZero(int input) {
      return input > 0 ? input - 1 : input;
   }

   public static int handleOldGorgon(EntityMob gorgon, EntityLiving statue, int gorgonDelay, DamageSource gorgonSource) {
      int newGorgonDelay = gorgonDelay;
      gorgon.getAttackTarget().attackEntityFrom(gorgonSource, 2.14748365E9F);
      if (!gorgon.getAttackTarget().isEntityAlive() && gorgonDelay == 0) {
         statue.prevRotationYaw = gorgon.getAttackTarget().rotationYaw;
         statue.rotationYaw = gorgon.getAttackTarget().rotationYaw;
         statue.rotationYawHead = gorgon.getAttackTarget().rotationYaw;
         statue.renderYawOffset = gorgon.getAttackTarget().rotationYaw;
         statue.prevRenderYawOffset = gorgon.getAttackTarget().rotationYaw;
         gorgon.world.spawnEntity(statue);
         newGorgonDelay = 40;
      }

      gorgon.setAttackTarget((EntityLivingBase)null);
      return newGorgonDelay;
   }
}
