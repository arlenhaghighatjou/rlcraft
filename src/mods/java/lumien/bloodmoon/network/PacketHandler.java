package lumien.bloodmoon.network;

import lumien.bloodmoon.network.handler.HandleBloodmoonStatus;
import lumien.bloodmoon.network.messages.MessageBloodmoonStatus;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
   public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper("bloodmoon");

   public PacketHandler() {
   }

   public static void init() {
      INSTANCE.registerMessage(new HandleBloodmoonStatus(), MessageBloodmoonStatus.class, MessageBloodmoonStatus::new, 0, Side.CLIENT);
   }
}
