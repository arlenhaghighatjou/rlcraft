package xzeroair.trinkets.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class CapabilityStorage<T extends CapabilityBase> implements IStorage<T> {
   public CapabilityStorage() {
   }

   public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
      return instance == null ? null : instance.saveToNBT(instance.getTag());
   }

   public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
      if (instance != null && nbt != null) {
         instance.loadFromNBT((NBTTagCompound)nbt);
      }
   }
}
