package com.lycanitesmobs.core.capabilities;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class ExtendedPlayerStorage implements IStorage<ExtendedPlayer> {
   public ExtendedPlayerStorage() {
   }

   public void readNBT(Capability<ExtendedPlayer> capability, ExtendedPlayer instance, EnumFacing facing, NBTBase nbt) {
      if (instance instanceof ExtendedPlayer && nbt instanceof NBTTagCompound) {
         NBTTagCompound extTagCompound = (NBTTagCompound)nbt;
         instance.readNBT(extTagCompound);
      }
   }

   public NBTBase writeNBT(Capability<ExtendedPlayer> capability, ExtendedPlayer instance, EnumFacing facing) {
      if (!(instance instanceof ExtendedPlayer)) {
         return null;
      } else {
         NBTTagCompound extTagCompound = new NBTTagCompound();
         instance.writeNBT(extTagCompound);
         return extTagCompound;
      }
   }
}
