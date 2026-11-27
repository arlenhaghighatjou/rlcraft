package com.Shultrea.Rin.Prop_Sector;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class ArrowPropertiesProvider implements ICapabilitySerializable<NBTTagCompound> {
   public static Capability<IArrowProperties> ARROWPROPERTIES_CAP = null;
   private IArrowProperties instance;

   public ArrowPropertiesProvider() {
      this.instance = (IArrowProperties)ARROWPROPERTIES_CAP.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == ARROWPROPERTIES_CAP;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == ARROWPROPERTIES_CAP ? ARROWPROPERTIES_CAP.cast(this.instance) : null);
   }

   public NBTTagCompound serializeNBT() {
      return (NBTTagCompound)ARROWPROPERTIES_CAP.getStorage().writeNBT(ARROWPROPERTIES_CAP, this.instance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      ARROWPROPERTIES_CAP.getStorage().readNBT(ARROWPROPERTIES_CAP, this.instance, (EnumFacing)null, nbt);
   }

   public static void registerCapabilities() {
      CapabilityManager.INSTANCE.onRegistered(IArrowProperties.class, capability -> ARROWPROPERTIES_CAP = capability);
   }
}
