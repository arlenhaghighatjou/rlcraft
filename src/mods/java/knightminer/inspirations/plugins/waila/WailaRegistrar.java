package knightminer.inspirations.plugins.waila;

import knightminer.inspirations.library.Util;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import mcp.mobius.waila.api.IWailaRegistrar;

public class WailaRegistrar {
   static final String CONFIG_CAULDRON = Util.prefix("cauldron");

   public WailaRegistrar() {
   }

   public static void registerWaila(IWailaRegistrar registrar) {
      registrar.addConfig("Inspirations", CONFIG_CAULDRON, true);
      registrar.registerBodyProvider(new CauldronProvider(), TileCauldron.class);
   }
}
