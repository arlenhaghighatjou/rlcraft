package com.charles445.rltweaker.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class TweakerStorage implements IStorage<ITweakerCapability> {
   public TweakerStorage() {
   }

   public NBTBase writeNBT(Capability<ITweakerCapability> capability, ITweakerCapability instance, EnumFacing side) {
      new NBTTagCompound();
      return new NBTTagCompound();
   }

   public void readNBT(Capability<ITweakerCapability> capability, ITweakerCapability instance, EnumFacing side, NBTBase nbt) {
      if (nbt instanceof NBTTagCompound) {
         NBTTagCompound var5 = (NBTTagCompound)nbt;
      }

   }
}
