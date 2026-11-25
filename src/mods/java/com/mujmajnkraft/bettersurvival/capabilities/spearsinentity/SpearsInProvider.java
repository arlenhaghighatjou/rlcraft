package com.mujmajnkraft.bettersurvival.capabilities.spearsinentity;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class SpearsInProvider implements ICapabilitySerializable<NBTTagList> {
   @CapabilityInject(ISpearsIn.class)
   public static final Capability<ISpearsIn> SPEARSIN_CAP = null;
   private ISpearsIn instance;

   public SpearsInProvider() {
      this.instance = (ISpearsIn)SPEARSIN_CAP.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == SPEARSIN_CAP;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == SPEARSIN_CAP ? SPEARSIN_CAP.cast(this.instance) : null);
   }

   public NBTTagList serializeNBT() {
      return (NBTTagList)SPEARSIN_CAP.getStorage().writeNBT(SPEARSIN_CAP, this.instance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTTagList nbt) {
      SPEARSIN_CAP.getStorage().readNBT(SPEARSIN_CAP, this.instance, (EnumFacing)null, nbt);
   }
}
