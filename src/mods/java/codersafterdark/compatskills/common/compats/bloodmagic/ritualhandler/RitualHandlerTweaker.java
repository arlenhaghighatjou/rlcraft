package codersafterdark.compatskills.common.compats.bloodmagic.ritualhandler;

import WayofTime.bloodmagic.BloodMagic;
import WayofTime.bloodmagic.ritual.Ritual;
import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.bloodmagic.BMCompatHandler;
import codersafterdark.compatskills.common.compats.bloodmagic.ritualhandler.keys.RitualCostLockKey;
import codersafterdark.compatskills.common.compats.bloodmagic.ritualhandler.keys.RitualCrystalLockKey;
import codersafterdark.compatskills.common.compats.bloodmagic.ritualhandler.keys.RitualNameLockKey;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly("bloodmagic")
@ZenClass("mods.compatskills.RitualHandler")
@ZenRegister
public class RitualHandlerTweaker {
   public RitualHandlerTweaker() {
   }

   @ZenMethod
   public static void addRitualLock(String ritual, String... requirements) {
      if (BMCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new RitualHandlerTweaker.AddRitualLock(ritual, requirements));
      }

   }

   @ZenMethod
   public static void addRitualCostLock(int activationCost, String... requirements) {
      if (BMCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new RitualHandlerTweaker.AddRitualCostLock(activationCost, requirements));
      }

   }

   @ZenMethod
   public static void addRitualCrystalLock(int crystalLevel, String... requirements) {
      if (BMCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new RitualHandlerTweaker.AddRitualCrystalLock(crystalLevel, requirements));
      }

   }

   private static class AddRitualCostLock implements IAction {
      private final int activationCost;
      private final String[] requirements;

      private AddRitualCostLock(int activationCost, String... requirements) {
         this.activationCost = activationCost;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkInt(this.activationCost) & CheckMethods.checkStringArray(this.requirements)) {
            BMCompatHandler.addBMLock(new RitualCostLockKey(this.activationCost), RequirementHolder.fromStringList(this.requirements));
         }

      }

      public String describe() {
         return "Added Ritual Lock for rituals with a costs equal to " + this.activationCost + " With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }

   private static class AddRitualCrystalLock implements IAction {
      private final int crystalLevel;
      private final String[] requirements;

      private AddRitualCrystalLock(int crystalLevel, String... requirements) {
         this.crystalLevel = crystalLevel;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkInt(this.crystalLevel) & CheckMethods.checkStringArray(this.requirements)) {
            BMCompatHandler.addBMLock(new RitualCrystalLockKey(this.crystalLevel), RequirementHolder.fromStringList(this.requirements));
         }

      }

      public String describe() {
         return "Added Ritual Lock for rituals with a crystal requirement of level: " + this.crystalLevel + " With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }

   private static class AddRitualLock implements IAction {
      private final String ritual;
      private final String[] requirements;

      private AddRitualLock(String ritual, String... requirements) {
         this.ritual = ritual;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkRitual(this.ritual) & CheckMethods.checkStringArray(this.requirements)) {
            Ritual trueRitual = BloodMagic.RITUAL_MANAGER.getRitual(this.ritual);
            if (trueRitual != null) {
               BMCompatHandler.addBMLock(new RitualNameLockKey(trueRitual), RequirementHolder.fromStringList(this.requirements));
            }
         }

      }

      public String describe() {
         return "Added Ritual Lock for Ritual: " + this.ritual + " With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
