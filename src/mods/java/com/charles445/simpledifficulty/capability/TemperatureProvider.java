package com.charles445.simpledifficulty.capability;

import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class TemperatureProvider implements ICapabilitySerializable<NBTBase> {
   final Capability<ITemperatureCapability> capability;
   final ITemperatureCapability instance;

   public TemperatureProvider(Capability<ITemperatureCapability> newcapability) {
      this.capability = newcapability;
      this.instance = (ITemperatureCapability)this.capability.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> requestedcapability, EnumFacing facing) {
      return requestedcapability == this.capability;
   }

   public <T> T getCapability(Capability<T> requestedcapability, EnumFacing facing) {
      return (T)(requestedcapability == this.capability ? this.capability.cast(this.instance) : null);
   }

   public NBTBase serializeNBT() {
      return this.capability.writeNBT(this.instance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTBase nbt) {
      this.capability.readNBT(this.instance, (EnumFacing)null, nbt);
   }
}
