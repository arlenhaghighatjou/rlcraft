package betterquesting.api.storage;

import betterquesting.api2.storage.INBTPartial;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagList;

public interface INameCache extends INBTPartial<NBTTagList, EaglercraftUUID> {
   boolean updateName(@Nonnull EntityPlayerMP var1);

   String getName(@Nonnull EaglercraftUUID var1);

   EaglercraftUUID getUUID(@Nonnull String var1);

   List<String> getAllNames();

   boolean isOP(@Nonnull EaglercraftUUID var1);

   int size();

   void reset();
}
