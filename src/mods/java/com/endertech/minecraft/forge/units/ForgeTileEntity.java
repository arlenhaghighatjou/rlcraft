package com.endertech.minecraft.forge.units;

import com.endertech.minecraft.forge.ForgeMain;
import com.endertech.minecraft.forge.data.TileInventory;
import com.endertech.minecraft.forge.messages.TileSharedDataMsg;
import com.endertech.minecraft.forge.world.ForgeWorld;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public abstract class ForgeTileEntity extends TileEntity {
   public ForgeTileEntity() {
   }

   public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
      return oldState.getBlock() != newState.getBlock();
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound compound = new NBTTagCompound();
      this.writeToNBT(compound);
      return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), compound);
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.readFromNBT(pkt.getNbtCompound());
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.readSharedData(compound);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      this.writeSharedData(compound);
      return super.writeToNBT(compound);
   }

   public NBTTagCompound getSharedData() {
      NBTTagCompound compound = new NBTTagCompound();
      this.writeSharedData(compound);
      return compound;
   }

   public void syncWithClients() {
      if (ForgeWorld.isServerSide(this.getWorld())) {
         TileSharedDataMsg message = new TileSharedDataMsg(this.getSharedData(), this.getPos());
         ForgeMain.instance.getConnection().sendToDimension(message, this.getWorld().provider.getDimension());
         this.markDirty();
      }

   }

   public abstract void readSharedData(NBTTagCompound var1);

   public abstract void writeSharedData(NBTTagCompound var1);

   public abstract static class WithInventory extends ForgeTileEntity {
      public WithInventory() {
      }

      public abstract TileInventory getTileInventory();

      public void readFromNBT(NBTTagCompound compound) {
         super.readFromNBT(compound);
         this.getTileInventory().deserializeNBT(compound.getCompoundTag("inv"));
      }

      public NBTTagCompound writeToNBT(NBTTagCompound compound) {
         super.writeToNBT(compound);
         compound.setTag("inv", this.getTileInventory().serializeNBT());
         return compound;
      }

      public boolean canInteractWith(EntityPlayer playerIn) {
         return !this.isInvalid() && playerIn.getDistanceSq(this.pos.add(0.5D, 0.5D, 0.5D)) <= 64.0D;
      }

      public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
         return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? true : super.hasCapability(capability, facing);
      }

      public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
         return (T)(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.getTileInventory()) : super.getCapability(capability, facing));
      }
   }
}
