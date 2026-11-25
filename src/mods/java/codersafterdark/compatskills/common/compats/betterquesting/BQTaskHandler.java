package codersafterdark.compatskills.common.compats.betterquesting;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestDatabase;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.storage.IQuestSettings;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import codersafterdark.reskillable.api.event.CacheInvalidatedEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BQTaskHandler {
   public BQTaskHandler() {
   }

   @SubscribeEvent
   public void onCacheInvalidate(CacheInvalidatedEvent event) {
      EntityPlayer player = event.getPlayer();
      if (!player.world.isRemote) {
         ParticipantInfo pInfo = new ParticipantInfo(player);

         for(DBEntry<IQuest> entry : ((IQuestDatabase)QuestingAPI.getAPI(ApiReference.QUEST_DB)).bulkLookup(pInfo.getSharedQuests())) {
            for(DBEntry<ITask> task : ((IQuest)entry.getValue()).getTasks().getEntries()) {
               if (task.getValue() instanceof TaskRequirement) {
                  ((ITask)task.getValue()).detect(pInfo, entry);
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onEntityLiving(LivingUpdateEvent event) {
      EntityLivingBase entityLiving = event.getEntityLiving();
      if (entityLiving instanceof EntityPlayer && !entityLiving.world.isRemote && entityLiving.ticksExisted % 100 == 0 && !((Boolean)((IQuestSettings)QuestingAPI.getAPI(ApiReference.SETTINGS)).getProperty(NativeProps.EDIT_MODE)).booleanValue()) {
         ParticipantInfo pInfo = new ParticipantInfo((EntityPlayer)entityLiving);

         for(DBEntry<IQuest> entry : ((IQuestDatabase)QuestingAPI.getAPI(ApiReference.QUEST_DB)).bulkLookup(pInfo.getSharedQuests())) {
            for(DBEntry<ITask> task : ((IQuest)entry.getValue()).getTasks().getEntries()) {
               if (task.getValue() instanceof TaskRequirement && ((TaskRequirement)task.getValue()).hasUncacheable()) {
                  ((ITask)task.getValue()).detect(pInfo, entry);
               }
            }
         }
      }

   }
}
