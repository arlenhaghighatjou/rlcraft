package svenhjol.charm.enchanting.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import svenhjol.charm.enchanting.feature.Magnetic;
import svenhjol.meson.MesonMessage;

public class MessageMagneticPickup extends MesonMessage {
   public BlockPos pos;

   public MessageMagneticPickup(BlockPos pos) {
      this.pos = pos;
   }

   public MessageMagneticPickup() {
   }

   public void toBytes(ByteBuf buf) {
      buf.writeLong(this.pos.toLong());
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = BlockPos.fromLong(buf.readLong());
   }

   public IMessage handle(MessageContext context) {
      Minecraft.getMinecraft().addScheduledTask(() -> {
         Magnetic.effectPickup(this.pos);
      });
      return null;
   }
}
