package net.silentchaos512.lib.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandlerSL {
   public final SimpleNetworkWrapper wrapper;
   private int lastMessageId = -1;

   public NetworkHandlerSL(String modId) {
      this.wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(modId);
   }

   public void register(Class clazz, Side handlerSide) {
      this.wrapper.registerMessage(clazz, clazz, ++this.lastMessageId, handlerSide);
   }
}
