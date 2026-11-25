package ivorius.reccomplex.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.network.IvPacketHelper;
import java.util.function.Supplier;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketWorldData implements IMessage {
   protected NBTTagCompound worldData;
   protected BlockPos source;
   protected BlockPos capturePoint1;
   protected BlockPos capturePoint2;

   public PacketWorldData() {
   }

   public PacketWorldData(NBTTagCompound worldData, BlockPos source, BlockPos capturePoint1, BlockPos capturePoint2) {
      this.worldData = worldData;
      this.source = source;
      this.capturePoint1 = capturePoint1;
      this.capturePoint2 = capturePoint2;
   }

   public NBTTagCompound getWorldData() {
      return this.worldData;
   }

   public void setWorldData(NBTTagCompound worldData) {
      this.worldData = worldData;
   }

   public BlockPos getCapturePoint1() {
      return this.capturePoint1;
   }

   public void setCapturePoint1(BlockPos capturePoint1) {
      this.capturePoint1 = capturePoint1;
   }

   public BlockPos getCapturePoint2() {
      return this.capturePoint2;
   }

   public void setCapturePoint2(BlockPos capturePoint2) {
      this.capturePoint2 = capturePoint2;
   }

   public BlockPos getSource() {
      return this.source;
   }

   public void setSource(BlockPos source) {
      this.source = source;
   }

   public void fromBytes(ByteBuf buf) {
      this.worldData = ByteBufUtils.readTag(buf);
      this.source = (BlockPos)IvPacketHelper.maybeRead(buf, (Object)null, () -> {
         return BlockPositions.readFromBuffer(buf);
      });
      this.capturePoint1 = (BlockPos)IvPacketHelper.maybeRead(buf, (Object)null, () -> {
         return BlockPositions.readFromBuffer(buf);
      });
      this.capturePoint2 = (BlockPos)IvPacketHelper.maybeRead(buf, (Object)null, () -> {
         return BlockPositions.readFromBuffer(buf);
      });
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeTag(buf, this.worldData);
      IvPacketHelper.maybeWrite(buf, this.source, () -> {
         BlockPositions.writeToBuffer(this.source, buf);
      });
      IvPacketHelper.maybeWrite(buf, this.capturePoint1, () -> {
         BlockPositions.writeToBuffer(this.capturePoint1, buf);
      });
      IvPacketHelper.maybeWrite(buf, this.capturePoint2, () -> {
         BlockPositions.writeToBuffer(this.capturePoint2, buf);
      });
   }
}
