package reborncore.common.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.io.serialization.ValidatingObjectInputStream;
import reborncore.RebornCore;

public class ExtendedPacketBuffer extends PacketBuffer {
   public ExtendedPacketBuffer(ByteBuf wrapped) {
      super(wrapped);
   }

   public void writeObject(Object object) {
      ObjectBufferUtils.writeObject(object, this);
   }

   public Object readObject() {
      return ObjectBufferUtils.readObject(this);
   }

   /** @deprecated */
   @Deprecated
   public void writeBigInt(BigInteger bigInteger) {
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         Throwable var3 = null;

         try {
            ObjectOutputStream outputStream = new ObjectOutputStream(baos);
            outputStream.writeObject(bigInteger);
            this.writeByteArray(baos.toByteArray());
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (baos != null) {
               if (var3 != null) {
                  try {
                     baos.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  baos.close();
               }
            }

         }
      } catch (IOException var15) {
         RebornCore.logHelper.error(var15);
      }

   }

   /** @deprecated */
   @Deprecated
   public BigInteger readBigInt() {
      try {
         ValidatingObjectInputStream inputStream = new ValidatingObjectInputStream(new ByteArrayInputStream(this.readByteArray()));
         Throwable var2 = null;

         BigInteger var3;
         try {
            inputStream.accept(new Class[]{BigInteger.class});
            var3 = (BigInteger)inputStream.readObject();
         } catch (Throwable var13) {
            var2 = var13;
            throw var13;
         } finally {
            if (inputStream != null) {
               if (var2 != null) {
                  try {
                     inputStream.close();
                  } catch (Throwable var12) {
                     var2.addSuppressed(var12);
                  }
               } else {
                  inputStream.close();
               }
            }

         }

         return var3;
      } catch (ClassNotFoundException | IOException var15) {
         RebornCore.logHelper.error(var15);
         return BigInteger.ZERO;
      }
   }

   public void writeFluidStack(FluidStack fluidStack) {
      try {
         if (fluidStack != null && FluidRegistry.getFluidName(fluidStack) != null) {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            CompressedStreamTools.writeCompressed(fluidStack.writeToNBT(new NBTTagCompound()), byteStream);
            byte[] bytes = byteStream.toByteArray();
            this.writeShort((short)bytes.length);
            this.writeByteArray(bytes);
         } else {
            this.writeShort(-1);
         }
      } catch (IOException var4) {
         RebornCore.logHelper.fatal(var4);
      }

   }

   public FluidStack readFluidStack() {
      try {
         short length = this.readShort();
         if (length < 0) {
            return null;
         } else {
            byte[] bytes = this.readByteArray(length);
            ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
            return FluidStack.loadFluidStackFromNBT(CompressedStreamTools.readCompressed(byteStream));
         }
      } catch (IOException var4) {
         RebornCore.logHelper.fatal(var4);
         return null;
      }
   }
}
