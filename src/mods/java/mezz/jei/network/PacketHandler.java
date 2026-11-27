package mezz.jei.network;

import net.minecraftforge.fml.common.network.FMLEventChannel;
import java.io.IOException;
import java.util.EnumMap;
import javax.annotation.Nullable;
import mezz.jei.network.packets.IPacketJeiHandler;
import mezz.jei.network.packets.PacketDeletePlayerItem;
import mezz.jei.network.packets.PacketGiveItemStack;
import mezz.jei.network.packets.PacketRecipeTransfer;
import mezz.jei.network.packets.PacketRequestCheatPermission;
import mezz.jei.network.packets.PacketSetHotbarItemStack;
import mezz.jei.util.Log;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;

public class PacketHandler {
   public static final String CHANNEL_ID = "jei";
   public final EnumMap<PacketIdServer, IPacketJeiHandler> serverHandlers = new EnumMap(PacketIdServer.class);

   public PacketHandler() {
      this.serverHandlers.put(PacketIdServer.RECIPE_TRANSFER, PacketRecipeTransfer::readPacketData);
      this.serverHandlers.put(PacketIdServer.DELETE_ITEM, PacketDeletePlayerItem::readPacketData);
      this.serverHandlers.put(PacketIdServer.GIVE_ITEM, PacketGiveItemStack::readPacketData);
      this.serverHandlers.put(PacketIdServer.SET_HOTBAR_ITEM, PacketSetHotbarItemStack::readPacketData);
      this.serverHandlers.put(PacketIdServer.CHEAT_PERMISSION_REQUEST, PacketRequestCheatPermission::readPacketData);
   }

   public void register(FMLEventChannel channel) {
      channel.register(ServerCustomPacketEvent.class, this::onPacket);
   }

   public void onPacket(ServerCustomPacketEvent event) {
      PacketBuffer packetBuffer = new PacketBuffer(event.getPacket().payload());
      EntityPlayerMP player = ((NetHandlerPlayServer)event.getHandler()).player;

      try {
         byte packetIdOrdinal = packetBuffer.readByte();
         PacketIdServer packetId = PacketIdServer.VALUES[packetIdOrdinal];
         IPacketJeiHandler packetHandler = (IPacketJeiHandler)this.serverHandlers.get(packetId);
         checkThreadAndEnqueue(packetHandler, packetBuffer, player, player.getServer());
      } catch (RuntimeException var7) {
         Log.get().error("Packet error", var7);
      }

   }

   private static void checkThreadAndEnqueue(IPacketJeiHandler packetHandler, PacketBuffer packetBuffer, EntityPlayer player, @Nullable IThreadListener threadListener) {
      if (threadListener != null && !threadListener.isCallingFromMinecraftThread()) {
         threadListener.addScheduledTask(() -> {
            try {
               packetHandler.readPacketData(packetBuffer, player);
            } catch (IOException var4) {
               Log.get().error("Network Error", var4);
            }

         });
      }

   }
}
