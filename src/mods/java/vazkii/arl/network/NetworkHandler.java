package vazkii.arl.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import vazkii.arl.network.message.MessageDropIn;

public class NetworkHandler {
   public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("autoreglib");
   private static int i = 0;

   public NetworkHandler() {
   }

   public static <T extends NetworkMessage<T>> void register(Class<T> clazz, Side handlerSide) {
      INSTANCE.registerMessage(clazz, clazz, i++, handlerSide);
   }

   public static void initARLMessages() {
      register(MessageDropIn.class, Side.SERVER);
   }
}
