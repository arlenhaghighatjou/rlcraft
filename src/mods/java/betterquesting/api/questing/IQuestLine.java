package betterquesting.api.questing;

import betterquesting.api.properties.IPropertyContainer;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.IDatabase;
import betterquesting.api2.storage.INBTPartial;
import net.minecraft.nbt.NBTTagCompound;

public interface IQuestLine extends IDatabase<IQuestLineEntry>, INBTPartial<NBTTagCompound, Integer>, IPropertyContainer {
   IQuestLineEntry createNew(int var1);

   String getUnlocalisedName();

   String getUnlocalisedDescription();

   DBEntry<IQuestLineEntry> getEntryAt(int var1, int var2);
}
