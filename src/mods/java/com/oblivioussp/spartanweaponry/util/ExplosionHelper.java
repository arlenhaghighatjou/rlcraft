package com.oblivioussp.spartanweaponry.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class ExplosionHelper {
   public ExplosionHelper() {
   }

   public static void explode(Entity explosive, Entity exploder, BlockPos blockPos) {
      if (!explosive.world.isRemote) {
         boolean mobGriefing = explosive.world.getGameRules().getBoolean("mobGriefing");
         explosive.world.createExplosion(explosive, (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, ConfigHandler.explosionStrengthArrowExplosive, ConfigHandler.enableTerrainDamage && mobGriefing);
         explosive.setDead();
      }

   }
}
