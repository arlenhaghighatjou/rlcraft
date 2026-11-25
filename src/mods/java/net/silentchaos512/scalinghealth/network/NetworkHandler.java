package net.silentchaos512.scalinghealth.network;

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
      register(MessageDataSync.class, Side.CLIENT);
      register(MessageMarkBlight.class, Side.CLIENT);
      register(MessageWorldDataSync.class, Side.CLIENT);
      register(MessagePlaySound.class, Side.CLIENT);
      register(MessageDebugData.class, Side.CLIENT);
   }

   private static void register(Class clazz, Side handlerSide) {
      INSTANCE.registerMessage(clazz, clazz, i++, handlerSide);
   }
}
