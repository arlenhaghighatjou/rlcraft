package levelup2.skills.combat;

import levelup2.skills.SkillRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class StealthLib {
   public StealthLib() {
   }

   public static boolean canSeePlayer(EntityLivingBase entity) {
      EntityPlayer player = entity.world.getClosestPlayerToEntity(entity, 16.0D);
      return player != null && entity.canEntityBeSeen(player) && (!player.isSneaking() || entityHasVisionOf(entity, player));
   }

   public static boolean entityHasVisionOf(EntityLivingBase living, EntityPlayer player) {
      if (living != null && player != null) {
         if (getDistanceFrom(living, player) > 256.0F - (float)SkillRegistry.getSkillLevel(player, "levelup:stealth") * 12.8F) {
            return false;
         } else {
            return living.canEntityBeSeen(player) && entityIsFacing(player, living);
         }
      } else {
         return false;
      }
   }

   public static float getDistanceFrom(EntityLivingBase living, EntityLivingBase living1) {
      return (float)MathHelper.floor((living1.posX - living.posX) * (living1.posX - living.posX) + (living1.posZ - living.posZ) * (living1.posZ - living.posZ));
   }

   public static boolean entityIsFacing(EntityLivingBase entityLiving, EntityLivingBase entityliving1) {
      if (entityLiving != null && entityliving1 != null) {
         float f = -((float)(entityliving1.posX - entityLiving.posX));
         float f1 = (float)(entityliving1.posZ - entityLiving.posZ);
         float f2 = entityLiving.rotationYaw;
         if (f2 < 0.0F) {
            float f3 = ((float)MathHelper.floor(MathHelper.abs(f2) / 360.0F) + 1.0F) * 360.0F;
            f2 += f3;
         } else {
            while(f2 > 360.0F) {
               f2 -= 360.0F;
            }
         }

         float f4 = (float)(Math.atan2((double)f, (double)f1) * 180.0D / 3.141592653589793D);
         if (f < 0.0F) {
            f4 += 360.0F;
         }

         return compareAngles(f2, f4, 22.5F);
      } else {
         return false;
      }
   }

   private static boolean compareAngles(float f, float f1, float f2) {
      if (MathHelper.abs(f - f1) < f2) {
         return true;
      } else if (f + f2 >= 360.0F && f + f2 - 360.0F > f1) {
         return true;
      } else {
         return f1 + f2 >= 360.0F && f1 + f2 - 360.0F > f;
      }
   }
}
