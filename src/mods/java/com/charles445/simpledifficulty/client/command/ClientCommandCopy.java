package com.charles445.simpledifficulty.client.command;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class ClientCommandCopy extends ClientCommandBase {
   public ClientCommandCopy() {
   }

   public String getName() {
      return "sdcopy";
   }

   public String getUsage(ICommandSender sender) {
      return "/sdcopy <string>";
   }

   public int getRequiredPermissionLevel() {
      return 0;
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      StringBuilder sb = new StringBuilder();

      for(String s : args) {
         sb.append(s);
         sb.append(" ");
      }

      if (sender.getEntityWorld().isRemote) {
         String result = sb.toString().trim();
         GuiScreen.setClipboardString(result);
         sender.sendMessage(new TextComponentString("Copied to clipboard!"));
      } else {
         sender.sendMessage(new TextComponentString("World was not remote, skipping copy execution!"));
      }

   }
}
