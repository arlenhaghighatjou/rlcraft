package crafttweaker.zenscript.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.IData;
import java.util.Map;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenExpansion("any[any]")
@ZenRegister
public class ExpandAnyDict {
   public ExpandAnyDict() {
   }

   @ZenCaster
   public static IData asData(Map<String, IData> values) {
      return new DataMap(values, true);
   }
}
