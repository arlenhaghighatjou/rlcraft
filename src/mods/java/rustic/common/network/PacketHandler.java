package rustic.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
   public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("rustic");
   private static int id = 0;

   public PacketHandler() {
   }

   public static void registerMessages() {
      INSTANCE.registerMessage(MessageShameFX.MessageHolder.class, MessageShameFX.class, id++, Side.CLIENT);
      INSTANCE.registerMessage(MessageVaseMeta.MessageHolder.class, MessageVaseMeta.class, id++, Side.SERVER);
      INSTANCE.registerMessage(MessageDismountChair.MessageHolder.class, MessageDismountChair.class, id++, Side.SERVER);
   }
}
