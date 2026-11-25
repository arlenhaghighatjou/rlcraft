package codersafterdark.compatskills.common.compats.tinkersconstruct.materiallocks;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.tinkersconstruct.TinkersCompatHandler;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import slimeknights.tconstruct.library.TinkerRegistry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly("tconstruct")
@ZenClass("mods.compatskills.MaterialLock")
@ZenRegister
public class MaterialLockTweaker {
   public MaterialLockTweaker() {
   }

   @ZenMethod
   public static void addMaterialLock(String id, String... requirements) {
      if (TinkersCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new MaterialLockTweaker.AddMaterialLock(id, requirements));
      }

   }

   private static class AddMaterialLock implements IAction {
      private final String id;
      private final String[] requirements;

      private AddMaterialLock(String id, String... requirements) {
         this.id = id;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkMaterial(this.id)) {
            TinkersCompatHandler.addTinkersLock(new MaterialLockKey(TinkerRegistry.getMaterial(this.id)), RequirementHolder.fromStringList(this.requirements));
         }

      }

      public String describe() {
         return "Added Material Lock for Material: " + this.id + " With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
