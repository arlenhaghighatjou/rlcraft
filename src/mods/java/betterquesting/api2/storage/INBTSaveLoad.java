package betterquesting.api2.storage;

import net.minecraft.nbt.NBTBase;

public interface INBTSaveLoad<T extends NBTBase> {
   T writeToNBT(T var1);

   void readFromNBT(T var1);
}
