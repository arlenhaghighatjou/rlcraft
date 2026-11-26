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
      SRPMain.network.registerMessage(new SRPPacketMeleeRange.Handler(), SRPPacketMeleeRange.class, SRPPacketMeleeRange::new, nextID(), Side.SERVER);
      SRPMain.network.registerMessage(new SRPPacketEntityBodyHit.Handler(), SRPPacketEntityBodyHit.class, SRPPacketEntityBodyHit::new, nextID(), Side.SERVER);
      SRPMain.network.registerMessage(new SRPPacketBiomeChange.Handler(), SRPPacketBiomeChange.class, SRPPacketBiomeChange::new, nextID(), Side.CLIENT);
      SRPMain.network.registerMessage(new SRPPacketMovingSound.Handler(), SRPPacketMovingSound.class, SRPPacketMovingSound::new, nextID(), Side.CLIENT);
      SRPMain.network.registerMessage(new SRPPacketParticle.Handler(), SRPPacketParticle.class, SRPPacketParticle::new, nextID(), Side.CLIENT);
      SRPMain.network.registerMessage(new SRPPacketEntityBodyDead.Handler(), SRPPacketEntityBodyDead.class, SRPPacketEntityBodyDead::new, nextID(), Side.CLIENT);
      SRPMain.network.registerMessage(new SRPPacketFog.Handler(), SRPPacketFog.class, SRPPacketFog::new, nextID(), Side.CLIENT);
   }
}
