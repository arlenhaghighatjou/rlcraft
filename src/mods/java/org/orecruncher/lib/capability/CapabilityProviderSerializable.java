package org.orecruncher.lib.capability;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public class CapabilityProviderSerializable<H> extends CapabilityProviderSimple<H> implements INBTSerializable<NBTBase> {
   public CapabilityProviderSerializable(Capability<H> capability, @Nullable EnumFacing facing) {
      this(capability, facing, capability.getDefaultInstance());
   }

   public CapabilityProviderSerializable(Capability<H> capability, @Nullable EnumFacing facing, @Nullable H instance) {
      super(capability, facing, instance);
   }

   @Nullable
   public NBTBase serializeNBT() {
      return this.getCapability().writeNBT(this.getInstance(), this.getFacing());
   }

   public void deserializeNBT(NBTBase nbt) {
      this.getCapability().readNBT(this.getInstance(), this.getFacing(), nbt);
   }
}
