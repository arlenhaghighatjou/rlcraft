package svenhjol.charm.brewing.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import svenhjol.charm.brewing.feature.FlavoredCake;
import svenhjol.meson.MesonMessage;

public class MessageCakeImbue extends MesonMessage {
   public BlockPos pos;

   public MessageCakeImbue(BlockPos pos) {
      this.pos = pos;
   }

   public MessageCakeImbue() {
   }

   public void toBytes(ByteBuf buf) {
      buf.writeLong(this.pos.toLong());
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = BlockPos.fromLong(buf.readLong());
   }

   public IMessage handle(MessageContext context) {
      Minecraft.getMinecraft().addScheduledTask(() -> {
         FlavoredCake.cakeImbued(this.pos);
      });
      return null;
   }
}
