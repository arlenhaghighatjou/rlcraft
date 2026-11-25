package betterquesting.api.questing.party;

import betterquesting.api.enums.EnumPartyStatus;
import betterquesting.api.properties.IPropertyContainer;
import betterquesting.api2.storage.INBTSaveLoad;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;

public interface IParty extends INBTSaveLoad<NBTTagCompound> {
   IPropertyContainer getProperties();

   void kickUser(@Nonnull UUID var1);

   void setStatus(@Nonnull UUID var1, @Nonnull EnumPartyStatus var2);

   @Nullable
   EnumPartyStatus getStatus(@Nonnull UUID var1);

   List<UUID> getMembers();

   NBTTagCompound writeProperties(NBTTagCompound var1);

   void readProperties(NBTTagCompound var1);
}
