package de.mennomax.astikorcarts.handler;

import de.mennomax.astikorcarts.packets.CPacketActionKey;
import de.mennomax.astikorcarts.packets.CPacketOpenCartGui;
import de.mennomax.astikorcarts.packets.CPacketToggleSlow;
import de.mennomax.astikorcarts.packets.SPacketDrawnUpdate;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
   private static int id = 0;
   public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("astikorcarts");

   public PacketHandler() {
   }

   public static void registerPackets() {
      INSTANCE.registerMessage(new CPacketActionKey.ActionKeyPacketHandler(), CPacketActionKey.class, CPacketActionKey::new, id++, Side.SERVER);
      INSTANCE.registerMessage(new CPacketOpenCartGui.OpenCartGuiPacketHandler(), CPacketOpenCartGui.class, CPacketOpenCartGui::new, id++, Side.SERVER);
      INSTANCE.registerMessage(new CPacketToggleSlow.ToggleSlowHandler(), CPacketToggleSlow.class, CPacketToggleSlow::new, id++, Side.SERVER);
      INSTANCE.registerMessage(new SPacketDrawnUpdate.DrawnUpdatePacketHandler(), SPacketDrawnUpdate.class, SPacketDrawnUpdate::new, id++, Side.CLIENT);
   }
}
