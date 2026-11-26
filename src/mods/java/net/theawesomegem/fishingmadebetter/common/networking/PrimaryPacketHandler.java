package net.theawesomegem.fishingmadebetter.common.networking;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.theawesomegem.fishingmadebetter.common.networking.packet.PacketFishingHandshakeS;
import net.theawesomegem.fishingmadebetter.common.networking.packet.PacketKeybindS;
import net.theawesomegem.fishingmadebetter.common.networking.packet.PacketReelingC;

public class PrimaryPacketHandler {
   public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("fishingmadebetter");

   public PrimaryPacketHandler() {
   }

   public static void registerPackets() {
      INSTANCE.registerMessage(new PacketKeybindS.KeybindMessageHandler(), PacketKeybindS.class, PacketKeybindS::new, 1, Side.SERVER);
      INSTANCE.registerMessage(new PacketReelingC.ReelingMessageHandler(), PacketReelingC.class, PacketReelingC::new, 2, Side.CLIENT);
      INSTANCE.registerMessage(new PacketFishingHandshakeS.HandshakeMessageHandler(), PacketFishingHandshakeS.class, PacketFishingHandshakeS::new, 3, Side.SERVER);
   }
}
