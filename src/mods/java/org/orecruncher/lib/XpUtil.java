package org.orecruncher.lib;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import org.orecruncher.LibBase;

public class XpUtil {
   public static final int RATIO = 20;
   private static final int MAX_LEVEL = 21862;
   private static final int[] xpmap = new int[21863];

   public XpUtil() {
   }

   public static int liquidToExperience(int liquid) {
      return liquid / 20;
   }

   public static int experienceToLiquid(int xp) {
      return xp * 20;
   }

   public static int getExperienceForLevel(int level) {
      if (level <= 0) {
         return 0;
      } else {
         return level > 21862 ? Integer.MAX_VALUE : xpmap[level];
      }
   }

   public static int getXpBarCapacity(int level) {
      if (level >= 30) {
         return 112 + (level - 30) * 9;
      } else {
         return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
      }
   }

   public static int getLevelForExperience(int experience) {
      for(int i = 1; i < xpmap.length; ++i) {
         if (xpmap[i] > experience) {
            return i - 1;
         }
      }

      return xpmap.length;
   }

   public static int getPlayerXP(@Nonnull EntityPlayer player) {
      return (int)((float)getExperienceForLevel(player.experienceLevel) + player.experience * (float)player.xpBarCap());
   }

   public static void addPlayerXP(@Nonnull EntityPlayer player, int amount) {
      int experience = Math.max(0, getPlayerXP(player) + amount);
      player.experienceTotal = experience;
      player.experienceLevel = getLevelForExperience(experience);
      int expForLevel = getExperienceForLevel(player.experienceLevel);
      player.experience = (float)(experience - expForLevel) / (float)getXpBarCapacity(player.experienceLevel);
   }

   static {
      int res = 0;

      for(int i = 0; i <= 21862; ++i) {
         if (res < 0) {
            res = Integer.MAX_VALUE;
            LibBase.log().warn("Internal XP calculation is wrong. Level " + i + " already maxes out.");
         }

         xpmap[i] = res;
         res += getXpBarCapacity(i);
      }

   }
}
