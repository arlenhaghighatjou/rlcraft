package net.silentchaos512.scalinghealth.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.lang3.tuple.Pair;

public class Message<REQ extends Message> implements Serializable, IMessage, IMessageHandler<REQ, IMessage> {
   private static final HashMap<Class, Pair<Message.Reader, Message.Writer>> handlers = new HashMap();
   private static final HashMap<Class, Field[]> fieldCache = new HashMap();

   public Message() {
   }

   public IMessage handleMessage(MessageContext context) {
      return null;
   }

   public final IMessage onMessage(REQ message, MessageContext context) {
      return message.handleMessage(context);
   }

   public void fromBytes(ByteBuf buf) {
      try {
         Class<?> clazz = this.getClass();
         Field[] clFields = getClassFields(clazz);

         for(Field f : clFields) {
            Class<?> type = f.getType();
            if (acceptField(f, type)) {
               this.readField(f, type, buf);
            }
         }

      } catch (Exception var9) {
         throw new RuntimeException("Error at reading packet " + this, var9);
      }
   }

   public final void toBytes(ByteBuf buf) {
      try {
         Class<?> clazz = this.getClass();
         Field[] clFields = getClassFields(clazz);

         for(Field f : clFields) {
            Class<?> type = f.getType();
            if (acceptField(f, type)) {
               this.writeField(f, type, buf);
            }
         }

      } catch (Exception var9) {
         throw new RuntimeException("Error at writing packet " + this, var9);
      }
   }

   private static Field[] getClassFields(Class<?> clazz) {
      if (fieldCache.containsValue(clazz)) {
         return (Field[])fieldCache.get(clazz);
      } else {
         Field[] fields = clazz.getFields();
         Arrays.sort(fields, (f1, f2) -> {
            return f1.getName().compareTo(f2.getName());
         });
         fieldCache.put(clazz, fields);
         return fields;
      }
   }

   private final void writeField(Field f, Class clazz, ByteBuf buf) throws IllegalArgumentException, IllegalAccessException {
      Pair<Message.Reader, Message.Writer> handler = getHandler(clazz);
      ((Message.Writer)handler.getRight()).write(f.get(this), buf);
   }

   private final void readField(Field f, Class clazz, ByteBuf buf) throws IllegalArgumentException, IllegalAccessException {
      Pair<Message.Reader, Message.Writer> handler = getHandler(clazz);
      f.set(this, ((Message.Reader)handler.getLeft()).read(buf));
   }

   private static Pair<Message.Reader, Message.Writer> getHandler(Class<?> clazz) {
      Pair<Message.Reader, Message.Writer> pair = (Pair)handlers.get(clazz);
      if (pair == null) {
         throw new RuntimeException("No R/W handler for  " + clazz);
      } else {
         return pair;
      }
   }

   private static boolean acceptField(Field f, Class<?> type) {
      int mods = f.getModifiers();
      return !Modifier.isFinal(mods) && !Modifier.isStatic(mods) && !Modifier.isTransient(mods) ? handlers.containsKey(type) : false;
   }

   private static <T> void map(Class<T> type, Message.Reader<T> reader, Message.Writer<T> writer) {
      handlers.put(type, Pair.of(reader, writer));
   }

   private static byte readByte(ByteBuf buf) {
      return buf.readByte();
   }

   private static void writeByte(byte b, ByteBuf buf) {
      buf.writeByte(b);
   }

   private static short readShort(ByteBuf buf) {
      return buf.readShort();
   }

   private static void writeShort(short s, ByteBuf buf) {
      buf.writeShort(s);
   }

   private static int readInt(ByteBuf buf) {
      return buf.readInt();
   }

   private static void writeInt(int i, ByteBuf buf) {
      buf.writeInt(i);
   }

   private static long readLong(ByteBuf buf) {
      return buf.readLong();
   }

   private static void writeLong(long l, ByteBuf buf) {
      buf.writeLong(l);
   }

   private static float readFloat(ByteBuf buf) {
      return buf.readFloat();
   }

   private static void writeFloat(float f, ByteBuf buf) {
      buf.writeFloat(f);
   }

   private static double readDouble(ByteBuf buf) {
      return buf.readDouble();
   }

   private static void writeDouble(double d, ByteBuf buf) {
      buf.writeDouble(d);
   }

   private static boolean readBoolean(ByteBuf buf) {
      return buf.readBoolean();
   }

   private static void writeBoolean(boolean b, ByteBuf buf) {
      buf.writeBoolean(b);
   }

   private static char readChar(ByteBuf buf) {
      return buf.readChar();
   }

   private static void writeChar(char c, ByteBuf buf) {
      buf.writeChar(c);
   }

   private static String readString(ByteBuf buf) {
      return ByteBufUtils.readUTF8String(buf);
   }

   private static void writeString(String s, ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, s);
   }

   private static NBTTagCompound readNBT(ByteBuf buf) {
      return ByteBufUtils.readTag(buf);
   }

   private static void writeNBT(NBTTagCompound cmp, ByteBuf buf) {
      ByteBufUtils.writeTag(buf, cmp);
   }

   private static ItemStack readItemStack(ByteBuf buf) {
      return ByteBufUtils.readItemStack(buf);
   }

   private static void writeItemStack(ItemStack stack, ByteBuf buf) {
      ByteBufUtils.writeItemStack(buf, stack);
   }

   private static BlockPos readBlockPos(ByteBuf buf) {
      return BlockPos.fromLong(buf.readLong());
   }

   private static void writeBlockPos(BlockPos pos, ByteBuf buf) {
      buf.writeLong(pos.toLong());
   }

   static {
      map(Byte.TYPE, Message::readByte, Message::writeByte);
      map(Short.TYPE, Message::readShort, Message::writeShort);
      map(Integer.TYPE, Message::readInt, Message::writeInt);
      map(Long.TYPE, Message::readLong, Message::writeLong);
      map(Float.TYPE, Message::readFloat, Message::writeFloat);
      map(Double.TYPE, Message::readDouble, Message::writeDouble);
      map(Boolean.TYPE, Message::readBoolean, Message::writeBoolean);
      map(Character.TYPE, Message::readChar, Message::writeChar);
      map(String.class, Message::readString, Message::writeString);
      map(NBTTagCompound.class, Message::readNBT, Message::writeNBT);
      map(ItemStack.class, Message::readItemStack, Message::writeItemStack);
      map(BlockPos.class, Message::readBlockPos, Message::writeBlockPos);
   }

   public interface Reader<T> {
      T read(ByteBuf var1);
   }

   public interface Writer<T> {
      void write(T var1, ByteBuf var2);
   }
}
