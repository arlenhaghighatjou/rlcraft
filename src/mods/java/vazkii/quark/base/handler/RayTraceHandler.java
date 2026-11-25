package vazkii.quark.base.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public class RayTraceHandler {
   public RayTraceHandler() {
   }

   public static RayTraceResult rayTrace(World world, EntityPlayer player, boolean stopOnLiquid) {
      return rayTrace(world, player, stopOnLiquid, getEntityRange(player));
   }

   public static RayTraceResult rayTrace(World world, Entity player, boolean stopOnLiquid, double range) {
      Pair<Vec3d, Vec3d> params = getEntityParams(player);
      return rayTrace(world, (Vec3d)params.getLeft(), (Vec3d)params.getRight(), stopOnLiquid, range);
   }

   public static RayTraceResult rayTrace(World world, Vec3d startPos, Vec3d ray, boolean stopOnLiquid, double range) {
      return rayTrace(world, startPos, ray.scale(range), stopOnLiquid);
   }

   public static RayTraceResult rayTrace(World world, Vec3d startPos, Vec3d ray, boolean stopOnLiquid) {
      Vec3d end = startPos.add(ray);
      return world.rayTraceBlocks(startPos, end, stopOnLiquid);
   }

   public static double getEntityRange(EntityLivingBase player) {
      return player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
   }

   public static Pair<Vec3d, Vec3d> getEntityParams(Entity player) {
      float scale = 1.0F;
      float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * scale;
      float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * scale;
      double posX = player.prevPosX + (player.posX - player.prevPosX) * (double)scale;
      double posY = player.prevPosY + (player.posY - player.prevPosY) * (double)scale;
      if (player instanceof EntityPlayer) {
         posY += (double)((EntityPlayer)player).eyeHeight;
      }

      double posZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)scale;
      Vec3d rayPos = new Vec3d(posX, posY, posZ);
      float zYaw = -MathHelper.cos(yaw * 3.1415927F / 180.0F);
      float xYaw = MathHelper.sin(yaw * 3.1415927F / 180.0F);
      float pitchMod = -MathHelper.cos(pitch * 3.1415927F / 180.0F);
      float azimuth = -MathHelper.sin(pitch * 3.1415927F / 180.0F);
      float xLen = xYaw * pitchMod;
      float yLen = zYaw * pitchMod;
      Vec3d ray = new Vec3d((double)xLen, (double)azimuth, (double)yLen);
      return Pair.of(rayPos, ray);
   }
}
