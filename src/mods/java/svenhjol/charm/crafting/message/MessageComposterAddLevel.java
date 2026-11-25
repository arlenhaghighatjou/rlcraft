package svenhjol.charm.crafting.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import svenhjol.charm.crafting.feature.Composter;
import svenhjol.meson.MesonMessage;

public class MessageComposterAddLevel extends MesonMessage {
   public BlockPos pos;
   public int level;

   public MessageComposterAddLevel(BlockPos pos, int level) {
      this.pos = pos;
      this.level = level;
   }

   public MessageComposterAddLevel() {
   }

   public void toBytes(ByteBuf buf) {
      buf.writeLong(this.pos.toLong());
      buf.writeInt(this.level);
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = BlockPos.fromLong(buf.readLong());
      this.level = buf.readInt();
   }

   public IMessage handle(MessageContext context) {
      Minecraft.getMinecraft().addScheduledTask(() -> {
         Composter.levelAdded(this.pos, this.level);
      });
      return null;
   }
}
