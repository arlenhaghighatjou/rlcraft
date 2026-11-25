package tschipp.callablehorses.common.capabilities.storedhorse;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class HorseStorage implements IStorage<IStoredHorse> {
   public HorseStorage() {
   }

   public NBTBase writeNBT(Capability<IStoredHorse> capability, IStoredHorse instance, EnumFacing side) {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setString("owner", instance.getOwnerUUID());
      tag.setInteger("horseNum", instance.getHorseNum());
      tag.setString("storage", instance.getStorageUUID());
      tag.setBoolean("owned", instance.isOwned());
      return tag;
   }

   public void readNBT(Capability<IStoredHorse> capability, IStoredHorse instance, EnumFacing side, NBTBase nbt) {
      NBTTagCompound tag = (NBTTagCompound)nbt;
      instance.setOwnerUUID(tag.getString("owner"));
      instance.setHorseNum(tag.getInteger("horseNum"));
      instance.setStorageUUID(tag.getString("storage"));
      instance.setOwned(tag.getBoolean("owned"));
   }
}
