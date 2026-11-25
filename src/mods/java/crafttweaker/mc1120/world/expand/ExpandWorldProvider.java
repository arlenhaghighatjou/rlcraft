package crafttweaker.mc1120.world.expand;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.world.IWorldProvider;
import crafttweaker.mc1120.world.MCWorldProvider;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethodStatic;

@ZenExpansion("crafttweaker.world.IWorldProvider")
@ZenRegister
public class ExpandWorldProvider {
   public ExpandWorldProvider() {
   }

   @ZenMethodStatic
   public static IWorldProvider getFromID(int id) {
      return new MCWorldProvider(id);
   }
}
