package crafttweaker.zenscript.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.DataShort;
import crafttweaker.api.data.IData;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenExpansion("short")
@ZenRegister
public class ExpandShort {
   public ExpandShort() {
   }

   @ZenCaster
   public static IData asNBT(short value) {
      return new DataShort(value);
   }
}
