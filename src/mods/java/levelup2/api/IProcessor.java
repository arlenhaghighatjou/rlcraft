package levelup2.api;

import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IProcessor {
   void extraProcessing(EntityPlayer var1);

   void setUUID(UUID var1);

   EntityPlayer getPlayerFromUUID();

   NBTTagCompound writeToNBT(NBTTagCompound var1);

   void readFromNBT(NBTTagCompound var1);
}
