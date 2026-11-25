package com.creativemd.creativecore.common.packet;

import com.creativemd.creativecore.common.utils.stack.InfoStack;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CreativeCorePacket {
   private static Gson gson = new Gson();
   private static final List<Class<? extends CreativeCorePacket>> packetTypes = new ArrayList();
   public static int maxPacketSize = 1047576;

   public CreativeCorePacket() {
   }

   public static void registerPacket(Class<? extends CreativeCorePacket> clazz) {
      packetTypes.add(clazz);
   }

   public static Class<? extends CreativeCorePacket> getClass(int id) {
      return (Class)packetTypes.get(id);
   }

   public static int getId(Class<? extends CreativeCorePacket> clazz) {
      return packetTypes.indexOf(clazz);
   }

   public static int getId(CreativeCorePacket packet) {
      return getId(packet.getClass());
   }

   public abstract void writeBytes(ByteBuf var1);

   public abstract void readBytes(ByteBuf var1);

   @SideOnly(Side.CLIENT)
   public abstract void executeClient(EntityPlayer var1);

   public abstract void executeServer(EntityPlayer var1);

   public static void writeString(ByteBuf buf, String input) {
      ByteBufUtils.writeUTF8String(buf, input);
   }

   public static String readString(ByteBuf buf) {
      return ByteBufUtils.readUTF8String(buf);
   }

   public static void writePos(ByteBuf buf, BlockPos pos) {
      buf.writeInt(pos.getX());
      buf.writeInt(pos.getY());
      buf.writeInt(pos.getZ());
   }

   public static BlockPos readPos(ByteBuf buf) {
      return new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
   }

   public static void writeState(ByteBuf buf, IBlockState state) {
      buf.writeInt(Block.getStateId(state));
   }

   public static IBlockState readState(ByteBuf buf) {
      return Block.getStateById(buf.readInt());
   }

   public static void writeNBT(ByteBuf buf, NBTTagCompound nbt) {
      ByteBufUtils.writeTag(buf, nbt);
   }

   public static NBTTagCompound readNBT(ByteBuf buf) {
      return ByteBufUtils.readTag(buf);
   }

   public static void writeVec3d(Vec3d vec, ByteBuf buf) {
      buf.writeDouble(vec.x);
      buf.writeDouble(vec.y);
      buf.writeDouble(vec.z);
   }

   public static Vec3d readVec3d(ByteBuf buf) {
      double x = buf.readDouble();
      double y = buf.readDouble();
      double z = buf.readDouble();
      return new Vec3d(x, y, z);
   }

   public static void writeInfoStack(ByteBuf buf, InfoStack info) {
      writeNBT(buf, info.writeToNBT(new NBTTagCompound()));
   }

   public static InfoStack readInfoStack(ByteBuf buf) {
      return InfoStack.parseNBT(readNBT(buf));
   }

   public void writeInfoStacks(ByteBuf buf, ArrayList<InfoStack> infos) {
      buf.writeInt(infos.size());

      for(int i = 0; i < infos.size(); ++i) {
         writeInfoStack(buf, (InfoStack)infos.get(i));
      }

   }

   public ArrayList<InfoStack> readInfoStacks(ByteBuf buf) {
      int count = buf.readInt();
      ArrayList<InfoStack> infos = new ArrayList();

      for(int i = 0; i < count; ++i) {
         InfoStack info = readInfoStack(buf);
         if (info != null) {
            infos.add(info);
         }
      }

      return infos;
   }

   public static void writeItemStack(ByteBuf buf, ItemStack stack) {
      ByteBufUtils.writeItemStack(buf, stack);
   }

   public static ItemStack readItemStack(ByteBuf buf) {
      return ByteBufUtils.readItemStack(buf);
   }

   public static EnumFacing readFacing(ByteBuf buf) {
      return EnumFacing.getFront(buf.readInt());
   }

   public static void writeFacing(ByteBuf buf, EnumFacing facing) {
      buf.writeInt(facing.getIndex());
   }

   public static void writePacket(ByteBuf buf, Packet<?> packet) {
      EnumConnectionState state = EnumConnectionState.getFromPacket(packet);
      buf.writeInt(state.getId());

      try {
         buf.writeInt(state.getPacketId(EnumPacketDirection.CLIENTBOUND, packet).intValue());
         packet.writePacketData(new PacketBuffer(buf));
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public static Packet<?> readPacket(ByteBuf buf) {
      EnumConnectionState state = EnumConnectionState.getById(buf.readInt());

      try {
         Packet<?> packet = state.getPacket(EnumPacketDirection.CLIENTBOUND, buf.readInt());
         packet.readPacketData(new PacketBuffer(buf));
         return packet;
      } catch (IllegalAccessException | IOException | InstantiationException var4) {
         throw new RuntimeException(var4);
      }
   }

   public static void writeJson(ByteBuf buf, JsonObject json) {
      writeString(buf, json.toString());
   }

   public static JsonObject readJson(ByteBuf buf) {
      return (JsonObject)gson.fromJson(readString(buf), JsonObject.class);
   }

   public static void openContainerOnServer(EntityPlayerMP entityPlayerMP, Container container) {
      entityPlayerMP.getNextWindowId();
      entityPlayerMP.closeContainer();
      int windowId = entityPlayerMP.currentWindowId;
      entityPlayerMP.openContainer = container;
      entityPlayerMP.openContainer.windowId = windowId;
      entityPlayerMP.openContainer.addListener(entityPlayerMP);
   }
}
