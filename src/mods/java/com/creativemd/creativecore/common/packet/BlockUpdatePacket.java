package com.creativemd.creativecore.common.packet;

import com.creativemd.creativecore.common.tileentity.TileEntityCreative;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class BlockUpdatePacket extends CreativeCorePacket {
   public BlockPos pos;
   public NBTTagCompound nbt;

   public BlockUpdatePacket() {
   }

   public BlockUpdatePacket(TileEntity te, NBTTagCompound nbt) {
      this(te.getPos(), nbt);
   }

   public BlockUpdatePacket(BlockPos pos, NBTTagCompound nbt) {
      this.pos = pos;
      this.nbt = nbt;
   }

   public void writeBytes(ByteBuf buf) {
      writePos(buf, this.pos);
      writeNBT(buf, this.nbt);
   }

   public void readBytes(ByteBuf buf) {
      this.pos = readPos(buf);
      this.nbt = readNBT(buf);
   }

   public void executeClient(EntityPlayer player) {
   }

   public void executeServer(EntityPlayer player) {
      TileEntity te = player.world.getTileEntity(this.pos);
      if (te instanceof TileEntityCreative) {
         ((TileEntityCreative)te).receiveUpdatePacket(this.nbt);
      }

   }
}
