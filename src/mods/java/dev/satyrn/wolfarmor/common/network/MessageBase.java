package dev.satyrn.wolfarmor.common.network;

import dev.satyrn.wolfarmor.WolfArmorMod;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public abstract class MessageBase<T extends MessageBase<T>> implements IMessage, IMessageHandler<T, IMessage> {
   public MessageBase() {
   }

   protected abstract void read(PacketBuffer var1) throws IOException;

   protected abstract void write(PacketBuffer var1) throws IOException;

   @Nullable
   protected abstract IMessage process(EntityPlayer var1, Side var2);

   protected boolean isSideValid(Side side) {
      return true;
   }

   /** @deprecated */
   @Deprecated
   protected boolean isMainThreadRequired() {
      return this.isScheduled();
   }

   protected boolean isScheduled() {
      return true;
   }

   public final IMessage onMessage(T message, MessageContext context) {
      if (!this.isSideValid(context.side)) {
         throw new RuntimeException(String.format("The side %s is not valid for packet %s", context.side.name(), message.getClass().getSimpleName()));
      } else if (this.isScheduled()) {
         this.enqueue(message, context);
         return null;
      } else {
         return message.process(WolfArmorMod.getProxy().getPlayerFromContext(context), context.side);
      }
   }

   private void enqueue(final T message, final MessageContext context) {
      IThreadListener thread = WolfArmorMod.getProxy().getThreadFromContext(context);
      thread.addScheduledTask(new Runnable() {
         public void run() {
            message.process(WolfArmorMod.getProxy().getPlayerFromContext(context), context.side);
         }
      });
   }

   public final void fromBytes(ByteBuf buf) {
      try {
         this.read(new PacketBuffer(buf));
      } catch (IOException var3) {
         throw new RuntimeException(String.format("The packet %s failed to deserialize.", this.getClass().getSimpleName()), var3);
      }
   }

   public final void toBytes(ByteBuf buf) {
      try {
         this.write(new PacketBuffer(buf));
      } catch (IOException var3) {
         throw new RuntimeException(String.format("The packet %s failed to serialize.", this.getClass().getSimpleName()), var3);
      }
   }

   public abstract static class ClientMessageBase<T extends MessageBase.ClientMessageBase<T>> extends MessageBase<T> {
      public ClientMessageBase() {
      }

      protected final boolean isSideValid(Side side) {
         return side.isClient();
      }
   }

   public abstract static class ServerMessageBase<T extends MessageBase.ServerMessageBase<T>> extends MessageBase<T> {
      public ServerMessageBase() {
      }

      protected final boolean isSideValid(Side side) {
         return side.isServer();
      }
   }
}
