package crafttweaker.zenscript.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.DataByteArray;
import crafttweaker.api.data.IData;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenExpansion("byte[]")
@ZenRegister
public class ExpandByteArray {
   public ExpandByteArray() {
   }

   @ZenCaster
   public static IData asData(byte[] values) {
      return new DataByteArray(values, true);
   }
}
