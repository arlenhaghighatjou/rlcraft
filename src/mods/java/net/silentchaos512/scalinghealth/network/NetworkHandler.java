package net.silentchaos512.scalinghealth.network;

import java.util.function.Supplier;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.silentchaos512.scalinghealth.network.message.MessageDataSync;
import net.silentchaos512.scalinghealth.network.message.MessageDebugData;
import net.silentchaos512.scalinghealth.network.message.MessageMarkBlight;
import net.silentchaos512.scalinghealth.network.message.MessagePlaySound;
import net.silentchaos512.scalinghealth.network.message.MessageWorldDataSync;

public final class NetworkHandler {
   public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("scalinghealth");
   private static int i = 0;

   private NetworkHandler() {
   }

   public static void init() {
      register(MessageDataSync.class, MessageDataSync::new, Side.CLIENT);
      register(MessageMarkBlight.class, MessageMarkBlight::new, Side.CLIENT);
      register(MessageWorldDataSync.class, MessageWorldDataSync::new, Side.CLIENT);
      register(MessagePlaySound.class, MessagePlaySound::new, Side.CLIENT);
      register(MessageDebugData.class, MessageDebugData::new, Side.CLIENT);
   }

   private static <T extends Message<T>> void register(Class<T> clazz, Supplier<T> factory, Side handlerSide) {
      INSTANCE.registerMessage(factory.get(), clazz, factory, i++, handlerSide);
   }
}
