package com.elenai.elenaidodge.capability.enabled;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class EnabledProvider implements ICapabilitySerializable<NBTBase> {
   @CapabilityInject(IEnabled.class)
   public static final Capability<IEnabled> ENABLED_CAP = null;
   private IEnabled instance;

   public EnabledProvider() {
      this.instance = (IEnabled)ENABLED_CAP.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == ENABLED_CAP;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == ENABLED_CAP ? ENABLED_CAP.cast(this.instance) : null);
   }

   public NBTBase serializeNBT() {
      return ENABLED_CAP.getStorage().writeNBT(ENABLED_CAP, this.instance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTBase nbt) {
      ENABLED_CAP.getStorage().readNBT(ENABLED_CAP, this.instance, (EnumFacing)null, nbt);
   }
}
