package codersafterdark.compatskills.common.compats.reskillable.skillchange.actions;

import codersafterdark.compatskills.common.compats.reskillable.playerexpansion.wrapper.CTUnlockable;
import codersafterdark.compatskills.common.compats.reskillable.skillchange.IChangeHandler;
import codersafterdark.compatskills.common.compats.reskillable.skillchange.SkillChangeHandler;
import codersafterdark.compatskills.common.compats.reskillable.skillchange.UnlockableChange;
import codersafterdark.compatskills.common.compats.reskillable.skillchange.UnlockableLock;
import codersafterdark.compatskills.common.compats.reskillable.skillchange.UnlockableUnlock;
import crafttweaker.IAction;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class UnlockableAction implements IAction {
   protected final CTUnlockable unlockable;
   protected final boolean unlock;

   private UnlockableAction(CTUnlockable unlockable, boolean unlock) {
      this.unlockable = unlockable;
      this.unlock = unlock;
   }

   UnlockableChange getUnlockableChange() {
      return (UnlockableChange)(this.unlock ? new UnlockableUnlock(this.unlockable.getUnlockable()) : new UnlockableLock(this.unlockable.getUnlockable()));
   }

   public static class Commands extends UnlockableAction {
      private final String[] commands;

      public Commands(CTUnlockable unlockable, String[] commands, boolean unlock) {
         this.commands = commands;
      }

      public void apply() {
         SkillChangeHandler.addSkillEvent(this.getUnlockableChange(), this.commands);
      }

      public String describe() {
         String descString = (String)Arrays.stream(this.commands).map((string) -> {
            return string + ", ";
         }).collect(Collectors.joining());
         return "Added commands to fire when " + this.unlockable.getName() + " is " + (this.unlock ? "unlocked" : "locked") + ". Commands: " + descString;
      }
   }

   public static class Handler extends UnlockableAction {
      private final IChangeHandler handler;

      public Handler(CTUnlockable unlockable, IChangeHandler handler, boolean unlock) {
         this.handler = handler;
      }

      public void apply() {
         SkillChangeHandler.addSkillEvent(this.getUnlockableChange(), this.handler);
      }

      public String describe() {
         return "Added handler to fire when " + this.unlockable.getName() + " is " + (this.unlock ? "unlocked" : "locked") + ".";
      }
   }
}
