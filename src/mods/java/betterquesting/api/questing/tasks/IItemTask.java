package betterquesting.api.questing.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api2.storage.DBEntry;
import java.util.UUID;
import net.minecraft.item.ItemStack;

public interface IItemTask extends ITask {
   boolean canAcceptItem(UUID var1, DBEntry<IQuest> var2, ItemStack var3);

   ItemStack submitItem(UUID var1, DBEntry<IQuest> var2, ItemStack var3);
}
