package invtweaks.network.packets;

import invtweaks.forge.InvTweaksMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;
import org.jetbrains.annotations.NotNull;

public class ITPacketLogin implements ITPacket {
   public byte protocolVersion = 2;

   public ITPacketLogin() {
   }

   public void readBytes(@NotNull ByteBuf bytes) {
      this.protocolVersion = bytes.readByte();
   }

   public void writeBytes(@NotNull ByteBuf bytes) {
      bytes.writeByte(this.protocolVersion);
   }

   public void handle(INetHandler handler) {
      if (this.protocolVersion == 2) {
         InvTweaksMod.proxy.setServerHasInvTweaks(true);
      }

   }
}
