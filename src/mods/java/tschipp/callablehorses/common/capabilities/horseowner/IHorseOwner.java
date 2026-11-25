package tschipp.callablehorses.common.capabilities.horseowner;

import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHorseOwner {
   AbstractHorse getHorseEntity(World var1);

   NBTTagCompound getHorseNBT();

   void setHorseNBT(NBTTagCompound var1);

   void setHorse(AbstractHorse var1, EntityPlayer var2);

   void clearHorse();

   int getHorseNum();

   void setHorseNum(int var1);

   String getStorageUUID();

   void setStorageUUID(String var1);

   void setLastSeenPosition(BlockPos var1);

   BlockPos getLastSeenPosition();

   int getLastSeenDim();

   void setLastSeenDim(int var1);
}
