package betterquesting.api.storage;

import betterquesting.api2.storage.INBTPartial;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagList;

public interface INameCache extends INBTPartial<NBTTagList, UUID> {
   boolean updateName(@Nonnull EntityPlayerMP var1);

   String getName(@Nonnull UUID var1);

   UUID getUUID(@Nonnull String var1);

   List<String> getAllNames();

   boolean isOP(@Nonnull UUID var1);

   int size();

   void reset();
}
