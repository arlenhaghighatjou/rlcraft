package mcjty.lostcities.network;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import mcjty.lostcities.LostCities;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Level;

public class NetworkTools {
   public NetworkTools() {
   }

   public static FluidStack readFluidStack(ByteBuf dataIn) {
      PacketBuffer buf = new PacketBuffer(dataIn);

      try {
         NBTTagCompound nbt = buf.readCompoundTag();
         return FluidStack.loadFluidStackFromNBT(nbt);
      } catch (IOException var3) {
         LostCities.setup.getLogger().log(Level.ERROR, "Error reading fluid stack", var3);
         return null;
      }
   }

   public static void writeFluidStack(ByteBuf dataOut, FluidStack fluidStack) {
      PacketBuffer buf = new PacketBuffer(dataOut);
      NBTTagCompound nbt = new NBTTagCompound();
      fluidStack.writeToNBT(nbt);

      try {
         buf.writeCompoundTag(nbt);
      } catch (Exception var5) {
         LostCities.setup.getLogger().log(Level.ERROR, "Error writing fluid stack", var5);
      }

   }

   public static NBTTagCompound readTag(ByteBuf dataIn) {
      PacketBuffer buf = new PacketBuffer(dataIn);

      try {
         return buf.readCompoundTag();
      } catch (IOException var3) {
         LostCities.setup.getLogger().log(Level.ERROR, "Error reading tag", var3);
         return null;
      }
   }

   public static void writeTag(ByteBuf dataOut, NBTTagCompound tag) {
      PacketBuffer buf = new PacketBuffer(dataOut);

      try {
         buf.writeCompoundTag(tag);
      } catch (Exception var4) {
         LostCities.setup.getLogger().log(Level.ERROR, "Error writing tag", var4);
      }

   }

   public static ItemStack readItemStack(ByteBuf dataIn) {
      PacketBuffer buf = new PacketBuffer(dataIn);

      try {
         NBTTagCompound nbt = buf.readCompoundTag();
         ItemStack stack = new ItemStack(nbt);
         stack.setCount(buf.readInt());
         return stack;
      } catch (IOException var4) {
         LostCities.setup.getLogger().log(Level.ERROR, "Error reading item stack", var4);
         return ItemStack.EMPTY;
      }
   }

   public static void writeItemStack(ByteBuf dataOut, ItemStack itemStack) {
      PacketBuffer buf = new PacketBuffer(dataOut);
      NBTTagCompound nbt = new NBTTagCompound();
      itemStack.writeToNBT(nbt);

      try {
         buf.writeCompoundTag(nbt);
         buf.writeInt(itemStack.getCount());
      } catch (Exception var5) {
         LostCities.setup.getLogger().log(Level.ERROR, "Error writing item stack", var5);
      }

   }

   public static String readString(ByteBuf dataIn) {
      int s = dataIn.readInt();
      if (s == -1) {
         return null;
      } else if (s == 0) {
         return "";
      } else {
         byte[] dst = new byte[s];
         dataIn.readBytes(dst);
         return new String(dst);
      }
   }

   public static void writeString(ByteBuf dataOut, String str) {
      if (str == null) {
         dataOut.writeInt(-1);
      } else {
         byte[] bytes = str.getBytes();
         dataOut.writeInt(bytes.length);
         if (bytes.length > 0) {
            dataOut.writeBytes(bytes);
         }

      }
   }

   public static String readStringUTF8(ByteBuf dataIn) {
      int s = dataIn.readInt();
      if (s == -1) {
         return null;
      } else if (s == 0) {
         return "";
      } else {
         byte[] dst = new byte[s];
         dataIn.readBytes(dst);
         return new String(dst, StandardCharsets.UTF_8);
      }
   }

   public static void writeStringUTF8(ByteBuf dataOut, String str) {
      if (str == null) {
         dataOut.writeInt(-1);
      } else {
         byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
         dataOut.writeInt(bytes.length);
         if (bytes.length > 0) {
            dataOut.writeBytes(bytes);
         }

      }
   }

   public static BlockPos readPos(ByteBuf dataIn) {
      return new BlockPos(dataIn.readInt(), dataIn.readInt(), dataIn.readInt());
   }

   public static void writePos(ByteBuf dataOut, BlockPos pos) {
      dataOut.writeInt(pos.getX());
      dataOut.writeInt(pos.getY());
      dataOut.writeInt(pos.getZ());
   }

   public static <T extends Enum<T>> void writeEnum(ByteBuf buf, T value, T nullValue) {
      if (value == null) {
         buf.writeInt(nullValue.ordinal());
      } else {
         buf.writeInt(value.ordinal());
      }

   }

   public static <T extends Enum<T>> T readEnum(ByteBuf buf, T[] values) {
      return (T)values[buf.readInt()];
   }

   public static <T extends Enum<T>> void writeEnumCollection(ByteBuf buf, Collection<T> collection) {
      buf.writeInt(collection.size());

      for(T type : collection) {
         buf.writeInt(type.ordinal());
      }

   }

   public static <T extends Enum<T>> void readEnumCollection(ByteBuf buf, Collection<T> collection, T[] values) {
      collection.clear();
      int size = buf.readInt();

      for(int i = 0; i < size; ++i) {
         collection.add(values[buf.readInt()]);
      }

   }

   public static void writeFloat(ByteBuf buf, Float f) {
      if (f != null) {
         buf.writeBoolean(true);
         buf.writeFloat(f.floatValue());
      } else {
         buf.writeBoolean(false);
      }

   }

   public static Float readFloat(ByteBuf buf) {
      return buf.readBoolean() ? buf.readFloat() : null;
   }
}
