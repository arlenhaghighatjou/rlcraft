package betterquesting.commands.admin;

import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api2.storage.DBEntry;
import betterquesting.commands.QuestCommandBase;
import betterquesting.handlers.SaveLoadHandler;
import betterquesting.network.handlers.NetChapterSync;
import betterquesting.network.handlers.NetQuestEdit;
import betterquesting.network.handlers.NetQuestSync;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.QuestLineDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class QuestCommandDelete extends QuestCommandBase {
   public QuestCommandDelete() {
   }

   public String getUsageSuffix() {
      return "[all|<quest_id>]";
   }

   public boolean validArgs(String[] args) {
      return args.length == 2;
   }

   public List<String> autoComplete(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length != 2) {
         return Collections.emptyList();
      } else {
         List<String> list = new ArrayList();
         list.add("all");

         for(DBEntry<IQuest> i : QuestDatabase.INSTANCE.getEntries()) {
            list.add("" + i.getID());
         }

         return list;
      }
   }

   public String getCommand() {
      return "delete";
   }

   public void runCommand(MinecraftServer server, CommandBase command, ICommandSender sender, String[] args) throws CommandException {
      if (args[1].equalsIgnoreCase("all")) {
         QuestDatabase.INSTANCE.reset();
         QuestLineDatabase.INSTANCE.reset();
         NetQuestSync.sendSync((EntityPlayerMP)null, (int[])null, true, true);
         NetChapterSync.sendSync((EntityPlayerMP)null, (int[])null);
         SaveLoadHandler.INSTANCE.markDirty();
         sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.delete.all", new Object[0]));
      } else {
         try {
            int id = Integer.parseInt(args[1].trim());
            IQuest quest = (IQuest)QuestDatabase.INSTANCE.getValue(id);
            NetQuestEdit.deleteQuests(new int[]{id});
            sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.delete.single", new Object[]{new TextComponentTranslation((String)quest.getProperty(NativeProps.NAME), new Object[0])}));
         } catch (Exception var7) {
            throw this.getException(command);
         }
      }

   }

   public String getPermissionNode() {
      return "betterquesting.command.admin.delete";
   }

   public DefaultPermissionLevel getPermissionLevel() {
      return DefaultPermissionLevel.OP;
   }

   public String getPermissionDescription() {
      return "Permission to delete given quest(s) and progression data however it does not delete new world defaults";
   }
}
