package melonslise.locks.common.capability;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import melonslise.locks.common.util.Lockable;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

public interface ILockableStorage extends INBTSerializable<NBTTagList> {
   Int2ObjectMap<Lockable> get();

   void add(Lockable var1);

   void remove(int var1);
}
