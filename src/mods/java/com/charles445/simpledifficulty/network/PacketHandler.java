package com.charles445.simpledifficulty.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
   public static final SimpleNetworkWrapper instance = NetworkRegistry.INSTANCE.newSimpleChannel("simpledifficulty");

   public PacketHandler() {
   }

   public static void init() {
      instance.registerMessage(new MessageUpdateThirst.Handler(), MessageUpdateThirst.class, MessageUpdateThirst::new, 0, Side.CLIENT);
      instance.registerMessage(new MessageDrinkWater.Handler(), MessageDrinkWater.class, MessageDrinkWater::new, 1, Side.SERVER);
      instance.registerMessage(new MessageConfigLAN.Handler(), MessageConfigLAN.class, MessageConfigLAN::new, 2, Side.SERVER);
      instance.registerMessage(new MessageUpdateConfig.Handler(), MessageUpdateConfig.class, MessageUpdateConfig::new, 3, Side.CLIENT);
      instance.registerMessage(new MessageUpdateTemperature.Handler(), MessageUpdateTemperature.class, MessageUpdateTemperature::new, 4, Side.CLIENT);
   }
}
