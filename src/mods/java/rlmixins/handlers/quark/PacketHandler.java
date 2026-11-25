package rlmixins.handlers.quark;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
   public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel("rlmixins");

   public PacketHandler() {
   }

   public static void init() {
      instance.registerMessage(RightClickSignEditHandler.MessageSyncConfig.Handler.class, RightClickSignEditHandler.MessageSyncConfig.class, 0, Side.CLIENT);
   }
}
