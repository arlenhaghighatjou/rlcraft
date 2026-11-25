package c4.corpsecomplex.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {
   public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("corpsecomplex");
   private static int id = 0;

   public NetworkHandler() {
   }

   public static void init() {
      registerMessage(TeleportSoundMessage.TeleportSoundMessageHandler.class, TeleportSoundMessage.class, Side.CLIENT);
      registerMessage(TeleportEffectMessage.TeleportEffectMessageHandler.class, TeleportEffectMessage.class, Side.CLIENT);
      registerMessage(TANMessage.TANMessageHandler.class, TANMessage.class, Side.CLIENT);
   }

   private static void registerMessage(Class messageHandler, Class requestMessageType, Side side) {
      INSTANCE.registerMessage(messageHandler, requestMessageType, id++, side);
   }
}
