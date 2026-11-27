package com.Shultrea.Rin.Utility_Sector;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class SMEnetwork {
   public static SimpleNetworkWrapper net;
   public static int id;

   public SMEnetwork() {
   }

   public static void mainRegistry() {
      id = 0;
      registerNetwork();
   }

   private static void registerNetwork() {
      net = NetworkRegistry.INSTANCE.newSimpleChannel("sme.net");
      net.registerMessage(new MsgSP_Particle.Handler(), MsgSP_Particle.class, MsgSP_Particle::new, id++, Side.CLIENT);
   }
}
