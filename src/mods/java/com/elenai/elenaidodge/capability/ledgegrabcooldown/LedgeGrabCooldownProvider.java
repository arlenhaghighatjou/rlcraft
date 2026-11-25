package com.elenai.elenaidodge.capability.ledgegrabcooldown;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class LedgeGrabCooldownProvider implements ICapabilitySerializable<NBTBase> {
   @CapabilityInject(ILedgeGrabCooldown.class)
   public static final Capability<ILedgeGrabCooldown> LEDGEGRABCOOLDOWN_CAP = null;
   private ILedgeGrabCooldown instance;

   public LedgeGrabCooldownProvider() {
      this.instance = (ILedgeGrabCooldown)LEDGEGRABCOOLDOWN_CAP.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == LEDGEGRABCOOLDOWN_CAP;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == LEDGEGRABCOOLDOWN_CAP ? LEDGEGRABCOOLDOWN_CAP.cast(this.instance) : null);
   }

   public NBTBase serializeNBT() {
      return LEDGEGRABCOOLDOWN_CAP.getStorage().writeNBT(LEDGEGRABCOOLDOWN_CAP, this.instance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTBase nbt) {
      LEDGEGRABCOOLDOWN_CAP.getStorage().readNBT(LEDGEGRABCOOLDOWN_CAP, this.instance, (EnumFacing)null, nbt);
   }
}
