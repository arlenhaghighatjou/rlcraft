package com.teamacronymcoders.base.tileentities;

import com.teamacronymcoders.base.blocks.properties.SideType;
import com.teamacronymcoders.base.client.IBlockOverlayText;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntitySidedBase<CAP> extends TileEntityBase implements IBlockOverlayText {
   private SideType[] sideTypes = new SideType[6];
   private boolean isColorBlindActive;

   public TileEntitySidedBase() {
      Arrays.fill(this.sideTypes, SideType.NONE);
      this.isColorBlindActive = false;
   }

   public void onLoad() {
      super.onLoad();
   }

   public void toggleSide(int side) {
      this.setSideType(side, this.getSideValue(side).next());
      this.updateBlock();
   }

   public void updateBlock() {
      IBlockState state = this.world.getBlockState(this.getPos());
      this.world.notifyBlockUpdate(this.pos, state, state, 3);
      this.world.notifyNeighborsOfStateChange(this.pos, state.getBlock(), true);
      if (!this.world.isRemote) {
         this.world.addBlockEvent(this.getPos(), this.getBlockType(), 0, 0);
      }

   }

   public void setSideType(int side, SideType sideType) {
      this.sideTypes[side] = sideType;
   }

   public SideType getSideValue(int side) {
      return this.sideTypes[side];
   }

   public SideType getSideValue(EnumFacing facing) {
      return this.getSideValue(facing.ordinal());
   }

   protected void readFromDisk(NBTTagCompound data) {
      this.setSideTypesFromNBT(data);
      this.readCapability(data);
   }

   protected abstract void readCapability(NBTTagCompound var1);

   protected NBTTagCompound writeToDisk(NBTTagCompound data) {
      this.setSideTypesToNBT(data);
      this.writeCapability(data);
      return data;
   }

   protected abstract void writeCapability(NBTTagCompound var1);

   public boolean receiveClientEvent(int id, int arg) {
      if (id == 0) {
         this.getWorld().notifyNeighborsOfStateChange(this.getPos(), this.getWorld().getBlockState(this.pos).getBlock(), true);
         return true;
      } else {
         return false;
      }
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      if (capability != this.getCapabilityType()) {
         return super.hasCapability(capability, facing);
      } else {
         return facing == null || this.getSideValue(facing) != SideType.NONE;
      }
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      if (capability == this.getCapabilityType()) {
         if (facing == null) {
            return (T)this.castCapability(this.getInternalCapability());
         }

         SideType sideType = this.getSideValue(facing.getIndex());
         if (sideType == SideType.INPUT) {
            return (T)this.castCapability(this.getInputCapability());
         }

         if (sideType == SideType.OUTPUT) {
            return (T)this.castCapability(this.getOutputCapability());
         }
      }

      return (T)super.getCapability(capability, facing);
   }

   protected abstract Capability<CAP> getCapabilityType();

   protected <T> T castCapability(CAP cap) {
      return (T)this.getCapabilityType().cast(cap);
   }

   protected abstract CAP getInternalCapability();

   protected abstract CAP getOutputCapability();

   protected abstract CAP getInputCapability();

   @SideOnly(Side.CLIENT)
   public String[] getOverlayText(EntityPlayer player, RayTraceResult rayTrace, boolean tool) {
      if (tool && this.isColorBlindActive) {
         SideType facing = this.sideTypes[rayTrace.sideHit.ordinal()];
         SideType opposite = this.sideTypes[rayTrace.sideHit.getOpposite().ordinal()];
         return new String[]{I18n.format("base.block_side.facing", new Object[0]) + ": " + I18n.format("base.sidetype." + facing.name().toLowerCase(), new Object[0]), I18n.format("base.block_side.opposite", new Object[0]) + ": " + I18n.format("base.sidetype." + opposite.name().toLowerCase(), new Object[0])};
      } else {
         return null;
      }
   }

   protected void setSideTypesToNBT(NBTTagCompound nbt) {
      int[] array = new int[6];

      for(int i = 0; i < this.sideTypes.length; ++i) {
         array[i] = this.sideTypes[i].ordinal();
      }

      nbt.setIntArray("sideTypes", array);
   }

   protected void setSideTypesFromNBT(NBTTagCompound nbt) {
      int[] array = nbt.getIntArray("sideTypes");
      if (this.sideTypes == null) {
         this.sideTypes = new SideType[6];
      }

      for(int i = 0; i < array.length; ++i) {
         this.sideTypes[i] = SideType.values()[array[i]];
      }

   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      this.setSideTypesToNBT(nbttagcompound);
      return new SPacketUpdateTileEntity(this.pos, 3, nbttagcompound);
   }

   @Nonnull
   public NBTTagCompound getUpdateTag() {
      NBTTagCompound nbt = super.writeToNBT(new NBTTagCompound());
      this.setSideTypesToNBT(nbt);
      return nbt;
   }

   @SideOnly(Side.CLIENT)
   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.setSideTypesFromNBT(pkt.getNbtCompound());
   }
}
