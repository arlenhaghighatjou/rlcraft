package reborncore.common.network.packets;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import reborncore.RebornCore;
import reborncore.client.containerBuilder.builder.BuiltContainer;
import reborncore.client.gui.builder.GuiBase;

public class VariableMessage implements IMessage {
   private VariableMessage.Type type;
   private int dimension;
   private BlockPos pos;
   private NBTTagCompound tag;

   public VariableMessage() {
   }

   public VariableMessage(VariableMessage.Type type, int dimension, BlockPos pos, NBTTagCompound tag) {
      this.type = type;
      this.dimension = dimension;
      this.pos = pos;
      this.tag = tag;
   }

   public void fromBytes(ByteBuf buf) {
      try {
         this.type = VariableMessage.Type.values()[buf.readInt()];
         this.dimension = buf.readInt();
         this.pos = BlockPos.fromLong(buf.readLong());
         short length = buf.readShort();
         if (length < 0) {
            this.tag = new NBTTagCompound();
         } else {
            byte[] bytes = new byte[length];
            buf.readBytes(bytes);
            ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
            this.tag = CompressedStreamTools.readCompressed(byteStream);
         }
      } catch (IOException var5) {
         RebornCore.logHelper.fatal(var5);
      }

   }

   public void toBytes(ByteBuf buf) {
      try {
         buf.writeInt(this.type.ordinal());
         buf.writeInt(this.dimension);
         buf.writeLong(this.pos.toLong());
         if (this.tag != null && this.tag.getSize() != 0) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            CompressedStreamTools.writeCompressed(this.tag, byteStream);
            byte[] bytes = byteStream.toByteArray();
            buf.writeShort((short)bytes.length);
            buf.writeBytes(bytes);
         } else {
            buf.writeShort(-1);
         }
      } catch (IOException var4) {
         RebornCore.logHelper.fatal(var4);
      }

   }

   public static enum Type {
      TILE,
      GUI;

      private Type() {
      }
   }

   public static class VariableMessageHandler implements IMessageHandler<VariableMessage, VariableMessage> {
      public VariableMessageHandler() {
      }

      public VariableMessage onMessage(VariableMessage message, MessageContext context) {
         if (message == null) {
            return null;
         } else if (message.type == null) {
            return null;
         } else {
            switch(message.type) {
            case GUI:
               this.handleGuiMessage(message, context);
            case TILE:
            default:
               return null;
            }
         }
      }

      public VariableMessage handleGuiMessage(VariableMessage message, MessageContext context) {
         if (message.pos != null && message.tag != null) {
            if (context.side.isServer()) {
               throw new RuntimeException("Message of Type GUI cannot be processed on the server");
            } else {
               GuiScreen gui = Minecraft.getMinecraft().currentScreen;
               if (gui instanceof GuiBase) {
                  BuiltContainer container = ((GuiBase)gui).container;
                  if (container != null) {
                     container.handleGuiMessage(message.tag);
                  }
               }

               return null;
            }
         } else {
            return null;
         }
      }
   }
}
