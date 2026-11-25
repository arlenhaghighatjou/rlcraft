package net.ilexiconn.llibrary.server.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface IWorldDataAdapter {
   String getID();

   void loadNBTData(NBTTagCompound var1, World var2);

   void saveNBTData(NBTTagCompound var1, World var2);
}
