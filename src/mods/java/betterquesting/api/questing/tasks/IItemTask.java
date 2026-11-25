package betterquesting.api.questing.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api2.storage.DBEntry;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.item.ItemStack;

public interface IItemTask extends ITask {
   boolean canAcceptItem(EaglercraftUUID var1, DBEntry<IQuest> var2, ItemStack var3);

   ItemStack submitItem(EaglercraftUUID var1, DBEntry<IQuest> var2, ItemStack var3);
}
