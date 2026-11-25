package com.charles445.rltweaker.client;

import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.IClientCommand;

public abstract class ClientCommandBase implements IClientCommand {
   public ClientCommandBase() {
   }

   public abstract int getRequiredPermissionLevel();

   public abstract String getName();

   public abstract String getUsage(ICommandSender var1);

   public abstract void execute(MinecraftServer var1, ICommandSender var2, String[] var3) throws CommandException;

   public List<String> getAliases() {
      return Collections.emptyList();
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
      return Collections.emptyList();
   }

   public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
      return sender.canUseCommand(this.getRequiredPermissionLevel(), this.getName());
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return false;
   }

   public int compareTo(ICommand command) {
      return this.getName().compareTo(command.getName());
   }

   public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
      return false;
   }

   public void inform(String s, ICommandSender sender) {
      sender.sendMessage(new TextComponentString(s));
   }
}
