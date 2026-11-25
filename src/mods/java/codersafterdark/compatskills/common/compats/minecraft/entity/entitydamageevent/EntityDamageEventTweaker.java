package codersafterdark.compatskills.common.compats.minecraft.entity.entitydamageevent;

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

@ZenClass("mods.compatskills.EntityDamageLock")
@ZenRegister
public class EntityDamageEventTweaker {
   public EntityDamageEventTweaker() {
   }

   @ZenMethod
   public static void addEntityLock(IEntityDefinition definition, String... defaultRequirements) {
      if (MinecraftCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new EntityDamageEventTweaker.AddDamageLock(definition, defaultRequirements));
      }

   }

   private static class AddDamageLock implements IAction {
      private final IEntityDefinition definition;
      private final String[] requirements;

      private AddDamageLock(IEntityDefinition definition, String... requirements) {
         this.definition = definition;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkValidIEntityDefinition(this.definition) & CheckMethods.checkStringArray(this.requirements)) {
            MinecraftCompatHandler.addMCLock(new EntityDamageKey(this.definition.getId()), RequirementHolder.fromStringList(this.requirements));
         }

      }

      public String describe() {
         return "Added Entity Damage Lock for Entity: " + (this.definition == null ? "null" : this.definition.getName()) + ", With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
