package mezz.jei.network;

import net.minecraftforge.fml.common.network.FMLEventChannel;
import java.io.IOException;
import java.util.EnumMap;
import javax.annotation.Nullable;
import mezz.jei.network.packets.IPacketJeiHandler;
import mezz.jei.network.packets.PacketCheatPermission;
import mezz.jei.util.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PacketHandlerClient extends PacketHandler {
   public final EnumMap<PacketIdClient, IPacketJeiHandler> clientHandlers = new EnumMap(PacketIdClient.class);

   public PacketHandlerClient() {
      this.clientHandlers.put(PacketIdClient.CHEAT_PERMISSION, PacketCheatPermission::readPacketData);
   }

   public void register(FMLEventChannel channel) {
      super.register(channel);
      channel.register(ClientCustomPacketEvent.class, this::onPacket);
   }

   @SideOnly(Side.CLIENT)
   public void onPacket(ClientCustomPacketEvent event) {
      PacketBuffer packetBuffer = new PacketBuffer(event.getPacket().payload());
      Minecraft minecraft = Minecraft.getMinecraft();

      try {
         byte packetIdOrdinal = packetBuffer.readByte();
         PacketIdClient packetId = PacketIdClient.VALUES[packetIdOrdinal];
         IPacketJeiHandler packetHandler = (IPacketJeiHandler)this.clientHandlers.get(packetId);
         checkThreadAndEnqueue(packetHandler, packetBuffer, minecraft);
      } catch (Exception var7) {
         Log.get().error("Packet error", var7);
      }

   }

   private static void checkThreadAndEnqueue(IPacketJeiHandler packetHandler, PacketBuffer packetBuffer, @Nullable IThreadListener threadListener) {
      if (threadListener != null && !threadListener.isCallingFromMinecraftThread()) {
         threadListener.addScheduledTask(() -> {
            try {
               Minecraft minecraft = Minecraft.getMinecraft();
               EntityPlayer player = minecraft.player;
               if (player != null) {
                  packetHandler.readPacketData(packetBuffer, player);
               }

            } catch (IOException var4) {
               Log.get().error("Network Error", var4);
            }

         });
      }

   }
}
