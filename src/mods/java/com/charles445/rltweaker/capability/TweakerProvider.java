package com.charles445.rltweaker.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class TweakerProvider implements ICapabilitySerializable<NBTBase> {
   final Capability<ITweakerCapability> capability;
   final ITweakerCapability instance;

   public TweakerProvider(Capability<ITweakerCapability> newcapability) {
      this.capability = newcapability;
      this.instance = (ITweakerCapability)this.capability.getDefaultInstance();
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
