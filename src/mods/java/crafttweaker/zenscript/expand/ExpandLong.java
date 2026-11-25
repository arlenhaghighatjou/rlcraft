package crafttweaker.zenscript.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.DataLong;
import crafttweaker.api.data.IData;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenExpansion("long")
@ZenRegister
public class ExpandLong {
   public ExpandLong() {
   }

   @ZenCaster
   public static IData asData(long value) {
      return new DataLong(value);
   }
}
