package com.teamacronymcoders.base.capability;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.INBTSerializable;

public class NBTCapStorage<T extends INBTSerializable<NBTTagCompound>> implements IStorage<T> {
   public NBTCapStorage() {
   }

   @Nullable
   public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
      return instance.serializeNBT();
   }

   public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
      if (nbt instanceof NBTTagCompound) {
         instance.deserializeNBT((NBTTagCompound)nbt);
      }

   }
}
