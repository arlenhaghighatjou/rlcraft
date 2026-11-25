package com.charles445.rltweaker.client;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class ClientCommandDebug extends ClientCommandBase {
   public ClientCommandDebug() {
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public String getName() {
      return "rldebugclient";
   }

   public String getUsage(ICommandSender sender) {
      return "/rldebugclient";
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
   }
}
