package com.charles445.rltweaker.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
   public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel("rltweaker");

   public PacketHandler() {
   }

   public static void init() {
      instance.registerMessage(MessageUpdateEntityMovement.Handler.class, MessageUpdateEntityMovement.class, 0, Side.CLIENT);
      instance.registerMessage(MessageUpdateDismountStatus.Handler.class, MessageUpdateDismountStatus.class, 1, Side.CLIENT);
      instance.registerMessage(MessageUpdateAttackYaw.Handler.class, MessageUpdateAttackYaw.class, 2, Side.CLIENT);
      instance.registerMessage(MessageSendVersion.Handler.class, MessageSendVersion.class, 3, Side.SERVER);
      instance.registerMessage(MessageReskillableLockSkill.Handler.class, MessageReskillableLockSkill.class, 4, Side.SERVER);
   }
}
