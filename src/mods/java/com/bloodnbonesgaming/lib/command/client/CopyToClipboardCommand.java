package com.bloodnbonesgaming.lib.command.client;

import com.bloodnbonesgaming.lib.util.ClipboardHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.IClientCommand;

public class CopyToClipboardCommand extends CommandBase implements IClientCommand {
   private static final List<String> aliases = new ArrayList();

   public CopyToClipboardCommand() {
   }

   public String getName() {
      return "clipboard";
   }

   public List<String> getAliases() {
      return aliases;
   }

   public String getUsage(ICommandSender sender) {
      return "";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      if (sender instanceof EntityPlayer) {
         if (args.length > 0) {
            StringBuilder builder = new StringBuilder();

            for(int i = 0; i < args.length; ++i) {
               builder.append(args[i]);
            }

            ClipboardHelper.copyToClipboard(builder.toString());
         }
      } else {
         sender.sendMessage(new TextComponentString("Command can only be used by players!"));
      }

   }

   public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
      return false;
   }

   public int getRequiredPermissionLevel() {
      return 0;
   }
}
