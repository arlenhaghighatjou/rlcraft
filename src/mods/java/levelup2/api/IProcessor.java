package levelup2.api;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IProcessor {
   void extraProcessing(EntityPlayer var1);

   void setUUID(EaglercraftUUID var1);

   EntityPlayer getPlayerFromUUID();

   NBTTagCompound writeToNBT(NBTTagCompound var1);

   void readFromNBT(NBTTagCompound var1);
}
