package meldexun.reachfix.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.lax1dude.eaglercraft.v1_8.netty.Unpooled;
import meldexun.reachfix.config.ReachFixConfig;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SPacketSyncConfig implements IMessage {
   private ByteBuf buffer;

   public SPacketSyncConfig() {
   }

   public SPacketSyncConfig(ReachFixConfig config) {
      this.buffer = Unpooled.buffer();
      config.writeServerSettings(this.buffer);
   }

   public void fromBytes(ByteBuf buf) {
      this.buffer = buf.readBytes(buf.readableBytes());
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBytes(this.buffer);
   }

   public ByteBuf getBuffer() {
      return this.buffer;
   }
}
