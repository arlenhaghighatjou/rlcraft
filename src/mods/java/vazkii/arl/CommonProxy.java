package vazkii.arl;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.network.NetworkHandler;
import vazkii.arl.util.DropInHandler;

public class CommonProxy {
   public CommonProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      NetworkHandler.initARLMessages();
      DropInHandler.register();
   }

   public void init(FMLInitializationEvent event) {
   }

   public void postInit(FMLPostInitializationEvent event) {
   }
}
