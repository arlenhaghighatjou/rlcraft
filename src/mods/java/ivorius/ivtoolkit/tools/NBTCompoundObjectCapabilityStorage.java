package ivorius.ivtoolkit.tools;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class NBTCompoundObjectCapabilityStorage<T extends NBTCompoundObject> implements IStorage<T> {
   public Class<T> tClass;

   public NBTCompoundObjectCapabilityStorage(Class<T> tClass) {
      this.tClass = tClass;
   }

   public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
      return NBTCompoundObjects.write(instance);
   }

   public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
      instance.readFromNBT(nbt instanceof NBTTagCompound ? (NBTTagCompound)nbt : new NBTTagCompound());
   }
}
