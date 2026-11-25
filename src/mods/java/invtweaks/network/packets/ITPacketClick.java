package invtweaks.network.packets;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import org.jetbrains.annotations.NotNull;

public class ITPacketClick implements ITPacket {
   public int slot;
   public int data;
   public ClickType action;
   public int window;

   public ITPacketClick() {
   }

   public ITPacketClick(int _slot, int _data, ClickType _action, int _window) {
      this.slot = _slot;
      this.data = _data;
      this.action = _action;
      this.window = _window;
   }

   public void readBytes(@NotNull ByteBuf bytes) {
      this.slot = bytes.readInt();
      this.data = bytes.readInt();
      this.action = ClickType.values()[bytes.readInt()];
      this.window = bytes.readByte();
   }

   public void writeBytes(@NotNull ByteBuf bytes) {
      bytes.writeInt(this.slot);
      bytes.writeInt(this.data);
      bytes.writeInt(this.action.ordinal());
      bytes.writeByte(this.window);
   }

   public void handle(INetHandler handler) {
      if (handler instanceof NetHandlerPlayServer) {
         NetHandlerPlayServer serverHandler = (NetHandlerPlayServer)handler;
         EntityPlayerMP player = serverHandler.player;
         if (!player.isSpectator() && player.openContainer.windowId == this.window) {
            player.openContainer.slotClick(this.slot, this.data, this.action, player);
         }
      }

   }
}
