package codersafterdark.compatskills.common.compats.reskillable.levellocking;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.reskillable.ReskillableCompatHandler;
import codersafterdark.compatskills.common.compats.reskillable.playerexpansion.wrapper.CTSkill;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly("crafttweaker")
@ZenClass("mods.compatskills.SkillLocks")
@ZenRegister
public class SkillLocksTweaker {
   public SkillLocksTweaker() {
   }

   @ZenMethod
   public static void addLevelLock(CTSkill skill, int level, String... defaultRequirements) {
      if (ReskillableCompatHandler.ENABLED & CheckMethods.checkSkill(skill.getSkill()) & CheckMethods.checkInt(level) & CheckMethods.checkStringArray(defaultRequirements)) {
         CompatSkills.LATE_ADDITIONS.add(new SkillLocksTweaker.AddLevelLock(skill, level, defaultRequirements));
      }

   }

   private static class AddLevelLock implements IAction {
      private final CTSkill skill;
      private final int level;
      private final String[] requirements;

      private AddLevelLock(CTSkill skill, int level, String... requirements) {
         this.skill = skill;
         this.level = level;
         this.requirements = requirements;
      }

      public void apply() {
         ReskillableCompatHandler.addReskillableLock(new SkillLock(this.skill.getSkill(), this.level), RequirementHolder.fromStringList(this.requirements));
      }

      public String describe() {
         return "Added Level-Lock " + this.skill.getName() + ": " + this.level + " With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
