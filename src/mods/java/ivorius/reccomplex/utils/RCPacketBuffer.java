package ivorius.reccomplex.utils;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBufInputStream;
import io.netty.handler.codec.EncoderException;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class RCPacketBuffer extends PacketBuffer {
   public RCPacketBuffer(ByteBuf wrapped) {
      super(wrapped);
   }

   @Nullable
   public NBTTagCompound readBigTag() throws IOException {
      int i = this.readerIndex();
      byte b0 = this.readByte();
      if (b0 == 0) {
         return null;
      } else {
         this.readerIndex(i);

         try {
            return CompressedStreamTools.read(new ByteBufInputStream(this), new NBTSizeTracker(8388608L));
         } catch (IOException var4) {
            throw new EncoderException(var4);
         }
      }
   }
}
