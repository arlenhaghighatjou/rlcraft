package betterquesting.api.storage;

import betterquesting.api2.storage.INBTPartial;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;

public interface ILifeDatabase extends INBTPartial<NBTTagCompound, UUID> {
   int getLives(@Nonnull UUID var1);

   void setLives(@Nonnull UUID var1, int var2);

   void reset();
}
