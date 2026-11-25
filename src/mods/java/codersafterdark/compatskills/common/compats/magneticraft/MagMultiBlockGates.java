package codersafterdark.compatskills.common.compats.magneticraft;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.multiblock.MultiBlockAction;
import codersafterdark.compatskills.utils.multiblock.MultiBlockGate;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly("magneticraft")
@ZenClass("mods.compatskills.MagMultiBlockGates")
@ZenRegister
public class MagMultiBlockGates {
   public MagMultiBlockGates() {
   }

   @ZenMethod
   public static void addGate(String multiBlockName, String failureMessage, String... defaultRequirements) {
      if (MagCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new MagMultiBlockGates.MagMultiBlockAction(multiBlockName, failureMessage, defaultRequirements));
      }

   }

   private static class MagMultiBlockAction extends MultiBlockAction {
      private MagMultiBlockAction(String gateName, String failureMessage, String... defaultRequirements) {
         super(gateName, failureMessage, defaultRequirements);
      }

      protected MultiBlockGate getGate() {
         return CheckMethods.checkValidMultiblockNameMag(this.multiblockName) ? new MagMultiBlockGate(this.multiblockName) : null;
      }

      protected void addLock(MultiBlockGate gate, RequirementHolder holder) {
         MagCompatHandler.addMagLock(gate, holder);
      }
   }
}
