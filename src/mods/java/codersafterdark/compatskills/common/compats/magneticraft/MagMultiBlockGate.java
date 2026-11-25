package codersafterdark.compatskills.common.compats.magneticraft;

import codersafterdark.compatskills.utils.multiblock.MultiBlockGate;

public class MagMultiBlockGate implements MultiBlockGate {
   private final String multiBlockName;

   public MagMultiBlockGate(String multiBlockName) {
      this.multiBlockName = multiBlockName;
   }

   public String getMultiBlockName() {
      return this.multiBlockName;
   }

   public boolean equals(Object o) {
      return o == this || o instanceof MagMultiBlockGate && this.getMultiBlockName().equals(((MagMultiBlockGate)o).getMultiBlockName());
   }

   public int hashCode() {
      return this.multiBlockName.hashCode();
   }
}
