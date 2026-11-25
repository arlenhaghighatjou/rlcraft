package meldexun.reachfix.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import meldexun.configutil.ConfigUtil;
import meldexun.reachfix.config.ReachFixConfig;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SPacketSyncConfig implements IMessage {
   private ByteBuf buffer;

   public SPacketSyncConfig() {
   }

   public SPacketSyncConfig(ReachFixConfig config) throws ReflectiveOperationException {
      this.buffer = Unpooled.buffer();
      ConfigUtil.writeServerSettings(config, this.buffer);
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
