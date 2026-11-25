package lumberwizard.anvilpatch.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModNetworkHandler {
   public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("anvilpatch");

   public ModNetworkHandler() {
   }

   public static void registerPacketHandlers() {
      INSTANCE.registerMessage(PacketConfigSync.Handler.class, PacketConfigSync.class, 0, Side.CLIENT);
   }
}
