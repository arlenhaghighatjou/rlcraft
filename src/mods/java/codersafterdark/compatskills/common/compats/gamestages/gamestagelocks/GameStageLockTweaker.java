package codersafterdark.compatskills.common.compats.gamestages.gamestagelocks;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.gamestages.GameStageCompatHandler;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly("gamestages")
@ZenClass("mods.compatskills.GameStageLocks")
@ZenRegister
public class GameStageLockTweaker {
   public GameStageLockTweaker() {
   }

   @ZenMethod
   public static void addGameStageLock(String gamestage, String... defaultRequirements) {
      if (GameStageCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new GameStageLockTweaker.AddGameStageLock(gamestage, defaultRequirements));
      }

   }

   private static class AddGameStageLock implements IAction {
      private final String gameStage;
      private final String[] requirements;

      private AddGameStageLock(String gameStage, String... requirements) {
         this.gameStage = gameStage;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkString(this.gameStage) & CheckMethods.checkStringArray(this.requirements)) {
            GameStageCompatHandler.addGameStageLock(new GameStageLock(this.gameStage), RequirementHolder.fromStringList(this.requirements));
         }

      }

      public String describe() {
         return "Added GameStage Lock: " + this.gameStage + ", With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
