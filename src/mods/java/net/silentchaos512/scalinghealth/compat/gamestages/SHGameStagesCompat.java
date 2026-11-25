package net.silentchaos512.scalinghealth.compat.gamestages;

import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.silentchaos512.scalinghealth.config.Config;

public class SHGameStagesCompat {
   private SHGameStagesCompat() {
   }

   public static int getDifficultyFromStages(EntityPlayer player) {
      int max = 0;

      for(String stage : GameStageHelper.getPlayerData(player).getStages()) {
         if (Config.Difficulty.DIFFICULTY_BY_GAME_STAGES.containsKey(stage)) {
            max = Math.max(max, ((Integer)Config.Difficulty.DIFFICULTY_BY_GAME_STAGES.get(stage)).intValue());
         }
      }

      return max;
   }
}
