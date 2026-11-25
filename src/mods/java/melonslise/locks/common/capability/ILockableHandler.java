package melonslise.locks.common.capability;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Observer;
import melonslise.locks.common.util.Lockable;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

public interface ILockableHandler extends INBTSerializable<NBTTagInt>, Observer {
   int nextId();

   Int2ObjectMap<Lockable> getLoaded();

   Int2ObjectMap<Lockable> getInChunk(BlockPos var1);

   boolean add(Lockable var1);

   boolean remove(int var1);
}
