package codersafterdark.compatskills.common.compats.reskillable.skillchange.actions;

import codersafterdark.compatskills.common.compats.reskillable.playerexpansion.wrapper.CTSkill;
import codersafterdark.compatskills.common.compats.reskillable.skillchange.IChangeHandler;
import codersafterdark.compatskills.common.compats.reskillable.skillchange.SkillChangeHandler;
import codersafterdark.compatskills.common.compats.reskillable.skillchange.SkillLevel;
import crafttweaker.IAction;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class LevelUpAction implements IAction {
   protected final CTSkill skill;
   protected final int level;

   private LevelUpAction(CTSkill skill, int level) {
      this.skill = skill;
      this.level = level;
   }

   SkillLevel getSkillLevel() {
      return new SkillLevel(this.skill.getSkill(), this.level);
   }

   public static class Commands extends LevelUpAction {
      private final String[] commands;

      public Commands(CTSkill skill, int level, String... commands) {
         this.commands = commands;
      }

      public void apply() {
         SkillChangeHandler.addSkillEvent(this.getSkillLevel(), this.commands);
      }

      public String describe() {
         String descString = (String)Arrays.stream(this.commands).map((string) -> {
            return string + ", ";
         }).collect(Collectors.joining());
         return "Added commands to fire when " + this.skill.getName() + " reaches level " + this.level + ". Commands: " + descString;
      }
   }

   public static class Handler extends LevelUpAction {
      private final IChangeHandler handler;

      public Handler(CTSkill skill, int level, IChangeHandler handler) {
         this.handler = handler;
      }

      public void apply() {
         SkillChangeHandler.addSkillEvent(this.getSkillLevel(), this.handler);
      }

      public String describe() {
         return "Added handler to fire when " + this.skill.getName() + " reaches level " + this.level + ".";
      }
   }
}
