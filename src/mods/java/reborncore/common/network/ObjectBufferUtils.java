package reborncore.common.network;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;

public enum ObjectBufferUtils {
   STRING(String.class, (string, buffer) -> {
      buffer.writeInt(string.length());
      buffer.writeString(string);
   }, (buffer) -> {
      return buffer.readString(buffer.readInt());
   }),
   INT(Integer.class, (value, buffer) -> {
      buffer.writeInt(value.intValue());
   }, PacketBuffer::readInt),
   LONG(Long.class, (pos, buffer) -> {
      buffer.writeLong(pos.longValue());
   }, PacketBuffer::readLong),
   DOUBLE(Double.class, (pos, buffer) -> {
      buffer.writeDouble(pos.doubleValue());
   }, PacketBuffer::readDouble),
   FLOAT(Float.class, (pos, buffer) -> {
      buffer.writeFloat(pos.floatValue());
   }, PacketBuffer::readFloat),
   BLOCK_POS(BlockPos.class, (pos, buffer) -> {
      buffer.writeBlockPos(pos);
   }, PacketBuffer::readBlockPos),
   BIG_INT(BigInteger.class, (pos, buffer) -> {
      buffer.writeBigInt(pos);
   }, ExtendedPacketBuffer::readBigInt),
   FLUID_STACK(FluidStack.class, (pos, buffer) -> {
      buffer.writeFluidStack(pos);
   }, ExtendedPacketBuffer::readFluidStack);

   Class clazz;
   ObjectBufferUtils.ObjectWriter writer;
   ObjectBufferUtils.ObjectReader reader;

   private <T> ObjectBufferUtils(Class<T> clazz, ObjectBufferUtils.ObjectWriter<T> writer, ObjectBufferUtils.ObjectReader<T> reader) {
      this.clazz = clazz;
      this.writer = writer;
      this.reader = reader;
   }

   public static void writeObject(Object object, ExtendedPacketBuffer buffer) {
      ObjectBufferUtils utils = (ObjectBufferUtils)Arrays.stream(values()).filter((objectBufferUtils) -> {
         return objectBufferUtils.clazz == object.getClass();
      }).findFirst().orElse((Object)null);
      Objects.requireNonNull(utils, "No support found for " + object.getClass());
      buffer.writeInt(utils.ordinal());
      utils.writer.write(object, buffer);
   }

   public static Object readObject(ExtendedPacketBuffer buffer) {
      ObjectBufferUtils utils = values()[buffer.readInt()];
      Objects.requireNonNull(utils, "Could not find reader");
      return utils.reader.read(buffer);
   }

   private interface ObjectReader<T> {
      T read(ExtendedPacketBuffer var1);
   }

   private interface ObjectWriter<T> {
      void write(T var1, ExtendedPacketBuffer var2);
   }
}
