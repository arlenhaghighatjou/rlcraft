package bq_standard;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSetExperience;

public class XPHelper {
   private static long[] QUICK_XP = new long[2147];

   public XPHelper() {
   }

   public static void addXP(EntityPlayer player, long xp) {
      addXP(player, xp, true);
   }

   public static void addXP(EntityPlayer player, long xp, boolean sync) {
      long experience = getPlayerXP(player) + xp;
      player.experienceTotal = experience >= 2147483647L ? Integer.MAX_VALUE : (int)experience;
      player.experienceLevel = getXPLevel(experience);
      long expForLevel = getLevelXP(player.experienceLevel);
      player.experience = (float)((double)(experience - expForLevel) / (double)xpBarCap(player));
      player.experience = Math.max(0.0F, player.experience);
      if (sync && player instanceof EntityPlayerMP) {
         syncXP((EntityPlayerMP)player);
      }

   }

   public static void syncXP(EntityPlayerMP player) {
      player.connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
   }

   public static long getPlayerXP(EntityPlayer player) {
      return getLevelXP(player.experienceLevel) + (long)((double)xpBarCap(player) * Math.max(0.0D, (double)player.experience));
   }

   public static long xpBarCap(EntityPlayer player) {
      if (player.experienceLevel < 16) {
         return (long)(2.0D * (double)player.experienceLevel + 7.0D);
      } else {
         return player.experienceLevel < 31 ? (long)(5.0D * (double)player.experienceLevel - 38.0D) : (long)(9.0D * (double)player.experienceLevel - 158.0D);
      }
   }

   public static int getXPLevel(long xp) {
      if (xp <= 0L) {
         return 0;
      } else {
         int i;
         for(i = 0; i < QUICK_XP.length && QUICK_XP[i] <= xp; ++i) {
            ;
         }

         if (i > 0) {
            i = (i - 1) * 1000000;
         }

         while(i < Integer.MAX_VALUE && getLevelXP(i) <= xp) {
            ++i;
         }

         return i - 1;
      }
   }

   public static long getLevelXP(int level) {
      if (level <= 0) {
         return 0L;
      } else if (level < 17) {
         return (long)(Math.pow((double)level, 2.0D) + (double)level * 6.0D);
      } else {
         return level < 32 ? (long)(2.5D * Math.pow((double)level, 2.0D) - (double)level * 40.5D + 360.0D) : (long)(4.5D * Math.pow((double)level, 2.0D) - (double)level * 162.5D + 2220.0D);
      }
   }

   static {
      for(int i = 0; i < QUICK_XP.length; ++i) {
         QUICK_XP[i] = getLevelXP(i * 1000000);
      }

   }
}
