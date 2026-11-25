package cursedflames.bountifulbaubles.util;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTSavable {
   NBTTagCompound writeToNBT();

   Object readFromNBT(NBTTagCompound var1);
}
