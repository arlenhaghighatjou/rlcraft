package com.elenai.elenaidodge.capability.particles;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class ParticlesProvider implements ICapabilitySerializable<NBTBase> {
   @CapabilityInject(IParticles.class)
   public static final Capability<IParticles> PARTICLES_CAP = null;
   private IParticles instance;

   public ParticlesProvider() {
      this.instance = (IParticles)PARTICLES_CAP.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == PARTICLES_CAP;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == PARTICLES_CAP ? PARTICLES_CAP.cast(this.instance) : null);
   }

   public NBTBase serializeNBT() {
      return PARTICLES_CAP.getStorage().writeNBT(PARTICLES_CAP, this.instance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTBase nbt) {
      PARTICLES_CAP.getStorage().readNBT(PARTICLES_CAP, this.instance, (EnumFacing)null, nbt);
   }
}
