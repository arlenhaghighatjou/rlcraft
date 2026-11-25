package bq_msi.handlers;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestDatabase;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.storage.IQuestSettings;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_msi.tasks.ITaskTickable;
import bq_msi.tasks.TaskMultiblock;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
   public EventHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onRightClickBlock(RightClickBlock event) {
      if (event.getEntityPlayer() != null && !event.getEntityLiving().world.isRemote && !event.isCanceled()) {
         EntityPlayer player = event.getEntityPlayer();
         ParticipantInfo pInfo = new ParticipantInfo(player);
         List<DBEntry<IQuest>> actQuest = ((IQuestDatabase)QuestingAPI.getAPI(ApiReference.QUEST_DB)).bulkLookup(pInfo.getSharedQuests());
         IBlockState state = player.world.getBlockState(event.getPos());

         for(DBEntry<IQuest> entry : actQuest) {
            for(DBEntry<ITask> task : ((IQuest)entry.getValue()).getTasks().getEntries()) {
               if (task.getValue() instanceof TaskMultiblock) {
                  ((TaskMultiblock)task.getValue()).onInteract(pInfo, entry, event.getHand(), event.getItemStack(), state, event.getPos());
               }
            }
         }

      }
   }

   @SubscribeEvent
   public void onEntityLiving(LivingUpdateEvent event) {
      if (event.getEntityLiving() instanceof EntityPlayer && !event.getEntityLiving().world.isRemote && event.getEntityLiving().ticksExisted % 20 == 0 && !((Boolean)((IQuestSettings)QuestingAPI.getAPI(ApiReference.SETTINGS)).getProperty(NativeProps.EDIT_MODE)).booleanValue()) {
         EntityPlayer player = (EntityPlayer)event.getEntityLiving();
         ParticipantInfo pInfo = new ParticipantInfo(player);

         for(DBEntry<IQuest> entry : ((IQuestDatabase)QuestingAPI.getAPI(ApiReference.QUEST_DB)).bulkLookup(pInfo.getSharedQuests())) {
            for(DBEntry<ITask> task : ((IQuest)entry.getValue()).getTasks().getEntries()) {
               if (task.getValue() instanceof ITaskTickable) {
                  ((ITaskTickable)task.getValue()).tickTask(pInfo, entry);
               }
            }
         }

      }
   }
}
