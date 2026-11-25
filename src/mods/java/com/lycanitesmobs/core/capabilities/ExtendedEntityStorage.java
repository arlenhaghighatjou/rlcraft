package com.lycanitesmobs.core.capabilities;

import com.lycanitesmobs.core.entity.ExtendedEntity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class ExtendedEntityStorage implements IStorage<ExtendedEntity> {
   public ExtendedEntityStorage() {
   }

   public void readNBT(Capability<ExtendedEntity> capability, ExtendedEntity instance, EnumFacing facing, NBTBase nbt) {
      if (instance instanceof ExtendedEntity && nbt instanceof NBTTagCompound) {
         NBTTagCompound extTagCompound = (NBTTagCompound)nbt;
         instance.readNBT(extTagCompound);
      }
   }

   public NBTBase writeNBT(Capability<ExtendedEntity> capability, ExtendedEntity instance, EnumFacing facing) {
      if (!(instance instanceof ExtendedEntity)) {
         return null;
      } else {
         NBTTagCompound extTagCompound = new NBTTagCompound();
         instance.writeNBT(extTagCompound);
         return extTagCompound;
      }
   }
}
