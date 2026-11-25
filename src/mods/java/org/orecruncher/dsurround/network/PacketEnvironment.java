package org.orecruncher.dsurround.network;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;

public class PacketEnvironment implements IMessage {
   private boolean inVillage;

   public PacketEnvironment() {
   }

   public PacketEnvironment(boolean inVillage) {
      this.inVillage = inVillage;
   }

   public void fromBytes(@Nonnull ByteBuf buf) {
      this.inVillage = buf.readBoolean();
   }

   public void toBytes(@Nonnull ByteBuf buf) {
      buf.writeBoolean(this.inVillage);
   }

   public static class PacketHandler implements IMessageHandler<PacketEnvironment, IMessage> {
      public PacketHandler() {
      }

      @Nullable
      public IMessage onMessage(@Nonnull PacketEnvironment message, @Nullable MessageContext ctx) {
         if (ctx != null) {
            ModBase.proxy().getThreadListener(ctx).addScheduledTask(() -> {
               EnvironStateHandler.EnvironState.setInVillage(message.inVillage);
            });
         }

         return null;
      }
   }
}
