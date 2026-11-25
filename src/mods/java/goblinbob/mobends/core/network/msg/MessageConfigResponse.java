package goblinbob.mobends.core.network.msg;

import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.network.NetworkConfiguration;
import goblinbob.mobends.core.network.SharedProperty;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageConfigResponse implements IMessage {
   public MessageConfigResponse() {
   }

   public void toBytes(ByteBuf buf) {
      NBTTagCompound tag = new NBTTagCompound();
      NetworkConfiguration.instance.getSharedConfig().writeToNBT(tag);
      ByteBufUtils.writeTag(buf, tag);
   }

   public void fromBytes(ByteBuf buf) {
      NBTTagCompound tag = ByteBufUtils.readTag(buf);
      if (tag == null) {
         Core.LOG.severe("An error occurred while receiving server configuration.");
      } else {
         NetworkConfiguration.instance.getSharedConfig().readFromNBT(tag);
      }
   }

   public static class Handler implements IMessageHandler<MessageConfigResponse, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(MessageConfigResponse message, MessageContext ctx) {
         StringBuilder builder = new StringBuilder("Received Mo' Bends server configuration.\n");

         for(SharedProperty<?> property : NetworkConfiguration.instance.getSharedConfig().getProperties()) {
            builder.append(String.format(" - %s: %b\n", property.getKey(), property.getValue()));
         }

         Core.LOG.info(builder.toString());
         return null;
      }
   }
}
