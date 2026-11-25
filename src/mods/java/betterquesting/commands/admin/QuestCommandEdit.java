package betterquesting.commands.admin;

import betterquesting.api.properties.NativeProps;
import betterquesting.commands.QuestCommandBase;
import betterquesting.handlers.SaveLoadHandler;
import betterquesting.network.handlers.NetSettingSync;
import betterquesting.storage.QuestSettings;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class QuestCommandEdit extends QuestCommandBase {
   public QuestCommandEdit() {
   }

   public String getCommand() {
      return "edit";
   }

   public String getUsageSuffix() {
      return "[true|false]";
   }

   public boolean validArgs(String[] args) {
      return args.length == 1 || args.length == 2;
   }

   public List<String> autoComplete(MinecraftServer server, ICommandSender sender, String[] args) {
      return args.length == 2 ? CommandBase.getListOfStringsMatchingLastWord(args, new String[]{"true", "false"}) : Collections.emptyList();
   }

   public void runCommand(MinecraftServer server, CommandBase command, ICommandSender sender, String[] args) throws CommandException {
      boolean flag = !((Boolean)QuestSettings.INSTANCE.getProperty(NativeProps.EDIT_MODE)).booleanValue();
      if (args.length == 2) {
         try {
            if (args[1].equalsIgnoreCase("on")) {
               flag = true;
            } else if (args[1].equalsIgnoreCase("off")) {
               flag = false;
            } else {
               flag = Boolean.parseBoolean(args[1]);
            }
         } catch (Exception var7) {
            throw this.getException(command);
         }
      }

      QuestSettings.INSTANCE.setProperty(NativeProps.EDIT_MODE, Boolean.valueOf(flag));
      sender.sendMessage(new TextComponentTranslation("betterquesting.cmd.edit", new Object[]{new TextComponentTranslation(((Boolean)QuestSettings.INSTANCE.getProperty(NativeProps.EDIT_MODE)).booleanValue() ? "options.on" : "options.off", new Object[0])}));
      SaveLoadHandler.INSTANCE.markDirty();
      NetSettingSync.sendSync((EntityPlayerMP)null);
   }

   public String getPermissionNode() {
      return "betterquesting.command.admin.edit";
   }

   public DefaultPermissionLevel getPermissionLevel() {
      return DefaultPermissionLevel.OP;
   }

   public String getPermissionDescription() {
      return "Permission to activate quest editing mode on and off";
   }
}
