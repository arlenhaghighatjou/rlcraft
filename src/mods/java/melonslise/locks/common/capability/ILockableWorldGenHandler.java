package melonslise.locks.common.capability;

import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.common.util.INBTSerializable;

public interface ILockableWorldGenHandler extends INBTSerializable<NBTTagInt> {
   void setChunkShouldGenerateChests();

   void tryGeneratingLocks();
}
