package baubles.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
   public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("baubles".toLowerCase());

   public PacketHandler() {
   }

   public static void init() {
      INSTANCE.registerMessage(new PacketOpenBaublesInventory(), PacketOpenBaublesInventory.class, PacketOpenBaublesInventory::new, 0, Side.SERVER);
      INSTANCE.registerMessage(new PacketOpenNormalInventory(), PacketOpenNormalInventory.class, PacketOpenNormalInventory::new, 1, Side.SERVER);
      INSTANCE.registerMessage(new PacketSync.Handler(), PacketSync.class, PacketSync::new, 2, Side.CLIENT);
   }
}
