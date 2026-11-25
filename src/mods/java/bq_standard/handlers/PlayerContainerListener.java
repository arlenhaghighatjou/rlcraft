package bq_standard.handlers;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestDatabase;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_standard.tasks.ITaskInventory;
import java.util.HashMap;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class PlayerContainerListener implements IContainerListener {
   private static final HashMap<UUID, PlayerContainerListener> LISTEN_MAP = new HashMap();
   private EntityPlayer player;

   static void refreshListener(@Nonnull EntityPlayer player) {
      UUID uuid = QuestingAPI.getQuestingUUID(player);
      PlayerContainerListener listener = (PlayerContainerListener)LISTEN_MAP.get(uuid);
      if (listener != null) {
         listener.player = player;
      } else {
         listener = new PlayerContainerListener(player);
         LISTEN_MAP.put(uuid, listener);
      }

      try {
         player.inventoryContainer.addListener(listener);
      } catch (Exception var4) {
         ;
      }

   }

   private PlayerContainerListener(@Nonnull EntityPlayer player) {
      this.player = player;
   }

   public void sendAllContents(@Nonnull Container container, @Nonnull NonNullList<ItemStack> nonNullList) {
      this.updateTasks();
   }

   public void sendSlotContents(@Nonnull Container container, int i, @Nonnull ItemStack itemStack) {
      this.updateTasks();
   }

   public void sendWindowProperty(@Nonnull Container container, int i, int i1) {
   }

   public void sendAllWindowProperties(@Nonnull Container container, @Nonnull IInventory iInventory) {
   }

   private void updateTasks() {
      ParticipantInfo pInfo = new ParticipantInfo(this.player);

      for(DBEntry<IQuest> entry : ((IQuestDatabase)QuestingAPI.getAPI(ApiReference.QUEST_DB)).bulkLookup(pInfo.getSharedQuests())) {
         for(DBEntry<ITask> task : ((IQuest)entry.getValue()).getTasks().getEntries()) {
            if (task.getValue() instanceof ITaskInventory) {
               ((ITaskInventory)task.getValue()).onInventoryChange(entry, pInfo);
            }
         }
      }

   }
}
