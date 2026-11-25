package hunternif.mc.atlas.network;

import hunternif.mc.atlas.AntiqueAtlasMod;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public abstract class AbstractMessage<T extends AbstractMessage<T>> implements IMessage, IMessageHandler<T, IMessage> {
   public AbstractMessage() {
   }

   protected abstract void read(PacketBuffer var1) throws IOException;

   protected abstract void write(PacketBuffer var1) throws IOException;

   protected abstract void process(EntityPlayer var1, Side var2);

   boolean isValidOnSide(Side side) {
      return true;
   }

   boolean requiresMainThread() {
      return true;
   }

   public void fromBytes(ByteBuf buffer) {
      try {
         this.read(new PacketBuffer(buffer));
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public void toBytes(ByteBuf buffer) {
      try {
         this.write(new PacketBuffer(buffer));
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public final IMessage onMessage(T msg, MessageContext ctx) {
      if (!msg.isValidOnSide(ctx.side)) {
         throw new RuntimeException("Invalid side " + ctx.side.name() + " for " + msg.getClass().getSimpleName());
      } else {
         if (msg.requiresMainThread()) {
            checkThreadAndEnqueue(msg, ctx);
         } else {
            msg.process(AntiqueAtlasMod.proxy.getPlayerEntity(ctx), ctx.side);
         }

         return null;
      }
   }

   private static <T extends AbstractMessage<T>> void checkThreadAndEnqueue(AbstractMessage<T> msg, MessageContext ctx) {
      IThreadListener thread = AntiqueAtlasMod.proxy.getThreadFromContext(ctx);
      if (!thread.isCallingFromMinecraftThread()) {
         thread.addScheduledTask(() -> {
            msg.process(AntiqueAtlasMod.proxy.getPlayerEntity(ctx), ctx.side);
         });
      }

   }

   public abstract static class AbstractClientMessage<T extends AbstractMessage.AbstractClientMessage<T>> extends AbstractMessage<T> {
      public AbstractClientMessage() {
      }

      protected final boolean isValidOnSide(Side side) {
         return side.isClient();
      }
   }

   public abstract static class AbstractServerMessage<T extends AbstractMessage.AbstractServerMessage<T>> extends AbstractMessage<T> {
      public AbstractServerMessage() {
      }

      protected final boolean isValidOnSide(Side side) {
         return side.isServer();
      }
   }
}
