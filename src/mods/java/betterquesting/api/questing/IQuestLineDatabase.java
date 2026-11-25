package betterquesting.api.questing;

import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.IDatabase;
import betterquesting.api2.storage.INBTPartial;
import java.util.List;
import net.minecraft.nbt.NBTTagList;

public interface IQuestLineDatabase extends IDatabase<IQuestLine>, INBTPartial<NBTTagList, Integer> {
   IQuestLine createNew(int var1);

   void removeQuest(int var1);

   int getOrderIndex(int var1);

   void setOrderIndex(int var1, int var2);

   List<DBEntry<IQuestLine>> getSortedEntries();
}
