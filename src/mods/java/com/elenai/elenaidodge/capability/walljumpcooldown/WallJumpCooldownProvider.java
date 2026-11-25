package com.elenai.elenaidodge.capability.walljumpcooldown;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class WallJumpCooldownProvider implements ICapabilitySerializable<NBTBase> {
   @CapabilityInject(IWallJumpCooldown.class)
   public static final Capability<IWallJumpCooldown> WALLJUMPCOOLDOWN_CAP = null;
   private IWallJumpCooldown instance;

   public WallJumpCooldownProvider() {
      this.instance = (IWallJumpCooldown)WALLJUMPCOOLDOWN_CAP.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == WALLJUMPCOOLDOWN_CAP;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == WALLJUMPCOOLDOWN_CAP ? WALLJUMPCOOLDOWN_CAP.cast(this.instance) : null);
   }

   public NBTBase serializeNBT() {
      return WALLJUMPCOOLDOWN_CAP.getStorage().writeNBT(WALLJUMPCOOLDOWN_CAP, this.instance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTBase nbt) {
      WALLJUMPCOOLDOWN_CAP.getStorage().readNBT(WALLJUMPCOOLDOWN_CAP, this.instance, (EnumFacing)null, nbt);
   }
}
