package crafttweaker.zenscript.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.DataFloat;
import crafttweaker.api.data.IData;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenExpansion("float")
@ZenRegister
public class ExpandFloat {
   public ExpandFloat() {
   }

   @ZenCaster
   public static IData asData(float value) {
      return new DataFloat(value);
   }
}
