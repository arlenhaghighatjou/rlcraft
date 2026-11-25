package svenhjol.meson.iface;

import net.minecraft.nbt.NBTTagCompound;

public interface IMesonTile {
   String getModId();

   void writeTag(NBTTagCompound var1);

   void readTag(NBTTagCompound var1);
}
