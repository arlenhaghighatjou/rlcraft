package com.fantasticsource.mctools.nbtcap;

import com.fantasticsource.fantasticlib.api.INBTCap;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class NBTCapStorage implements IStorage<INBTCap> {
   public NBTCapStorage() {
   }

   @Nullable
   public NBTBase writeNBT(Capability<INBTCap> capability, INBTCap instance, EnumFacing side) {
      NBTTagCompound compound = new NBTTagCompound();

      for(String modid : instance.getRegisteredModIDs()) {
         compound.setTag(modid, instance.getCompound(modid));
      }

      return compound;
   }

   public void readNBT(Capability<INBTCap> capability, INBTCap instance, EnumFacing side, NBTBase nbt) {
      NBTTagCompound compound = (NBTTagCompound)nbt;

      for(String modid : compound.getKeySet()) {
         for(String registeredID : instance.getRegisteredModIDs()) {
            if (registeredID.equals(modid)) {
               instance.setCompound(modid, compound.getCompoundTag(modid));
               break;
            }
         }
      }

   }
}
