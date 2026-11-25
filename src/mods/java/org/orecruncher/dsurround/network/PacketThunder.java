package org.orecruncher.dsurround.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.orecruncher.dsurround.event.ThunderEvent;

public class PacketThunder implements IMessage {
   private int dimension;
   private boolean doFlash;
   private BlockPos pos;

   public PacketThunder() {
   }

   public PacketThunder(int dimensionId, boolean doFlash, @Nonnull BlockPos pos) {
      this.dimension = dimensionId;
      this.doFlash = doFlash;
      this.pos = pos.toImmutable();
   }

   public void fromBytes(@Nonnull ByteBuf buf) {
      this.dimension = buf.readInt();
      this.doFlash = buf.readBoolean();
      this.pos = BlockPos.fromLong(buf.readLong());
   }

   public void toBytes(@Nonnull ByteBuf buf) {
      buf.writeInt(this.dimension);
      buf.writeBoolean(this.doFlash);
      buf.writeLong(this.pos.toLong());
   }

   public static class PacketHandler implements IMessageHandler<PacketThunder, IMessage> {
      public PacketHandler() {
      }

      @Nullable
      public IMessage onMessage(@Nonnull PacketThunder message, @Nullable MessageContext ctx) {
         if (ctx != null) {
            Network.postEvent(new ThunderEvent(message.dimension, message.doFlash, message.pos));
         }

         return null;
      }
   }
}
