package com.charles445.simpledifficulty.capability;

import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class ThirstProvider implements ICapabilitySerializable<NBTBase> {
   final Capability<IThirstCapability> capability;
   final IThirstCapability instance;

   public ThirstProvider(Capability<IThirstCapability> newcapability) {
      this.capability = newcapability;
      this.instance = (IThirstCapability)this.capability.getDefaultInstance();
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
