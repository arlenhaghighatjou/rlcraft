package sereneseasons.handler;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import sereneseasons.network.message.MessageSyncConfigs;
import sereneseasons.network.message.MessageSyncSeasonCycle;

public class PacketHandler {
   public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel("sereneseasons");

   public PacketHandler() {
   }

   public static void init() {
      instance.registerMessage(new MessageSyncSeasonCycle(), MessageSyncSeasonCycle.class, MessageSyncSeasonCycle::new, 3, Side.CLIENT);
      instance.registerMessage(new MessageSyncConfigs(), MessageSyncConfigs.class, MessageSyncConfigs::new, 4, Side.CLIENT);
   }
}
