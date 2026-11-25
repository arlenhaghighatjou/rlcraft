package com.elenai.elenaidodge.capability.joined;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class JoinedProvider implements ICapabilitySerializable<NBTBase> {
   @CapabilityInject(IJoined.class)
   public static final Capability<IJoined> JOINED_CAP = null;
   private IJoined instance;

   public JoinedProvider() {
      this.instance = (IJoined)JOINED_CAP.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == JOINED_CAP;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == JOINED_CAP ? JOINED_CAP.cast(this.instance) : null);
   }

   public NBTBase serializeNBT() {
      return JOINED_CAP.getStorage().writeNBT(JOINED_CAP, this.instance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTBase nbt) {
      JOINED_CAP.getStorage().readNBT(JOINED_CAP, this.instance, (EnumFacing)null, nbt);
   }
}
