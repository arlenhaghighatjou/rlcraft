package betterquesting.network.handlers;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.enums.EnumLogic;
import betterquesting.api.events.DatabaseEvent;
import betterquesting.api.network.QuestingPacket;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.storage.DBEntry;
import betterquesting.core.BetterQuesting;
import betterquesting.handlers.SaveLoadHandler;
import betterquesting.network.PacketSender;
import betterquesting.network.PacketTypeRegistry;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.QuestLineDatabase;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class NetQuestEdit {
   private static final ResourceLocation ID_NAME = new ResourceLocation("betterquesting:quest_edit");

   public NetQuestEdit() {
   }

   public static void registerHandler() {
      PacketTypeRegistry.INSTANCE.registerServerHandler(ID_NAME, NetQuestEdit::onServer);
      if (BetterQuesting.proxy.isClient()) {
         PacketTypeRegistry.INSTANCE.registerClientHandler(ID_NAME, NetQuestEdit::onClient);
      }

   }

   @SideOnly(Side.CLIENT)
   public static void sendEdit(NBTTagCompound payload) {
      PacketSender.INSTANCE.sendToServer(new QuestingPacket(ID_NAME, payload));
   }

   private static void onServer(Tuple<NBTTagCompound, EntityPlayerMP> message) {
      EntityPlayerMP sender = (EntityPlayerMP)message.getSecond();
      MinecraftServer server = sender.getServer();
      if (server != null) {
         boolean isOP = server.getPlayerList().canSendCommands(sender.getGameProfile());
         if (!isOP) {
            BetterQuesting.logger.log(Level.WARN, "Player " + sender.getName() + " (EaglercraftUUID:" + QuestingAPI.getQuestingUUID(sender) + ") tried to edit quests without OP permissions!");
            sender.sendStatusMessage(new TextComponentString(TextFormatting.RED + "You need to be OP to edit quests!"), true);
         } else {
            NBTTagCompound tag = (NBTTagCompound)message.getFirst();
            EaglercraftUUID senderID = QuestingAPI.getQuestingUUID(sender);
            int action = !((NBTTagCompound)message.getFirst()).hasKey("action", 99) ? -1 : ((NBTTagCompound)message.getFirst()).getInteger("action");
            switch(action) {
            case 0:
               editQuests(tag.getTagList("data", 10));
               break;
            case 1:
               deleteQuests(tag.getIntArray("questIDs"));
               break;
            case 2:
               setQuestStates(tag.getIntArray("questIDs"), tag.getBoolean("state"), senderID);
               break;
            case 3:
               createQuests(tag.getTagList("data", 10));
               break;
            default:
               BetterQuesting.logger.log(Level.ERROR, "Invalid quest edit action '" + action + "'. Full payload:\n" + ((NBTTagCompound)message.getFirst()).toString());
            }

         }
      }
   }

   public static void editQuests(NBTTagList data) {
      int[] ids = new int[data.tagCount()];

      for(int i = 0; i < data.tagCount(); ++i) {
         NBTTagCompound entry = data.getCompoundTagAt(i);
         int questID = entry.getInteger("questID");
         ids[i] = questID;
         IQuest quest = (IQuest)QuestDatabase.INSTANCE.getValue(questID);
         if (quest != null) {
            quest.readFromNBT(entry.getCompoundTag("config"));
         }
      }

      SaveLoadHandler.INSTANCE.markDirty();
      NetQuestSync.sendSync((EntityPlayerMP)null, ids, true, false);
   }

   public static void deleteQuests(int[] questIDs) {
      for(int id : questIDs) {
         QuestDatabase.INSTANCE.removeID(id);
         QuestLineDatabase.INSTANCE.removeQuest(id);
      }

      SaveLoadHandler.INSTANCE.markDirty();
      NBTTagCompound payload = new NBTTagCompound();
      payload.setIntArray("questIDs", questIDs);
      payload.setInteger("action", 1);
      PacketSender.INSTANCE.sendToAll(new QuestingPacket(ID_NAME, payload));
   }

   public static void setQuestStates(int[] questIDs, boolean state, EaglercraftUUID targetID) {
      for(DBEntry<IQuest> entry : QuestDatabase.INSTANCE.bulkLookup(questIDs)) {
         if (!state) {
            ((IQuest)entry.getValue()).resetUser(targetID, true);
         } else if (((IQuest)entry.getValue()).isComplete(targetID)) {
            ((IQuest)entry.getValue()).setClaimed(targetID, 0L);
         } else {
            ((IQuest)entry.getValue()).setComplete(targetID, 0L);
            int done = 0;
            if (!((EnumLogic)((IQuest)entry.getValue()).getProperty(NativeProps.LOGIC_TASK)).getResult(done, ((IQuest)entry.getValue()).getTasks().size())) {
               for(DBEntry<ITask> task : ((IQuest)entry.getValue()).getTasks().getEntries()) {
                  ((ITask)task.getValue()).setComplete(targetID);
                  ++done;
                  if (((EnumLogic)((IQuest)entry.getValue()).getProperty(NativeProps.LOGIC_TASK)).getResult(done, ((IQuest)entry.getValue()).getTasks().size())) {
                     break;
                  }
               }
            }
         }
      }

      SaveLoadHandler.INSTANCE.markDirty();
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      if (server != null) {
         EntityPlayerMP player = server.getPlayerList().getPlayerByUUID(targetID);
         if (player != null) {
            NetQuestSync.sendSync(player, questIDs, false, true);
         }
      }
   }

   public static void createQuests(NBTTagList data) {
      int[] ids = new int[data.tagCount()];

      for(int i = 0; i < data.tagCount(); ++i) {
         NBTTagCompound entry = data.getCompoundTagAt(i);
         int questID = entry.hasKey("questID", 99) ? entry.getInteger("questID") : -1;
         if (questID < 0) {
            questID = QuestDatabase.INSTANCE.nextID();
         }

         ids[i] = questID;
         IQuest quest = (IQuest)QuestDatabase.INSTANCE.getValue(questID);
         if (quest == null) {
            quest = QuestDatabase.INSTANCE.createNew(questID);
         }

         if (entry.hasKey("config", 10)) {
            quest.readFromNBT(entry.getCompoundTag("config"));
         }
      }

      SaveLoadHandler.INSTANCE.markDirty();
      NetQuestSync.sendSync((EntityPlayerMP)null, ids, true, false);
   }

   @SideOnly(Side.CLIENT)
   private static void onClient(NBTTagCompound message) {
      int action = !message.hasKey("action", 99) ? -1 : message.getInteger("action");
      if (action == 1) {
         for(int id : message.getIntArray("questIDs")) {
            QuestDatabase.INSTANCE.removeID(id);
            QuestLineDatabase.INSTANCE.removeQuest(id);
         }

         MinecraftForge.EVENT_BUS.post(new DatabaseEvent.Update(DatabaseEvent.DBType.CHAPTER));
      }

   }
}
