package lykrast.defiledlands.core;

import lykrast.defiledlands.common.init.ModEntities;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent e) {
      super.preInit(e);
      ModEntities.initModels();
   }
}
