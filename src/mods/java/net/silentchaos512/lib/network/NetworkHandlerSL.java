package net.silentchaos512.lib.network;

import java.util.function.Supplier;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandlerSL {
   public final SimpleNetworkWrapper wrapper;
   private int lastMessageId = -1;

   public NetworkHandlerSL(String modId) {
      this.wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(modId);
   }

   public <T extends MessageSL<T>> void register(Class<T> clazz, Supplier<T> factory, Side handlerSide) {
      this.wrapper.registerMessage(factory.get(), clazz, factory, ++this.lastMessageId, handlerSide);
   }
}
