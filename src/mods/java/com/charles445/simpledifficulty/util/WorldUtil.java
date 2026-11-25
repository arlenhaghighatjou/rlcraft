package com.charles445.simpledifficulty.util;

import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class WorldUtil {
   public WorldUtil() {
   }

   public static BlockPos getSidedBlockPos(World world, Entity entity) {
      if (!world.isRemote) {
         return entity.getPosition();
      } else if (entity instanceof EntityPlayer) {
         return new BlockPos(entity.getPositionVector().addVector(0.0D, 0.5D, 0.0D));
      } else {
         return entity instanceof EntityItemFrame ? new BlockPos(entity.getPositionVector().addVector(0.0D, -0.45D, 0.0D)) : entity.getPosition();
      }
   }

   public static int calculateClientWorldEntityTemperature(World world, Entity entity) {
      return TemperatureUtil.clampTemperature(TemperatureUtil.getWorldTemperature(world, getSidedBlockPos(world, entity)));
   }

   public static boolean isChunkLoaded(World world, BlockPos pos) {
      if (world.isRemote) {
         return true;
      } else {
         return world instanceof WorldServer ? ((WorldServer)world).getChunkProvider().chunkExists(pos.getX() >> 4, pos.getZ() >> 4) : false;
      }
   }
}
