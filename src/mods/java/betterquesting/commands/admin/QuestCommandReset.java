package betterquesting.commands.admin;

import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api2.storage.DBEntry;
import betterquesting.commands.QuestCommandBase;
import betterquesting.network.handlers.NetQuestSync;
import betterquesting.questing.QuestDatabase;
import betterquesting.storage.NameCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class QuestCommandReset extends QuestCommandBase {
   public QuestCommandReset() {
   }

   public String getUsageSuffix() {
      return "[all|<quest_id>] [username|uuid]";
   }

   public boolean validArgs(String[] args) {
      return args.length == 2 || args.length == 3;
   }

   public List<String> autoComplete(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length != 2) {
         return args.length == 3 ? CommandBase.getListOfStringsMatchingLastWord(args, NameCache.INSTANCE.getAllNames()) : Collections.emptyList();
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
      return "reset";
   }

   public void runCommand(MinecraftServer server, CommandBase command, ICommandSender sender, String[] args) throws CommandException {
      String action = args[1];
      EaglercraftUUID uuid = null;
      if (args.length == 3) {
         uuid = this.findPlayerID(server, sender, args[2]);
         if (uuid == null) {
            throw this.getException(command);
         }
      }

      String pName = uuid == null ? "NULL" : NameCache.INSTANCE.getName(uuid);
      EntityPlayerMP player = uuid == null ? null : server.getPlayerList().getPlayerByUUID(uuid);
      if (action.equalsIgnoreCase("all")) {
         for(DBEntry<IQuest> entry : QuestDatabase.INSTANCE.getEntries()) {
            if (uuid != null) {
               ((IQuest)entry.getValue()).resetUser(uuid, true);
            } else {
               ((IQuest)entry.getValue()).resetUser((EaglercraftUUID)null, true);
            }
         }

         if (uuid != null) {
            sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.reset.player_all", new Object[]{pName}));
            if (player != null) {
               NetQuestSync.sendSync(player, (int[])null, false, true);
            }
         } else {
            sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.reset.all_all", new Object[0]));
            NetQuestSync.quickSync(-1, false, true);
         }
      } else {
         try {
            int id = Integer.parseInt(action.trim());
            IQuest quest = (IQuest)QuestDatabase.INSTANCE.getValue(id);
            if (uuid != null) {
               quest.resetUser(uuid, true);
               sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.reset.player_single", new Object[]{new TextComponentTranslation((String)quest.getProperty(NativeProps.NAME), new Object[0]), pName}));
               if (player != null) {
                  NetQuestSync.sendSync(player, new int[]{id}, false, true);
               }
            } else {
               quest.resetUser((EaglercraftUUID)null, true);
               sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.reset.all_single", new Object[]{new TextComponentTranslation((String)quest.getProperty(NativeProps.NAME), new Object[0])}));
               NetQuestSync.quickSync(id, false, true);
            }
         } catch (Exception var11) {
            throw this.getException(command);
         }
      }

   }

   public boolean isArgUsername(String[] args, int index) {
      return index == 2;
   }

   public String getPermissionNode() {
      return "betterquesting.command.admin.reset";
   }

   public DefaultPermissionLevel getPermissionLevel() {
      return DefaultPermissionLevel.OP;
   }

   public String getPermissionDescription() {
      return "Permission to erases quest completion data for the given user";
   }
}
