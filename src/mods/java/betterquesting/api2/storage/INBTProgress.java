package betterquesting.api2.storage;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;

public interface INBTProgress<T extends NBTBase> {
   T writeProgressToNBT(T var1, @Nullable List<UUID> var2);

   void readProgressFromNBT(T var1, boolean var2);
}
