package crafttweaker.zenscript.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.DataList;
import crafttweaker.api.data.IData;
import java.util.Arrays;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenExpansion("any[]")
@ZenRegister
public class ExpandAnyArray {
   public ExpandAnyArray() {
   }

   @ZenCaster
   public static IData asData(IData[] values) {
      return new DataList(Arrays.asList(values), true);
   }
}
