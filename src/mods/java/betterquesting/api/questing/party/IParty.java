package betterquesting.api.questing.party;

import betterquesting.api.enums.EnumPartyStatus;
import betterquesting.api.properties.IPropertyContainer;
import betterquesting.api2.storage.INBTSaveLoad;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;

public interface IParty extends INBTSaveLoad<NBTTagCompound> {
   IPropertyContainer getProperties();

   void kickUser(@Nonnull EaglercraftUUID var1);

   void setStatus(@Nonnull EaglercraftUUID var1, @Nonnull EnumPartyStatus var2);

   @Nullable
   EnumPartyStatus getStatus(@Nonnull EaglercraftUUID var1);

   List<EaglercraftUUID> getMembers();

   NBTTagCompound writeProperties(NBTTagCompound var1);

   void readProperties(NBTTagCompound var1);
}
