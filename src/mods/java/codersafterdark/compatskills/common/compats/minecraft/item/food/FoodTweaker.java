package codersafterdark.compatskills.common.compats.minecraft.item.food;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.minecraft.MinecraftCompatHandler;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.compatskills.FoodTweaker")
@ZenRegister
public class FoodTweaker {
   public FoodTweaker() {
   }

   @ZenMethod
   public static void addHungerLock(int level, String... requirements) {
      if (MinecraftCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new FoodTweaker.AddHungerLock(level, requirements));
      }

   }

   @ZenMethod
   public static void addSaturationLock(float level, String... requirements) {
      if (MinecraftCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new FoodTweaker.AddSaturationLock(level, requirements));
      }

   }

   private static class AddHungerLock implements IAction {
      private final int level;
      private final String[] requirements;

      private AddHungerLock(int level, String... requirements) {
         this.level = level;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkInt(this.level) & CheckMethods.checkStringArray(this.requirements)) {
            MinecraftCompatHandler.addMCLock(new HungerLockKey(this.level), RequirementHolder.fromStringList(this.requirements));
         }

      }

      public String describe() {
         return "Added Food Lock for items of hunger level: " + this.level + ", With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }

   private static class AddSaturationLock implements IAction {
      private final float level;
      private final String[] requirements;

      private AddSaturationLock(float level, String... requirements) {
         this.level = level;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkFloat(this.level) & CheckMethods.checkStringArray(this.requirements)) {
            MinecraftCompatHandler.addMCLock(new SaturationLockKey(this.level), RequirementHolder.fromStringList(this.requirements));
         }

      }

      public String describe() {
         return "Added Food Lock for items of saturation level: " + this.level + ", With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
