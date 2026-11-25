package ivorius.reccomplex.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.blocks.BlockStates;
import ivorius.ivtoolkit.network.IvPacketHelper;
import java.util.function.Supplier;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketInspectBlock implements IMessage {
   private NBTTagCompound tileEntityData;
   private BlockPos pos;
   private IBlockState state;

   public PacketInspectBlock() {
   }

   public PacketInspectBlock(BlockPos pos, IBlockState state) {
      this.pos = pos;
      this.state = state;
   }

   public PacketInspectBlock(NBTTagCompound tileEntityData, BlockPos pos, IBlockState state) {
      this.tileEntityData = tileEntityData;
      this.pos = pos;
      this.state = state;
   }

   public NBTTagCompound getTileEntityData() {
      return this.tileEntityData;
   }

   public void setTileEntityData(NBTTagCompound tileEntityData) {
      this.tileEntityData = tileEntityData;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public void setPos(BlockPos pos) {
      this.pos = pos;
   }

   public IBlockState getState() {
      return this.state;
   }

   public void setState(IBlockState state) {
      this.state = state;
   }

   public void fromBytes(ByteBuf buf) {
      this.tileEntityData = (NBTTagCompound)IvPacketHelper.maybeRead(buf, (Object)null, () -> {
         return ByteBufUtils.readTag(buf);
      });
      this.pos = BlockPositions.readFromBuffer(buf);
      this.state = BlockStates.readBlockState(buf);
   }

   public void toBytes(ByteBuf buf) {
      IvPacketHelper.maybeWrite(buf, this.tileEntityData, () -> {
         ByteBufUtils.writeTag(buf, this.tileEntityData);
      });
      BlockPositions.writeToBuffer(this.pos, buf);
      BlockStates.writeBlockState(buf, this.state);
   }
}
