package com.Shultrea.Rin.Utility_Sector;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class DebugUtil {
   public DebugUtil() {
   }

   public static void messageAll(String s) {
      for(EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
         player.sendMessage(new TextComponentString(s));
      }

   }

   public static void dotPathPoints(World world, Path path) {
      for(int i = 0; i < path.getCurrentPathLength(); ++i) {
         PathPoint point = path.getPathPointFromIndex(i);
         sendParticle(world, EnumParticleTypes.FLAME, (double)point.x, (double)point.y, (double)point.z);
      }

   }

   public static void sendParticle(World world, EnumParticleTypes type, double x, double y, double z) {
      if (world.isRemote) {
         world.spawnParticle(type, x, y, z, 0.0D, 0.0D, 0.0D, new int[0]);
      } else {
         WorldServer worldServer = (WorldServer)world;
         worldServer.spawnParticle(type, true, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }
}
