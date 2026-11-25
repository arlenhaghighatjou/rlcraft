package ivorius.ivtoolkit.tools;

import net.minecraft.nbt.NBTTagCompound;

public interface NBTCompoundObject {
   void readFromNBT(NBTTagCompound var1);

   void writeToNBT(NBTTagCompound var1);
}
