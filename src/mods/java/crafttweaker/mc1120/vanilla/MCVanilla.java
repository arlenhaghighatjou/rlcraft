package crafttweaker.mc1120.vanilla;

import crafttweaker.api.vanilla.ISeedRegistry;
import crafttweaker.api.vanilla.IVanilla;

public class MCVanilla implements IVanilla {
   private final MCSeedRegistry seedRegistry = new MCSeedRegistry();

   public MCVanilla() {
   }

   public ISeedRegistry getSeeds() {
      return this.seedRegistry;
   }
}
