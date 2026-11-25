package svenhjol.charm.world.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import svenhjol.charm.world.feature.Spectre;
import svenhjol.meson.MesonMessage;

public class MessageSpectreDespawn extends MesonMessage {
   public BlockPos pos;

   public MessageSpectreDespawn(BlockPos pos) {
      this.pos = pos;
   }

   public MessageSpectreDespawn() {
   }

   public void toBytes(ByteBuf buf) {
      buf.writeLong(this.pos.toLong());
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = BlockPos.fromLong(buf.readLong());
   }

   public IMessage handle(MessageContext context) {
      Minecraft.getMinecraft().addScheduledTask(() -> {
         Spectre.effectDespawn(this.pos);
      });
      return null;
   }
}
