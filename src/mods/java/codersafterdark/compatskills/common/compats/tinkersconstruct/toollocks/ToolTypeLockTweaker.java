package codersafterdark.compatskills.common.compats.tinkersconstruct.toollocks;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.tinkersconstruct.TinkersCompatHandler;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly("tconstruct")
@ZenClass("mods.compatskills.ToolTypeLock")
@ZenRegister
public class ToolTypeLockTweaker {
   public ToolTypeLockTweaker() {
   }

   @ZenMethod
   public static void addToolTypeLock(IItemStack stack, String... requirements) {
      if (TinkersCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new ToolTypeLockTweaker.AddToolTypeLock(stack, requirements));
      }

   }

   private static class AddToolTypeLock implements IAction {
      private final IItemStack stack;
      private final String[] requirements;

      private AddToolTypeLock(IItemStack stack, String... requirements) {
         this.stack = stack;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkIItemstack(this.stack) & CheckMethods.checkStringArray(this.requirements)) {
            TinkersCompatHandler.addTinkersLock(new ToolTypeLockKey(CraftTweakerMC.getItemStack(this.stack).getItem()), RequirementHolder.fromStringList(this.requirements));
         }

      }

      public String describe() {
         return "Added Tool-Type Lock for Tool-Type: " + (this.stack == null ? "null" : this.stack.getDisplayName()) + " With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
