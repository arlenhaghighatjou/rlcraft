package vazkii.arl.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.Serializable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class NetworkMessage<REQ extends NetworkMessage> implements Serializable, IMessage, IMessageHandler<REQ, IMessage> {
   public NetworkMessage() {
   }

   public IMessage handleMessage(MessageContext context) {
      return null;
   }

   public final IMessage onMessage(REQ message, MessageContext context) {
      return message.handleMessage(context);
   }

   public void fromBytes(ByteBuf buf) {
   }

   public void toBytes(ByteBuf buf) {
   }

   protected static byte readByte(ByteBuf buf) {
      return buf.readByte();
   }

   protected static void writeByte(byte b, ByteBuf buf) {
      buf.writeByte(b);
   }

   protected static short readShort(ByteBuf buf) {
      return buf.readShort();
   }

   protected static void writeShort(short s, ByteBuf buf) {
      buf.writeShort(s);
   }

   protected static int readInt(ByteBuf buf) {
      return buf.readInt();
   }

   protected static void writeInt(int i, ByteBuf buf) {
      buf.writeInt(i);
   }

   protected static long readLong(ByteBuf buf) {
      return buf.readLong();
   }

   protected static void writeLong(long l, ByteBuf buf) {
      buf.writeLong(l);
   }

   protected static float readFloat(ByteBuf buf) {
      return buf.readFloat();
   }

   protected static void writeFloat(float f, ByteBuf buf) {
      buf.writeFloat(f);
   }

   protected static double readDouble(ByteBuf buf) {
      return buf.readDouble();
   }

   protected static void writeDouble(double d, ByteBuf buf) {
      buf.writeDouble(d);
   }

   protected static boolean readBoolean(ByteBuf buf) {
      return buf.readBoolean();
   }

   protected static void writeBoolean(boolean b, ByteBuf buf) {
      buf.writeBoolean(b);
   }

   protected static char readChar(ByteBuf buf) {
      return buf.readChar();
   }

   protected static void writeChar(char c, ByteBuf buf) {
      buf.writeChar(c);
   }

   protected static String readString(ByteBuf buf) {
      return ByteBufUtils.readUTF8String(buf);
   }

   protected static void writeString(String s, ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, s);
   }

   protected static NBTTagCompound readNBT(ByteBuf buf) {
      return ByteBufUtils.readTag(buf);
   }

   protected static void writeNBT(NBTTagCompound cmp, ByteBuf buf) {
      ByteBufUtils.writeTag(buf, cmp);
   }

   protected static ItemStack readItemStack(ByteBuf buf) {
      return new ItemStack(readNBT(buf));
   }

   protected static void writeItemStack(ItemStack stack, ByteBuf buf) {
      writeNBT(stack.serializeNBT(), buf);
   }

   protected static BlockPos readBlockPos(ByteBuf buf) {
      return BlockPos.fromLong(buf.readLong());
   }

   protected static void writeBlockPos(BlockPos pos, ByteBuf buf) {
      buf.writeLong(pos.toLong());
   }
}
