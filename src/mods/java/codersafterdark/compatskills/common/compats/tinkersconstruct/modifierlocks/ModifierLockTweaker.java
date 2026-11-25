package codersafterdark.compatskills.common.compats.tinkersconstruct.modifierlocks;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.tinkersconstruct.TinkersCompatHandler;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.IModifier;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly("tconstruct")
@ZenClass("mods.compatskills.ModifierLock")
@ZenRegister
public class ModifierLockTweaker {
   public ModifierLockTweaker() {
   }

   @ZenMethod
   public static void addModifierLock(String identifier, String... requirements) {
      if (TinkersCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new ModifierLockTweaker.AddModifierLock(identifier, requirements));
      }

   }

   private static class AddModifierLock implements IAction {
      private final String id;
      private final String[] requirements;

      private AddModifierLock(String id, String... requirements) {
         this.id = id;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkModifier(this.id)) {
            IModifier modifier = TinkerRegistry.getModifier(this.id);
            if (modifier != null) {
               TinkersCompatHandler.addTinkersLock(new ModifierLockKey(modifier), RequirementHolder.fromStringList(this.requirements));
            }
         }

      }

      public String describe() {
         return "Added Modifier Lock for Modifier: " + this.id + " With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
