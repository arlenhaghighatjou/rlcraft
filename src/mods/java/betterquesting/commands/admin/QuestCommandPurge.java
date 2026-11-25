package betterquesting.commands.admin;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.questing.IQuestLineEntry;
import betterquesting.api2.storage.DBEntry;
import betterquesting.commands.QuestCommandBase;
import betterquesting.network.handlers.NetQuestEdit;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.QuestLineDatabase;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class QuestCommandPurge extends QuestCommandBase {
   public QuestCommandPurge() {
   }

   public String getCommand() {
      return "purge_hidden_quests";
   }

   public void runCommand(MinecraftServer server, CommandBase command, ICommandSender sender, String[] args) {
      TreeSet<Integer> knownKeys = new TreeSet();

      for(DBEntry<IQuestLine> entry : QuestLineDatabase.INSTANCE.getEntries()) {
         for(DBEntry<IQuestLineEntry> qle : ((IQuestLine)entry.getValue()).getEntries()) {
            knownKeys.add(Integer.valueOf(qle.getID()));
         }
      }

      Iterator<Integer> keyIterator = knownKeys.iterator();
      List<Integer> removeQueue = new ArrayList();
      int n = -1;
      Iterator var15 = QuestDatabase.INSTANCE.getEntries().iterator();

      while(var15.hasNext()) {
         DBEntry<IQuest> entry;
         for(entry = (DBEntry)var15.next(); n < entry.getID() && keyIterator.hasNext(); n = ((Integer)keyIterator.next()).intValue()) {
            ;
         }

         if (n != entry.getID()) {
            removeQueue.add(Integer.valueOf(entry.getID()));
         }
      }

      int removed = removeQueue.size();
      int[] bulkIDs = new int[removeQueue.size()];

      for(int var14 = 0; var14 < bulkIDs.length; ++var14) {
         bulkIDs[var14] = ((Integer)removeQueue.get(var14)).intValue();
      }

      NetQuestEdit.deleteQuests(bulkIDs);
      sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.purge_hidden", new Object[]{removed}));
   }

   public String getPermissionNode() {
      return "betterquesting.command.admin.purge";
   }

   public DefaultPermissionLevel getPermissionLevel() {
      return DefaultPermissionLevel.OP;
   }

   public String getPermissionDescription() {
      return "Permission to purge all hidden quests and progression data however it does not delete any in new world defaults";
   }
}
