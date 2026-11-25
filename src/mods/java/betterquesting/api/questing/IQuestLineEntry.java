package betterquesting.api.questing;

import betterquesting.api2.storage.INBTSaveLoad;
import net.minecraft.nbt.NBTTagCompound;

public interface IQuestLineEntry extends INBTSaveLoad<NBTTagCompound> {
   /** @deprecated */
   @Deprecated
   int getSize();

   int getSizeX();

   int getSizeY();

   int getPosX();

   int getPosY();

   void setPosition(int var1, int var2);

   /** @deprecated */
   @Deprecated
   void setSize(int var1);

   void setSize(int var1, int var2);
}
