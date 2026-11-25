package invtweaks.network.packets;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;

public class ITPacketSortComplete implements ITPacket {
   public ITPacketSortComplete() {
   }

   public void readBytes(ByteBuf bytes) {
   }

   public void writeBytes(ByteBuf bytes) {
   }

   public void handle(INetHandler handler) {
      if (handler instanceof NetHandlerPlayServer) {
         NetHandlerPlayServer serverHandler = (NetHandlerPlayServer)handler;
         EntityPlayerMP player = serverHandler.player;
         player.sendContainerToPlayer(player.openContainer);
      }

   }
}
