package reborncore.common.util;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.packet.CustomDescriptionPacket;
import reborncore.common.tile.FluidConfiguration;
import reborncore.common.tile.RebornMachineTile;

public class Tank extends FluidTank {
   private final String name;
   private FluidStack lastBeforeUpdate = null;
   Fluid lastFluid;
   int lastAmmount;
   @Nullable
   private EnumFacing side = null;
   RebornMachineTile machine;

   public Tank(String name, int capacity, RebornMachineTile tile) {
      super(capacity);
      this.name = name;
      this.tile = tile;
      this.machine = tile;
   }

   public boolean isEmpty() {
      return this.getFluid() == null || this.getFluid().amount <= 0;
   }

   public boolean isFull() {
      return this.getFluid() != null && this.getFluid().amount >= this.getCapacity();
   }

   public Fluid getFluidType() {
      return this.getFluid() != null ? this.getFluid().getFluid() : null;
   }

   public final NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      NBTTagCompound tankData = new NBTTagCompound();
      super.writeToNBT(tankData);
      nbt.setTag(this.name, tankData);
      return nbt;
   }

   public void setFluidAmount(int amount) {
      if (this.fluid != null) {
         this.fluid.amount = amount;
      }

   }

   public final FluidTank readFromNBT(NBTTagCompound nbt) {
      if (nbt.hasKey(this.name)) {
         this.setFluid((FluidStack)null);
         NBTTagCompound tankData = nbt.getCompoundTag(this.name);
         super.readFromNBT(tankData);
      }

      return this;
   }

   @Nullable
   public EnumFacing getSide() {
      return this.side;
   }

   public void setSide(@Nullable EnumFacing side) {
      this.side = side;
   }

   public boolean canFill() {
      if (this.side != null && this.machine.fluidConfiguration != null) {
         FluidConfiguration.FluidConfig fluidConfig = this.machine.fluidConfiguration.getSideDetail(this.side);
         return fluidConfig == null ? super.canFill() : fluidConfig.getIoConfig().isInsert();
      } else {
         return super.canFill();
      }
   }

   public boolean canDrain() {
      if (this.side != null && this.machine.fluidConfiguration != null) {
         FluidConfiguration.FluidConfig fluidConfig = this.machine.fluidConfiguration.getSideDetail(this.side);
         return fluidConfig == null ? super.canDrain() : fluidConfig.getIoConfig().isExtact();
      } else {
         return super.canDrain();
      }
   }

   /** @deprecated */
   @Deprecated
   public void compareAndUpdate() {
      if (this.tile != null && !this.tile.getWorld().isRemote) {
         FluidStack current = this.getFluid();
         if (current != null) {
            if (this.lastBeforeUpdate != null) {
               if (Math.abs(current.amount - this.lastBeforeUpdate.amount) >= 500) {
                  NetworkManager.sendToWorld(new CustomDescriptionPacket(this.tile), this.tile.getWorld());
                  this.lastBeforeUpdate = current.copy();
               } else if (this.lastBeforeUpdate.amount < this.getCapacity() && current.amount == this.getCapacity() || this.lastBeforeUpdate.amount == this.getCapacity() && current.amount < this.getCapacity()) {
                  NetworkManager.sendToWorld(new CustomDescriptionPacket(this.tile), this.tile.getWorld());
                  this.lastBeforeUpdate = current.copy();
               }
            } else {
               NetworkManager.sendToWorld(new CustomDescriptionPacket(this.tile), this.tile.getWorld());
               this.lastBeforeUpdate = current.copy();
            }
         } else if (this.lastBeforeUpdate != null) {
            NetworkManager.sendToWorld(new CustomDescriptionPacket(this.tile), this.tile.getWorld());
            this.lastBeforeUpdate = null;
         }

      }
   }
}
