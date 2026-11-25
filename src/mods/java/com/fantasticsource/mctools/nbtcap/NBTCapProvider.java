package com.fantasticsource.mctools.nbtcap;

import com.fantasticsource.fantasticlib.api.FLibAPI;
import com.fantasticsource.fantasticlib.api.INBTCap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class NBTCapProvider implements ICapabilitySerializable<NBTTagCompound> {
   protected INBTCap instance = new NBTCap();

   public NBTCapProvider() {
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == FLibAPI.NBT_CAP;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == FLibAPI.NBT_CAP ? FLibAPI.NBT_CAP.cast(this.instance) : null);
   }

   public NBTTagCompound serializeNBT() {
      return (NBTTagCompound)FLibAPI.NBT_CAP.getStorage().writeNBT(FLibAPI.NBT_CAP, this.instance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      FLibAPI.NBT_CAP.getStorage().readNBT(FLibAPI.NBT_CAP, this.instance, (EnumFacing)null, nbt);
   }
}
