package crafttweaker.mc1120.oredict;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.api.oredict.WeightedOreDictEntry;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenExpansion("crafttweaker.oredict.IOreDictEntry")
@ZenRegister
public class ExpandOreDictEntry {
   public ExpandOreDictEntry() {
   }

   @ZenOperator(OperatorType.MOD)
   public static WeightedOreDictEntry percent(IOreDictEntry thisEntry, float p) {
      return new WeightedOreDictEntry(thisEntry, p * 0.01F);
   }

   @ZenMethod
   public static WeightedOreDictEntry weight(IOreDictEntry thisEntry, float p) {
      return new WeightedOreDictEntry(thisEntry, p);
   }
}
