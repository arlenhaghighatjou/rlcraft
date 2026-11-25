package com.creativemd.creativecore.common.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityCreative extends TileEntity {
   private World tempWorld;

   public TileEntityCreative() {
   }

   public boolean isClientSide() {
      return this.world != null ? this.world.isRemote : this.tempWorld.isRemote;
   }

   public World getTempWorld() {
      return this.tempWorld;
   }

   protected void setWorldCreate(World worldIn) {
      this.tempWorld = worldIn;
   }

   public void deleteTempWorld() {
      this.tempWorld = null;
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), this.getUpdateTag());
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.handleUpdate(pkt.getNbtCompound(), false);
   }

   public void handleUpdateTag(NBTTagCompound tag) {
      this.handleUpdate(tag, true);
   }

   public abstract void handleUpdate(NBTTagCompound var1, boolean var2);

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public double getDistance(BlockPos coord) {
      return Math.sqrt(this.pos.distanceSq(coord));
   }

   @SideOnly(Side.CLIENT)
   public void updateRender() {
      this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
   }

   public void updateBlock() {
      if (!this.world.isRemote) {
         IBlockState state = this.world.getBlockState(this.pos);
         this.world.notifyBlockUpdate(this.pos, state, state, 3);
         this.world.markChunkDirty(this.getPos(), this);
      }

   }
}
