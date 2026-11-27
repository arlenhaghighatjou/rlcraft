package com.Shultrea.Rin.Prop_Sector;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class PlayerPropertiesProvider implements ICapabilitySerializable<NBTTagCompound> {
   public static Capability<IPlayerProperties> PLAYERPROPERTIES_CAP = null;
   private IPlayerProperties propertyInstance;

   public PlayerPropertiesProvider() {
      this.propertyInstance = (IPlayerProperties)PLAYERPROPERTIES_CAP.getDefaultInstance();
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == PLAYERPROPERTIES_CAP;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == PLAYERPROPERTIES_CAP ? PLAYERPROPERTIES_CAP.cast(this.propertyInstance) : null);
   }

   public NBTTagCompound serializeNBT() {
      return (NBTTagCompound)PLAYERPROPERTIES_CAP.getStorage().writeNBT(PLAYERPROPERTIES_CAP, this.propertyInstance, (EnumFacing)null);
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      PLAYERPROPERTIES_CAP.getStorage().readNBT(PLAYERPROPERTIES_CAP, this.propertyInstance, (EnumFacing)null, nbt);
   }

   public static void registerCapabilities() {
      CapabilityManager.INSTANCE.onRegistered(IPlayerProperties.class, capability -> PLAYERPROPERTIES_CAP = capability);
   }
}
