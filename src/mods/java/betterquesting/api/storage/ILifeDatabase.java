package betterquesting.api.storage;

import betterquesting.api2.storage.INBTPartial;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;

public interface ILifeDatabase extends INBTPartial<NBTTagCompound, EaglercraftUUID> {
   int getLives(@Nonnull EaglercraftUUID var1);

   void setLives(@Nonnull EaglercraftUUID var1, int var2);

   void reset();
}
