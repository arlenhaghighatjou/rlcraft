package crafttweaker.zenscript.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.DataIntArray;
import crafttweaker.api.data.IData;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenExpansion("int[]")
@ZenRegister
public class ExpandIntArray {
   public ExpandIntArray() {
   }

   @ZenCaster
   public static IData asData(int[] values) {
      return new DataIntArray(values, true);
   }
}
