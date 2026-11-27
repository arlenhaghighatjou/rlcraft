package maxhyper.dynamictreesdefiledlands.proxy;

import maxhyper.dynamictreesdefiledlands.cells.CellKits;
import maxhyper.dynamictreesdefiledlands.event.TreeGenCancelDefiledEventHandler;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {
   public CommonProxy() {
   }

   public void preInit() {
      CellKits.init();
      (new TreeGenCancelDefiledEventHandler()).register(MinecraftForge.TERRAIN_GEN_BUS);
   }

   public void init() {
   }

   public void postInit() {
   }
}
