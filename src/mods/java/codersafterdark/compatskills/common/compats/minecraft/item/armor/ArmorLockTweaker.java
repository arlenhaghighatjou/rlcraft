package codersafterdark.compatskills.common.compats.minecraft.item.armor;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.minecraft.MinecraftCompatHandler;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.compatskills.ArmorLock")
@ZenRegister
public class ArmorLockTweaker {
   public ArmorLockTweaker() {
   }

   @ZenMethod
   public static void addArmorLock(double armor, String... requirements) {
      if (MinecraftCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new ArmorLockTweaker.AddArmorLock(armor, requirements));
      }

   }

   private static class AddArmorLock implements IAction {
      private final double armor;
      private final String[] requirements;

      private AddArmorLock(double armor, String... requirements) {
         this.armor = armor;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkDouble(this.armor) & CheckMethods.checkStringArray(this.requirements)) {
            MinecraftCompatHandler.addMCLock(new ArmorLockKey(this.armor), RequirementHolder.fromStringList(this.requirements));
         }

      }

      public String describe() {
         return "Added Armor Lock of: " + this.armor + ", With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
