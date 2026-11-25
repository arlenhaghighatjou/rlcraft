package codersafterdark.compatskills.common.compats.reskillable;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly("crafttweaker")
@ZenClass("mods.compatskills.Requirement")
@ZenRegister
public class RequirementTweaker {
   public RequirementTweaker() {
   }

   @ZenMethod
   public static void addRequirement(IItemStack item, String... locked) {
      if (ReskillableCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new RequirementTweaker.Add(item, locked));
      }

   }

   private static class Add implements IAction {
      private final IItemStack stack;
      private final String[] requirements;

      private Add(IItemStack stack, String... requirements) {
         this.stack = stack;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkIItemstack(this.stack) & CheckMethods.checkStringArray(this.requirements)) {
            RequirementHolder h = RequirementHolder.fromStringList(this.requirements);
            LevelLockHandler.addLock(CraftTweakerMC.getItemStack(this.stack), h);
         }

      }

      public String describe() {
         return "Setting the requirement of: " + (this.stack == null ? "null" : this.stack.getDisplayName()) + " to Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
