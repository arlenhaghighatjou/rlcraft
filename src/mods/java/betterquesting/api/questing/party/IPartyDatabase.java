package betterquesting.api.questing.party;

import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.IDatabase;
import betterquesting.api2.storage.INBTPartial;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagList;

public interface IPartyDatabase extends IDatabase<IParty>, INBTPartial<NBTTagList, Integer> {
   IParty createNew(int var1);

   @Nullable
   DBEntry<IParty> getParty(@Nonnull EaglercraftUUID var1);
}
