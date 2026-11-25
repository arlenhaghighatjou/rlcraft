package com.elenai.elenaidodge.capability.airborne;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class AirborneProvider implements ICapabilitySerializable<NBTBase> {
   @CapabilityInject(IAirborne.class)
   public static final Capability<IAirborne> AIRBORNE_CAP = null;
   private IAirborne instance;

   public AirborneProvider() {
      this.instance = (IAirborne)AIRBORNE_CAP.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == AIRBORNE_CAP;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == AIRBORNE_CAP ? AIRBORNE_CAP.cast(this.instance) : null);
   }

   public NBTBase serializeNBT() {
      return AIRBORNE_CAP.getStorage().writeNBT(AIRBORNE_CAP, this.instance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTBase nbt) {
      AIRBORNE_CAP.getStorage().readNBT(AIRBORNE_CAP, this.instance, (EnumFacing)null, nbt);
   }
}
