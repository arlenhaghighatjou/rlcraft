package slimeknights.mantle.client;

import net.minecraftforge.common.MinecraftForge;
import slimeknights.mantle.common.CommonProxy;

public class ClientProxy extends CommonProxy {

   public ClientProxy() {
   }

   public void preInit() {
   }

   public void init() {
   }

   public void postInit() {
      (new ExtraHeartRenderHandler()).register(MinecraftForge.EVENT_BUS);
   }
}
