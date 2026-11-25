package lumien.bloodmoon.proxy;

import lumien.bloodmoon.client.ClientBloodmoonHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      FMLCommonHandler.instance().bus().register(ClientBloodmoonHandler.INSTANCE);
      MinecraftForge.EVENT_BUS.register(ClientBloodmoonHandler.INSTANCE);
   }

   public boolean isBloodmoon() {
      return ClientBloodmoonHandler.INSTANCE.isBloodmoonActive();
   }
}
