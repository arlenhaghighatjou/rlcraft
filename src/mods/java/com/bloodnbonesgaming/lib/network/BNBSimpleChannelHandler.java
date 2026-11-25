package com.bloodnbonesgaming.lib.network;

import com.bloodnbonesgaming.lib.BNBGamingLib;
import com.google.common.collect.Sets;
import gnu.trove.map.TObjectByteMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import java.util.Set;
import net.minecraft.network.INetHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

@Sharable
public class BNBSimpleChannelHandler extends SimpleChannelInboundHandler<ConsumableMessage> {
   private final Side side;

   public BNBSimpleChannelHandler(Side side) {
      this.side = side;
   }

   protected void channelRead0(ChannelHandlerContext ctx, ConsumableMessage message) throws Exception {
      if (!message.isConsumed() && !NetworkManager.INSTANCE.consumers.isEmpty()) {
         TObjectByteMap<Class<? extends ConsumableMessage>> map = (TObjectByteMap)NetworkManager.INSTANCE.discriminators.get(this.side);
         if (!map.containsKey(message.getClass())) {
            BNBGamingLib.libInstance.getLog().warn("We received a message with no associated discriminator! Something went wrong during registration!");
         } else {
            BNBMessageContext context = new BNBMessageContext((INetHandler)ctx.channel().attr(NetworkRegistry.NET_HANDLER).get(), this.side);
            byte disc = map.get(message.getClass());
            Set<IMessage> returns = Sets.newHashSet();

            for(MessageConsumer consumer : (Set)NetworkManager.INSTANCE.consumers.get(this.side)) {
               returns.add(consumer.consume(message, disc, context));
               if (message.isConsumed()) {
                  break;
               }
            }

            if (this.side == Side.CLIENT) {
               for(IMessage mess : returns) {
                  if (mess != null) {
                     NetworkManager.INSTANCE.sendToServer(mess);
                  }
               }
            } else {
               for(IMessage mess : returns) {
                  if (mess != null) {
                     NetworkManager.INSTANCE.sendToClient(mess, context.getServerHandler().player);
                  }
               }
            }

         }
      }
   }
}
