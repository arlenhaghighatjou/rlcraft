package ivorius.reccomplex.commands;

import ivorius.ivtoolkit.world.MockWorld;
import ivorius.ivtoolkit.world.MockWorld.Real;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public interface CommandVirtual extends ICommand {
   default void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      this.execute(new Real(sender.getEntityWorld()), sender, args);
   }

   void execute(MockWorld var1, ICommandSender var2, String[] var3) throws CommandException;
}
