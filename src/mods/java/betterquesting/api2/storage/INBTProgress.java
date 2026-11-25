package betterquesting.api2.storage;

import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;

public interface INBTProgress<T extends NBTBase> {
   T writeProgressToNBT(T var1, @Nullable List<EaglercraftUUID> var2);

   void readProgressFromNBT(T var1, boolean var2);
}
