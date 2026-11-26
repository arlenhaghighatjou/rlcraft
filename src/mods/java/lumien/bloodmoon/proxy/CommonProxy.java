package lumien.bloodmoon.proxy;

import lumien.bloodmoon.handler.BloodmoonEventHandler;
import lumien.bloodmoon.network.PacketHandler;
import lumien.bloodmoon.server.BloodmoonHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
   public CommonProxy() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      BloodmoonEventHandler handler = new BloodmoonEventHandler();
      handler.register(MinecraftForge.EVENT_BUS);
      PacketHandler.init();
   }

   public void init(FMLInitializationEvent event) {
   }

   public void postInit(FMLPostInitializationEvent event) {
   }

   public boolean isBloodmoon() {
      return BloodmoonHandler.INSTANCE == null ? false : BloodmoonHandler.INSTANCE.isBloodmoonActive();
   }
}
