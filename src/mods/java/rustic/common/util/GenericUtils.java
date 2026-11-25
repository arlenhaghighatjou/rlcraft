package rustic.common.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GenericUtils {
   public GenericUtils() {
   }

   public static RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids) {
      float f = playerIn.rotationPitch;
      float f1 = playerIn.rotationYaw;
      double d0 = playerIn.posX;
      double d1 = playerIn.posY + (double)playerIn.getEyeHeight();
      double d2 = playerIn.posZ;
      Vec3d vec3d = new Vec3d(d0, d1, d2);
      float f2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
      float f3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
      float f4 = -MathHelper.cos(-f * 0.017453292F);
      float f5 = MathHelper.sin(-f * 0.017453292F);
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      double d3 = 5.0D;
      if (playerIn instanceof EntityPlayerMP) {
         d3 = ((EntityPlayerMP)playerIn).interactionManager.getBlockReachDistance();
      }

      Vec3d vec3d1 = vec3d.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
      return worldIn.rayTraceBlocks(vec3d, vec3d1, useLiquids, !useLiquids, false);
   }
}
