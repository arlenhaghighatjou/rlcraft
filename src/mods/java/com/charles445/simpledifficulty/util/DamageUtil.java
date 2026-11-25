package com.charles445.simpledifficulty.util;

import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class DamageUtil {
   public DamageUtil() {
   }

   public static boolean isModDangerous(World world) {
      return world.getDifficulty() != EnumDifficulty.PEACEFUL || ServerConfig.instance.getBoolean(ServerOptions.PEACEFUL_DANGER);
   }

   public static boolean healthAboveDifficulty(World world, EntityPlayer player) {
      EnumDifficulty difficulty = world.getDifficulty();
      return difficulty == EnumDifficulty.HARD || difficulty == EnumDifficulty.NORMAL && player.getHealth() > 1.0F || difficulty == EnumDifficulty.EASY && player.getHealth() > 10.0F || difficulty == EnumDifficulty.PEACEFUL && player.getHealth() > 10.0F;
   }
}
