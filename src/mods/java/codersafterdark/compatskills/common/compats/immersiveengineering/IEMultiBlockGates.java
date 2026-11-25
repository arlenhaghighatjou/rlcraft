package codersafterdark.compatskills.common.compats.immersiveengineering;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.multiblock.MultiBlockAction;
import codersafterdark.compatskills.utils.multiblock.MultiBlockGate;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ModOnly("immersiveengineering")
@ZenClass("mods.compatskills.IEMultiBlockGate")
@ZenRegister
public class IEMultiBlockGates {
   public IEMultiBlockGates() {
   }

   @ZenMethod
   public static void addGate(String multiBlockName, String failureMessage, String... defaultRequirements) {
      if (IECompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new IEMultiBlockGates.IEMultiBlockAction(multiBlockName, failureMessage, defaultRequirements));
      }

   }

   private static class IEMultiBlockAction extends MultiBlockAction {
      private IEMultiBlockAction(String gateName, String failureMessage, String... defaultRequirements) {
         super(gateName, failureMessage, defaultRequirements);
      }

      protected MultiBlockGate getGate() {
         return CheckMethods.checkValidMultiblockNameIE(this.multiblockName) ? new IEMultiBlockGate(this.multiblockName) : null;
      }

      protected void addLock(MultiBlockGate gate, RequirementHolder holder) {
         IECompatHandler.addIELock(gate, holder);
      }
   }
}
