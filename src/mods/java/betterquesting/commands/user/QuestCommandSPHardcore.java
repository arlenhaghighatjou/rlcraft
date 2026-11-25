package betterquesting.commands.user;

import betterquesting.api.properties.NativeProps;
import betterquesting.commands.QuestCommandBase;
import betterquesting.handlers.SaveLoadHandler;
import betterquesting.network.handlers.NetSettingSync;
import betterquesting.storage.QuestSettings;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class QuestCommandSPHardcore extends QuestCommandBase {
   public QuestCommandSPHardcore() {
   }

   public String getCommand() {
      return "hardcore";
   }

   public void runCommand(MinecraftServer server, CommandBase command, ICommandSender sender, String[] args) throws CommandException {
      if (server.isSinglePlayer() && server.getServerOwner().equalsIgnoreCase(sender.getName())) {
         QuestSettings.INSTANCE.setProperty(NativeProps.HARDCORE, Boolean.valueOf(true));
         SaveLoadHandler.INSTANCE.markDirty();
         sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.hardcore", new Object[]{new TextComponentTranslation("options.on", new Object[0])}));
         NetSettingSync.sendSync((EntityPlayerMP)null);
      } else {
         TextComponentTranslation cc = new TextComponentTranslation("commands.generic.permission", new Object[0]);
         cc.getStyle().setColor(TextFormatting.RED);
         sender.sendMessage(cc);
      }
   }

   public String getPermissionNode() {
      return "betterquesting.command.user.hardcores";
   }

   public DefaultPermissionLevel getPermissionLevel() {
      return DefaultPermissionLevel.ALL;
   }

   public String getPermissionDescription() {
      return "Permission to manually resyncs the local questing database with the server in case of potential desync issues";
   }
}
