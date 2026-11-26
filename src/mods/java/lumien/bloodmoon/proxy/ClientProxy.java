package lumien.bloodmoon.proxy;

import lumien.bloodmoon.client.ClientBloodmoonHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      ClientBloodmoonHandler.INSTANCE.register(MinecraftForge.EVENT_BUS);
   }

   public boolean isBloodmoon() {
      return ClientBloodmoonHandler.INSTANCE.isBloodmoonActive();
   }
}
