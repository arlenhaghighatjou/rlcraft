package codersafterdark.reskillable.base;

import net.minecraft.entity.player.EntityPlayer;

public class ExperienceHelper {
   public ExperienceHelper() {
   }

   public static int getPlayerXP(EntityPlayer player) {
      return (int)((float)getExperienceForLevel(player.experienceLevel) + player.experience * (float)player.xpBarCap());
   }

   public static void drainPlayerXP(EntityPlayer player, int amount) {
      addPlayerXP(player, -amount);
   }

   public static void addPlayerXP(EntityPlayer player, int amount) {
      int experience = getPlayerXP(player) + amount;
      player.experienceTotal = experience;
      player.experienceLevel = getLevelForExperience(experience);
      int expForLevel = getExperienceForLevel(player.experienceLevel);
      player.experience = (float)(experience - expForLevel) / (float)player.xpBarCap();
   }

   public static int getExperienceForLevel(int level) {
      if (level == 0) {
         return 0;
      } else if (level > 0 && level < 17) {
         return level * level + 6 * level;
      } else {
         return level > 16 && level < 32 ? (int)(2.5D * (double)level * (double)level - 40.5D * (double)level + 360.0D) : (int)(4.5D * (double)level * (double)level - 162.5D * (double)level + 2220.0D);
      }
   }

   public static int getLevelForExperience(int experience) {
      int i;
      for(i = 0; getExperienceForLevel(i) <= experience; ++i) {
         ;
      }

      return i - 1;
   }
}
