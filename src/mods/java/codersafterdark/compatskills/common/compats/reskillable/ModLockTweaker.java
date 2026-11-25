package codersafterdark.compatskills.common.compats.reskillable;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly("crafttweaker")
@ZenClass("mods.compatskills.ModLock")
@ZenRegister
public class ModLockTweaker {
   public ModLockTweaker() {
   }

   @ZenMethod
   public static void addModLock(String modId, String... locked) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new ModLockTweaker.Add(modId, locked));
      }

   }

   private static class Add implements IAction {
      private final String modID;
      private final String[] requirements;

      private Add(String modID, String... requirements) {
         this.modID = modID;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkModLoaded(this.modID) & CheckMethods.checkStringArray(this.requirements)) {
            RequirementHolder holder = RequirementHolder.fromStringList(this.requirements);
            LevelLockHandler.addModLock(this.modID, holder);
         }

      }

      public String describe() {
         return "Setting the requirement of Mod: " + this.modID + " to Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
