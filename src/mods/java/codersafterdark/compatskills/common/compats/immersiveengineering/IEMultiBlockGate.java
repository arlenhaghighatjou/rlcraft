package codersafterdark.compatskills.common.compats.immersiveengineering;

import codersafterdark.compatskills.utils.multiblock.MultiBlockGate;

public class IEMultiBlockGate implements MultiBlockGate {
   private final String multiBlockName;

   public IEMultiBlockGate(String multiBlockName) {
      this.multiBlockName = multiBlockName;
   }

   public String getMultiBlockName() {
      return this.multiBlockName;
   }

   public boolean equals(Object o) {
      return o == this || o instanceof IEMultiBlockGate && this.getMultiBlockName().equals(((IEMultiBlockGate)o).getMultiBlockName());
   }

   public int hashCode() {
      return this.multiBlockName.hashCode();
   }
}
