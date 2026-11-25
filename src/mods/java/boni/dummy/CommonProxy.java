package boni.dummy;

import boni.dummy.network.DamageMessage;
import boni.dummy.network.SyncEquipmentMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy {
   public SimpleNetworkWrapper network;

   public CommonProxy() {
   }

   public void preinit() {
   }

   public void init() {
      this.network = NetworkRegistry.INSTANCE.newSimpleChannel("TestDummy");
      this.network.registerMessage(DamageMessage.MessageHandlerClient.class, DamageMessage.class, 0, Side.CLIENT);
      this.network.registerMessage(SyncEquipmentMessage.MessageHandlerClient.class, SyncEquipmentMessage.class, 1, Side.CLIENT);
   }
}
