package bq_standard.network.handlers;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.network.IPacketRegistry;
import betterquesting.api.network.IPacketSender;
import betterquesting.api.network.QuestingPacket;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestDatabase;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.cache.CapabilityProviderQuestCache;
import betterquesting.api2.cache.QuestCache;
import bq_standard.tasks.TaskCheckbox;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NetTaskCheckbox {
   private static final ResourceLocation ID_NAME = new ResourceLocation("bq_standard:task_checkbox");

   public NetTaskCheckbox() {
   }

   public static void registerHandler() {
      ((IPacketRegistry)QuestingAPI.getAPI(ApiReference.PACKET_REG)).registerServerHandler(ID_NAME, NetTaskCheckbox::onServer);
   }

   @SideOnly(Side.CLIENT)
   public static void requestClick(int questID, int taskID) {
      NBTTagCompound payload = new NBTTagCompound();
      payload.setInteger("questID", questID);
      payload.setInteger("taskID", taskID);
      ((IPacketSender)QuestingAPI.getAPI(ApiReference.PACKET_SENDER)).sendToServer(new QuestingPacket(ID_NAME, payload));
   }

   private static void onServer(Tuple<NBTTagCompound, EntityPlayerMP> message) {
      NBTTagCompound data = (NBTTagCompound)message.getFirst();
      EntityPlayerMP sender = (EntityPlayerMP)message.getSecond();
      int qId = !data.hasKey("questID", 99) ? -1 : data.getInteger("questID");
      int tId = !data.hasKey("taskID", 99) ? -1 : data.getInteger("taskID");
      if (qId >= 0 && tId >= 0) {
         QuestCache qc = (QuestCache)sender.getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, (EnumFacing)null);
         IQuest quest = (IQuest)((IQuestDatabase)QuestingAPI.getAPI(ApiReference.QUEST_DB)).getValue(qId);
         ITask task = quest == null ? null : (ITask)quest.getTasks().getValue(tId);
         if (task instanceof TaskCheckbox) {
            task.setComplete(QuestingAPI.getQuestingUUID(sender));
            if (qc != null) {
               qc.markQuestDirty(qId);
            }
         }
      }

   }
}
