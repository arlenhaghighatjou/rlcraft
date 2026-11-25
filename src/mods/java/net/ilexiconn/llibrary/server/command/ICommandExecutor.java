package net.ilexiconn.llibrary.server.command;

import net.ilexiconn.llibrary.server.command.argument.CommandArguments;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

@FunctionalInterface
public interface ICommandExecutor {
   void execute(MinecraftServer var1, ICommandSender var2, CommandArguments var3) throws CommandException;
}
