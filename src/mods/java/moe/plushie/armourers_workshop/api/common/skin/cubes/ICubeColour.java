package moe.plushie.armourers_workshop.api.common.skin.cubes;

import net.minecraft.nbt.NBTTagCompound;

public interface ICubeColour {
   byte getRed(int var1);

   byte getGreen(int var1);

   byte getBlue(int var1);

   byte getPaintType(int var1);

   byte[] getRed();

   byte[] getGreen();

   byte[] getBlue();

   byte[] getPaintType();

   void setColour(int var1, int var2);

   /** @deprecated */
   @Deprecated
   void setColour(int var1);

   void setRed(byte var1, int var2);

   void setGreen(byte var1, int var2);

   void setBlue(byte var1, int var2);

   void setPaintType(byte var1, int var2);

   void readFromNBT(NBTTagCompound var1);

   void writeToNBT(NBTTagCompound var1);
}
