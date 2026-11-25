package betterquesting.commands;

import betterquesting.api.api.QuestingAPI;
import betterquesting.storage.NameCache;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public abstract class QuestCommandBase {
   public abstract String getCommand();

   public String getUsageSuffix() {
      return "";
   }

   public QuestCommandBase() {
      this.registerPermission();
   }

   public abstract String getPermissionNode();

   public abstract DefaultPermissionLevel getPermissionLevel();

   public abstract String getPermissionDescription();

   private void registerPermission() {
      PermissionAPI.registerNode(this.getPermissionNode(), this.getPermissionLevel(), this.getPermissionDescription());
   }

   public boolean validArgs(String[] args) {
      return args.length == 1;
   }

   public List<String> autoComplete(MinecraftServer server, ICommandSender sender, String[] args) {
      return Collections.emptyList();
   }

   public abstract void runCommand(MinecraftServer var1, CommandBase var2, ICommandSender var3, String[] var4) throws CommandException;

   public final WrongUsageException getException(CommandBase command) {
      String message = command.getName() + " " + this.getCommand();
      if (this.getUsageSuffix().length() > 0) {
         message = message + " " + this.getUsageSuffix();
      }

      return new WrongUsageException(message, new Object[0]);
   }

   public EaglercraftUUID findPlayerID(MinecraftServer server, ICommandSender sender, String name) {
      EntityPlayerMP player = null;

      try {
         player = (EntityPlayerMP)CommandBase.getEntity(server, sender, name, EntityPlayerMP.class);
      } catch (Exception var8) {
         ;
      }

      EaglercraftUUID playerID;
      if (player == null) {
         if (name.startsWith("@")) {
            return null;
         }

         try {
            playerID = EaglercraftUUID.fromString(name);
         } catch (Exception var7) {
            playerID = NameCache.INSTANCE.getUUID(name);
         }
      } else {
         playerID = QuestingAPI.getQuestingUUID(player);
      }

      return playerID;
   }

   public boolean isArgUsername(String[] args, int index) {
      return false;
   }
}
