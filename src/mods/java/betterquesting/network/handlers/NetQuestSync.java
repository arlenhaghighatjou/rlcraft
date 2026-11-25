package betterquesting.network.handlers;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.events.DatabaseEvent;
import betterquesting.api.network.QuestingPacket;
import betterquesting.api.questing.IQuest;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.BQThreadedIO;
import betterquesting.core.BetterQuesting;
import betterquesting.network.PacketSender;
import betterquesting.network.PacketTypeRegistry;
import betterquesting.questing.QuestDatabase;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NetQuestSync {
   private static final ResourceLocation ID_NAME = new ResourceLocation("betterquesting:quest_sync");

   public NetQuestSync() {
   }

   public static void registerHandler() {
      PacketTypeRegistry.INSTANCE.registerServerHandler(ID_NAME, NetQuestSync::onServer);
      if (BetterQuesting.proxy.isClient()) {
         PacketTypeRegistry.INSTANCE.registerClientHandler(ID_NAME, NetQuestSync::onClient);
      }

   }

   public static void quickSync(int questID, boolean config, boolean progress) {
      if (config || progress) {
         int[] IDs = questID < 0 ? null : new int[]{questID};
         if (config) {
            sendSync((EntityPlayerMP)null, IDs, true, false);
         }

         if (progress) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server == null) {
               return;
            }

            for(EntityPlayerMP player : server.getPlayerList().getPlayers()) {
               sendSync(player, IDs, false, true);
            }
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public static void sendSync(@Nullable EntityPlayerMP player, @Nullable int[] questIDs, boolean config, boolean progress) {
      sendSync(player, questIDs, (int[])null, config, progress);
   }

   public static void sendSync(@Nullable EntityPlayerMP player, @Nullable int[] questIDs, @Nullable int[] resetIDs, boolean config, boolean progress) {
      if ((config || progress) && (questIDs == null || questIDs.length > 0)) {
         BQThreadedIO.INSTANCE.enqueue(() -> {
            NBTTagList dataList = new NBTTagList();
            List<DBEntry<IQuest>> questSubset = questIDs == null ? QuestDatabase.INSTANCE.getEntries() : QuestDatabase.INSTANCE.bulkLookup(questIDs);
            List<UUID> pidList = player == null ? null : Collections.singletonList(QuestingAPI.getQuestingUUID(player));

            for(DBEntry<IQuest> entry : questSubset) {
               NBTTagCompound tag = new NBTTagCompound();
               if (config) {
                  tag.setTag("config", ((IQuest)entry.getValue()).writeToNBT(new NBTTagCompound()));
               }

               if (progress) {
                  tag.setTag("progress", ((IQuest)entry.getValue()).writeProgressToNBT(new NBTTagCompound(), pidList));
               }

               if (resetIDs != null) {
                  tag.setIntArray("resets", resetIDs);
               }

               tag.setInteger("questID", entry.getID());
               dataList.appendTag(tag);
            }

            NBTTagCompound payload = new NBTTagCompound();
            payload.setBoolean("merge", !config || questIDs != null);
            payload.setTag("data", dataList);
            if (player == null) {
               PacketSender.INSTANCE.sendToAll(new QuestingPacket(ID_NAME, payload));
            } else {
               PacketSender.INSTANCE.sendToPlayers(new QuestingPacket(ID_NAME, payload), player);
            }

         });
      }
   }

   @SideOnly(Side.CLIENT)
   public static void requestSync(@Nullable int[] questIDs, boolean configs, boolean progress) {
      NBTTagCompound payload = new NBTTagCompound();
      if (questIDs != null) {
         payload.setIntArray("requestIDs", questIDs);
      }

      payload.setBoolean("getConfig", configs);
      payload.setBoolean("getProgress", progress);
      PacketSender.INSTANCE.sendToServer(new QuestingPacket(ID_NAME, payload));
   }

   private static void onServer(Tuple<NBTTagCompound, EntityPlayerMP> message) {
      NBTTagCompound payload = (NBTTagCompound)message.getFirst();
      int[] reqIDs = !payload.hasKey("requestIDs", 11) ? null : payload.getIntArray("requestIDs");
      sendSync((EntityPlayerMP)message.getSecond(), reqIDs, payload.getBoolean("getConfig"), payload.getBoolean("getProgress"));
   }

   @SideOnly(Side.CLIENT)
   private static void onClient(NBTTagCompound message) {
      NBTTagList data = message.getTagList("data", 10);
      boolean merge = message.getBoolean("merge");
      if (!merge) {
         QuestDatabase.INSTANCE.reset();
      }

      for(int i = 0; i < data.tagCount(); ++i) {
         NBTTagCompound tag = data.getCompoundTagAt(i);
         if (tag.hasKey("questID", 99)) {
            int questID = tag.getInteger("questID");
            IQuest quest = (IQuest)QuestDatabase.INSTANCE.getValue(questID);
            if (tag.hasKey("config", 10)) {
               if (quest == null) {
                  quest = QuestDatabase.INSTANCE.createNew(questID);
               }

               quest.readFromNBT(tag.getCompoundTag("config"));
            }

            if (tag.hasKey("progress", 10) && quest != null) {
               quest.readProgressFromNBT(tag.getCompoundTag("progress"), merge || Minecraft.getMinecraft().isIntegratedServerRunning());
            }
         }
      }

      MinecraftForge.EVENT_BUS.post(new DatabaseEvent.Update(DatabaseEvent.DBType.QUEST));
   }
}
