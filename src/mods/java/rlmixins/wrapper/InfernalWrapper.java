package rlmixins.wrapper;

import atomicstryker.infernalmobs.common.InfernalMobsCore;
import net.minecraft.entity.EntityLivingBase;

public abstract class InfernalWrapper {
   public InfernalWrapper() {
   }

   public static boolean isEntityInfernal(EntityLivingBase entity) {
      return InfernalMobsCore.getMobModifiers(entity) != null;
   }
}
