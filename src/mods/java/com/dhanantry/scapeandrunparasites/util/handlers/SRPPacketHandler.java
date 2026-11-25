package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.network.SRPPacketBiomeChange;
import com.dhanantry.scapeandrunparasites.network.SRPPacketEntityBodyDead;
import com.dhanantry.scapeandrunparasites.network.SRPPacketEntityBodyHit;
import com.dhanantry.scapeandrunparasites.network.SRPPacketFog;
import com.dhanantry.scapeandrunparasites.network.SRPPacketMeleeRange;
import com.dhanantry.scapeandrunparasites.network.SRPPacketMovingSound;
import com.dhanantry.scapeandrunparasites.network.SRPPacketParticle;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class SRPPacketHandler {
   private static int packetId = 0;

   public SRPPacketHandler() {
   }

   public static int nextID() {
      return packetId++;
   }

   public static void init() {
      SRPMain.network = NetworkRegistry.INSTANCE.newSimpleChannel("SRParasites");
      SRPMain.network.registerMessage(SRPPacketMeleeRange.Handler.class, SRPPacketMeleeRange.class, nextID(), Side.SERVER);
      SRPMain.network.registerMessage(SRPPacketEntityBodyHit.Handler.class, SRPPacketEntityBodyHit.class, nextID(), Side.SERVER);
      SRPMain.network.registerMessage(SRPPacketBiomeChange.Handler.class, SRPPacketBiomeChange.class, nextID(), Side.CLIENT);
      SRPMain.network.registerMessage(SRPPacketMovingSound.Handler.class, SRPPacketMovingSound.class, nextID(), Side.CLIENT);
      SRPMain.network.registerMessage(SRPPacketParticle.Handler.class, SRPPacketParticle.class, nextID(), Side.CLIENT);
      SRPMain.network.registerMessage(SRPPacketEntityBodyDead.Handler.class, SRPPacketEntityBodyDead.class, nextID(), Side.CLIENT);
      SRPMain.network.registerMessage(SRPPacketFog.Handler.class, SRPPacketFog.class, nextID(), Side.CLIENT);
   }
}
