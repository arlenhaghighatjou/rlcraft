package betterquesting.commands.admin;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.properties.NativeProps;
import betterquesting.commands.QuestCommandBase;
import betterquesting.network.handlers.NetLifeSync;
import betterquesting.storage.LifeDatabase;
import betterquesting.storage.NameCache;
import betterquesting.storage.QuestSettings;
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

public class QuestCommandLives extends QuestCommandBase {
   public QuestCommandLives() {
   }

   public String getCommand() {
      return "lives";
   }

   public String getUsageSuffix() {
      return "[add|set|max|default] <value> [username|uuid]";
   }

   public boolean validArgs(String[] args) {
      return args.length == 4 || args.length == 3;
   }

   public List<String> autoComplete(MinecraftServer server, ICommandSender sender, String[] args) {
      if (args.length != 4 || !args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("set")) {
         return args.length == 2 ? CommandBase.getListOfStringsMatchingLastWord(args, new String[]{"add", "set", "max", "default"}) : Collections.emptyList();
      } else {
         return CommandBase.getListOfStringsMatchingLastWord(args, NameCache.INSTANCE.getAllNames());
      }
   }

   public void runCommand(MinecraftServer server, CommandBase command, ICommandSender sender, String[] args) throws CommandException {
      String action = args[1];
      EaglercraftUUID playerID = null;

      int value;
      try {
         value = Integer.parseInt(args[2]);
      } catch (Exception var13) {
         throw this.getException(command);
      }

      if (args.length >= 4) {
         playerID = this.findPlayerID(server, sender, args[3]);
         if (playerID == null) {
            throw this.getException(command);
         }
      }

      String pName = playerID == null ? "NULL" : NameCache.INSTANCE.getName(playerID);
      if (action.equalsIgnoreCase("set")) {
         value = Math.max(1, value);
         if (playerID != null) {
            LifeDatabase.INSTANCE.setLives(playerID, value);
            EntityPlayerMP target = server.getPlayerList().getPlayerByUUID(playerID);
            if (target != null) {
               NetLifeSync.sendSync(new EntityPlayerMP[]{target}, new EaglercraftUUID[]{playerID});
            }

            sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.lives.set_player", new Object[]{pName, value}));
         } else {
            for(EntityPlayerMP p : server.getPlayerList().getPlayers()) {
               EaglercraftUUID uuid = QuestingAPI.getQuestingUUID(p);
               LifeDatabase.INSTANCE.setLives(uuid, value);
               NetLifeSync.sendSync(new EntityPlayerMP[]{p}, new EaglercraftUUID[]{uuid});
            }

            sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.lives.set_all", new Object[]{value}));
         }
      } else if (action.equalsIgnoreCase("add")) {
         if (playerID != null) {
            int lives = LifeDatabase.INSTANCE.getLives(playerID) + value;
            LifeDatabase.INSTANCE.setLives(playerID, lives);
            EntityPlayerMP target = server.getPlayerList().getPlayerByUUID(playerID);
            if (target != null) {
               NetLifeSync.sendSync(new EntityPlayerMP[]{target}, new EaglercraftUUID[]{playerID});
            }

            if (value >= 0) {
               sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.lives.add_player", new Object[]{value, pName, lives}));
            } else {
               sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.lives.remove_player", new Object[]{Math.abs(value), pName, lives}));
            }
         } else {
            for(EntityPlayerMP p : server.getPlayerList().getPlayers()) {
               EaglercraftUUID uuid = QuestingAPI.getQuestingUUID(p);
               int lives = LifeDatabase.INSTANCE.getLives(uuid);
               LifeDatabase.INSTANCE.setLives(uuid, lives + value);
               NetLifeSync.sendSync(new EntityPlayerMP[]{p}, new EaglercraftUUID[]{uuid});
            }

            if (value >= 0) {
               sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.lives.add_all", new Object[]{value}));
            } else {
               sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.lives.remove_all", new Object[]{Math.abs(value)}));
            }
         }
      } else if (action.equalsIgnoreCase("max")) {
         value = Math.max(1, value);
         QuestSettings.INSTANCE.setProperty(NativeProps.LIVES_MAX, Integer.valueOf(value));
         sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.lives.max", new Object[]{value}));
      } else {
         if (!action.equalsIgnoreCase("default")) {
            throw this.getException(command);
         }

         value = Math.max(1, value);
         QuestSettings.INSTANCE.setProperty(NativeProps.LIVES_DEF, Integer.valueOf(value));
         sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.lives.default" + value, new Object[0]));
      }

   }

   public boolean isArgUsername(String[] args, int index) {
      return index == 3;
   }

   public String getPermissionNode() {
      return "betterquesting.command.admin.lives";
   }

   public DefaultPermissionLevel getPermissionLevel() {
      return DefaultPermissionLevel.OP;
   }

   public String getPermissionDescription() {
      return "Permission to set lives to the given user";
   }
}
