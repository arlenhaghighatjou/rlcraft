package xzeroair.trinkets.traits.abilities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;

public class AbilityCharge extends Ability {
   static int time = 0;
   static double prev_posx;
   static double prev_posy;
   static double prev_posz;

   public AbilityCharge() {
      super("Charge");
   }

   public static void count() {
      ++time;
   }

   public static int getCount() {
      return time;
   }

   public static void resetCount() {
      time = 0;
   }

   public static void startVec(EntityLivingBase player) {
      prev_posx = player.posX;
      prev_posy = player.posY;
      prev_posz = player.posZ;
   }

   public static Vec3d lastVec() {
      Vec3d lastPosVec = new Vec3d(prev_posx, prev_posy, prev_posz);
      return lastPosVec;
   }

   public static double entitySpeed(EntityLivingBase player) {
      Vec3d currentPosVec = new Vec3d(player.posX, player.posY, player.posZ);
      double distanceTraveled = lastVec().distanceTo(currentPosVec);
      return distanceTraveled;
   }

   protected boolean movingForward(EntityLivingBase entity, EnumFacing facing) {
      return (double)facing.getDirectionVec().getX() * entity.motionX > 0.0D || (double)facing.getDirectionVec().getZ() * entity.motionZ > 0.0D;
   }
}
