package leviathan143.loottweaker.common.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public interface Subcommand {
   void execute(MinecraftServer var1, ICommandSender var2, String[] var3);
}
