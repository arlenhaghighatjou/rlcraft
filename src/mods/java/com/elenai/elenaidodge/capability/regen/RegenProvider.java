package com.elenai.elenaidodge.capability.regen;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class RegenProvider implements ICapabilitySerializable<NBTBase> {
   @CapabilityInject(IRegen.class)
   public static final Capability<IRegen> REGEN_CAP = null;
   private IRegen instance;

   public RegenProvider() {
      this.instance = (IRegen)REGEN_CAP.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == REGEN_CAP;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == REGEN_CAP ? REGEN_CAP.cast(this.instance) : null);
   }

   public NBTBase serializeNBT() {
      return REGEN_CAP.getStorage().writeNBT(REGEN_CAP, this.instance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTBase nbt) {
      REGEN_CAP.getStorage().readNBT(REGEN_CAP, this.instance, (EnumFacing)null, nbt);
   }
}
