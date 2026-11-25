package betterquesting.network.handlers;

import betterquesting.api.events.DatabaseEvent;
import betterquesting.api.network.QuestingPacket;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.BQThreadedIO;
import betterquesting.core.BetterQuesting;
import betterquesting.network.PacketSender;
import betterquesting.network.PacketTypeRegistry;
import betterquesting.questing.QuestLineDatabase;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NetChapterSync {
   private static final ResourceLocation ID_NAME = new ResourceLocation("betterquesting:chapter_sync");

   public NetChapterSync() {
   }

   public static void registerHandler() {
      PacketTypeRegistry.INSTANCE.registerServerHandler(ID_NAME, NetChapterSync::onServer);
      if (BetterQuesting.proxy.isClient()) {
         PacketTypeRegistry.INSTANCE.registerClientHandler(ID_NAME, NetChapterSync::onClient);
      }

   }

   public static void sendSync(@Nullable EntityPlayerMP player, @Nullable int[] chapterIDs) {
      if (chapterIDs == null || chapterIDs.length > 0) {
         BQThreadedIO.INSTANCE.enqueue(() -> {
            NBTTagList data = new NBTTagList();

            for(DBEntry<IQuestLine> chapter : chapterIDs == null ? QuestLineDatabase.INSTANCE.getEntries() : QuestLineDatabase.INSTANCE.bulkLookup(chapterIDs)) {
               NBTTagCompound entry = new NBTTagCompound();
               entry.setInteger("chapterID", chapter.getID());
               entry.setTag("config", ((IQuestLine)chapter.getValue()).writeToNBT(new NBTTagCompound(), (List)null));
               data.appendTag(entry);
            }

            List<DBEntry<IQuestLine>> allSort = QuestLineDatabase.INSTANCE.getSortedEntries();
            int[] aryOrder = new int[allSort.size()];

            for(int i = 0; i < aryOrder.length; ++i) {
               aryOrder[i] = ((DBEntry)allSort.get(i)).getID();
            }

            NBTTagCompound payload = new NBTTagCompound();
            payload.setBoolean("merge", chapterIDs != null);
            payload.setTag("data", data);
            payload.setIntArray("order", aryOrder);
            if (player == null) {
               PacketSender.INSTANCE.sendToAll(new QuestingPacket(ID_NAME, payload));
            } else {
               PacketSender.INSTANCE.sendToPlayers(new QuestingPacket(ID_NAME, payload), player);
            }

         });
      }
   }

   @SideOnly(Side.CLIENT)
   public static void requestSync(@Nullable int[] chapterIDs) {
      NBTTagCompound payload = new NBTTagCompound();
      if (chapterIDs != null) {
         payload.setIntArray("requestIDs", chapterIDs);
      }

      PacketSender.INSTANCE.sendToServer(new QuestingPacket(ID_NAME, payload));
   }

   private static void onServer(Tuple<NBTTagCompound, EntityPlayerMP> message) {
      NBTTagCompound payload = (NBTTagCompound)message.getFirst();
      int[] reqIDs = !payload.hasKey("requestIDs") ? null : payload.getIntArray("requestIDs");
      sendSync((EntityPlayerMP)message.getSecond(), reqIDs);
   }

   @SideOnly(Side.CLIENT)
   private static void onClient(NBTTagCompound message) {
      NBTTagList data = message.getTagList("data", 10);
      if (!message.getBoolean("merge")) {
         QuestLineDatabase.INSTANCE.reset();
      }

      for(int i = 0; i < data.tagCount(); ++i) {
         NBTTagCompound tag = data.getCompoundTagAt(i);
         if (tag.hasKey("chapterID", 99)) {
            int chapterID = tag.getInteger("chapterID");
            IQuestLine chapter = (IQuestLine)QuestLineDatabase.INSTANCE.getValue(chapterID);
            if (chapter == null) {
               chapter = QuestLineDatabase.INSTANCE.createNew(chapterID);
            }

            chapter.readFromNBT(tag.getCompoundTag("config"), false);
         }
      }

      int[] aryOrder = message.getIntArray("order");

      for(int i = 0; i < aryOrder.length; ++i) {
         QuestLineDatabase.INSTANCE.setOrderIndex(aryOrder[i], i);
      }

      MinecraftForge.EVENT_BUS.post(new DatabaseEvent.Update(DatabaseEvent.DBType.CHAPTER));
   }
}
