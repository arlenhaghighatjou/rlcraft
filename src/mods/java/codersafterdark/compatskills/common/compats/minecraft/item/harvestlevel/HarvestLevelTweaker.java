package codersafterdark.compatskills.common.compats.minecraft.item.harvestlevel;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.minecraft.MinecraftCompatHandler;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.compatskills.HarvestLock")
@ZenRegister
public class HarvestLevelTweaker {
   public HarvestLevelTweaker() {
   }

   @ZenMethod
   public static void addToolLevelLock(int level, String... requirements) {
      if (MinecraftCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new HarvestLevelTweaker.AddToolLevelLock((String)null, level, requirements));
      }

   }

   @ZenMethod
   public static void addToolLevelLock(String type, int level, String... requirements) {
      if (MinecraftCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new HarvestLevelTweaker.AddToolLevelLock(type, level, requirements));
      }

   }

   @ZenMethod
   public static void addBlockLevelLock(int level, String... requirements) {
      if (MinecraftCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new HarvestLevelTweaker.AddBlockLevelLock(level, requirements));
      }

   }

   private static class AddBlockLevelLock implements IAction {
      private final int harvestLevel;
      private final String[] requirements;

      private AddBlockLevelLock(int harvestLevel, String... requirements) {
         this.harvestLevel = harvestLevel;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkInt(this.harvestLevel) & CheckMethods.checkStringArray(this.requirements)) {
            MinecraftCompatHandler.addMCLock(new BlockHarvestLock(this.harvestLevel), RequirementHolder.fromStringList(this.requirements));
         }

      }

      public String describe() {
         return "Added Harvest Level Lock for blocks of harvest level: " + this.harvestLevel + ", With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }

   private static class AddToolLevelLock implements IAction {
      private final String type;
      private final int harvestLevel;
      private final String[] requirements;

      private AddToolLevelLock(String type, int harvestLevel, String... requirements) {
         this.type = type;
         this.harvestLevel = harvestLevel;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkInt(this.harvestLevel) & CheckMethods.checkStringArray(this.requirements)) {
            MinecraftCompatHandler.addMCLock(new ToolHarvestLock(this.type, this.harvestLevel), RequirementHolder.fromStringList(this.requirements));
         }

      }

      public String describe() {
         return "Added Harvest Level Lock for tools " + (this.type == null ? "" : "of type: " + this.type + ' ') + "with harvest level: " + this.harvestLevel + ", With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
