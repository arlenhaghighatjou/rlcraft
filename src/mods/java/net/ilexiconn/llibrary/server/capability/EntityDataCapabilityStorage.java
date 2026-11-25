package net.ilexiconn.llibrary.server.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class EntityDataCapabilityStorage implements IStorage<IEntityDataCapability> {
   public EntityDataCapabilityStorage() {
   }

   public NBTBase writeNBT(Capability<IEntityDataCapability> capability, IEntityDataCapability instance, EnumFacing side) {
      NBTTagCompound compound = new NBTTagCompound();
      instance.saveToNBT(compound);
      return compound;
   }

   public void readNBT(Capability<IEntityDataCapability> capability, IEntityDataCapability instance, EnumFacing side, NBTBase nbt) {
      instance.loadFromNBT((NBTTagCompound)nbt);
   }
}
