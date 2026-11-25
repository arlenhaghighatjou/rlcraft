package com.mujmajnkraft.bettersurvival.capabilities.nunchakucombo;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class NunchakuComboProvider implements ICapabilitySerializable<NBTTagCompound> {
   @CapabilityInject(INunchakuCombo.class)
   public static final Capability<INunchakuCombo> NUNCHAKUCOMBO_CAP = null;
   private INunchakuCombo instance;

   public NunchakuComboProvider() {
      this.instance = (INunchakuCombo)NUNCHAKUCOMBO_CAP.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == NUNCHAKUCOMBO_CAP;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == NUNCHAKUCOMBO_CAP ? NUNCHAKUCOMBO_CAP.cast(this.instance) : null);
   }

   public NBTTagCompound serializeNBT() {
      return (NBTTagCompound)NUNCHAKUCOMBO_CAP.getStorage().writeNBT(NUNCHAKUCOMBO_CAP, this.instance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      NUNCHAKUCOMBO_CAP.getStorage().readNBT(NUNCHAKUCOMBO_CAP, this.instance, (EnumFacing)null, nbt);
   }
}
