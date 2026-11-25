package svenhjol.charm.brewing.message;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import svenhjol.meson.MesonMessage;
import svenhjol.meson.handler.ClientHandler;

public class MessageSetStructure extends MesonMessage {
   private BlockPos pos;
   private String name;

   public MessageSetStructure(String name, BlockPos pos) {
      this.pos = pos;
      this.name = name;
   }

   public MessageSetStructure() {
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = BlockPos.fromLong(buf.readLong());
      this.name = ByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeLong(this.pos.toLong());
      ByteBufUtils.writeUTF8String(buf, this.name);
   }

   public IMessage handle(MessageContext context) {
      Minecraft.getMinecraft().addScheduledTask(() -> {
         ClientHandler.setNearestStructure(this.name, this.pos);
      });
      return null;
   }
}
