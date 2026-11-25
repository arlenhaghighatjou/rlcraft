package bq_standard.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import javax.annotation.Nonnull;

public interface ITaskInventory extends ITask {
   void onInventoryChange(@Nonnull DBEntry<IQuest> var1, @Nonnull ParticipantInfo var2);
}
