package crafttweaker.mc1120.world.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.util.Position3f;
import crafttweaker.mc1120.util.MCPosition3f;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethodStatic;

@ZenExpansion("crafttweaker.util.Position3f")
@ZenRegister
public class ExpandPosition3f {
   public ExpandPosition3f() {
   }

   @ZenMethodStatic
   public static Position3f create(float x, float y, float z) {
      return new MCPosition3f(x, y, z);
   }
}
