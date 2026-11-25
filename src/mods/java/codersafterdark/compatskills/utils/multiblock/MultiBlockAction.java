package codersafterdark.compatskills.utils.multiblock;

import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.MessageStorage;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.IAction;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class MultiBlockAction implements IAction {
   protected final String multiblockName;
   private final String failureMessage;
   private final String[] defaultRequirements;

   public MultiBlockAction(String multiblockName, String failureMessage, String... defaultRequirements) {
      this.multiblockName = multiblockName;
      this.failureMessage = failureMessage;
      this.defaultRequirements = defaultRequirements;
   }

   protected abstract MultiBlockGate getGate();

   protected abstract void addLock(MultiBlockGate var1, RequirementHolder var2);

   public void apply() {
      if (CheckMethods.checkString(this.failureMessage) & CheckMethods.checkStringArray(this.defaultRequirements)) {
         MultiBlockGate gate = this.getGate();
         if (gate != null) {
            this.addLock(gate, RequirementHolder.fromStringList(this.defaultRequirements));
            MessageStorage.setFailureMessage(gate, this.failureMessage);
         }
      }

   }

   public String describe() {
      String descString = (String)Arrays.stream(this.defaultRequirements).map((string) -> {
         return string + ", ";
      }).collect(Collectors.joining());
      return "Added MultiBlock " + this.multiblockName + " With Requirements: " + descString;
   }
}
