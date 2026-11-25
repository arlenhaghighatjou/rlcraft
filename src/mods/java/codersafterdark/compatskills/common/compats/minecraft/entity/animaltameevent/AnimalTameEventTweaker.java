package codersafterdark.compatskills.common.compats.minecraft.entity.animaltameevent;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.minecraft.MinecraftCompatHandler;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityDefinition;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.compatskills.AnimalTameLock")
@ZenRegister
public class AnimalTameEventTweaker {
   public AnimalTameEventTweaker() {
   }

   @ZenMethod
   public static void addTameLock(IEntityDefinition definition, String... defaultRequirements) {
      if (MinecraftCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new AnimalTameEventTweaker.AddTameLock(definition, defaultRequirements));
      }

   }

   private static class AddTameLock implements IAction {
      private final IEntityDefinition definition;
      private final String[] requirements;

      private AddTameLock(IEntityDefinition definition, String... requirements) {
         this.definition = definition;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkValidIEntityDefinition(this.definition) & CheckMethods.checkStringArray(this.requirements)) {
            MinecraftCompatHandler.addMCLock(new EntityTameKey(this.definition.getId()), RequirementHolder.fromStringList(this.requirements));
         }

      }

      public String describe() {
         return "Added Entity Tame Lock for Entity: " + (this.definition == null ? "null" : this.definition.getName()) + ", With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
