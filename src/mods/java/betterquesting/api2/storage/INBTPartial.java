package betterquesting.api2.storage;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;

public interface INBTPartial<T extends NBTBase, K> {
   T writeToNBT(T var1, @Nullable List<K> var2);

   void readFromNBT(T var1, boolean var2);
}
