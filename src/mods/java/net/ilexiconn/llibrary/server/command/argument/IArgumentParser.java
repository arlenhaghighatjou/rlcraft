package net.ilexiconn.llibrary.server.command.argument;

import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public interface IArgumentParser<T> {
   T parseArgument(MinecraftServer var1, ICommandSender var2, String var3) throws CommandException;

   List<String> getTabCompletion(MinecraftServer var1, ICommandSender var2, String[] var3, BlockPos var4);
}
