package ivorius.reccomplex.commands;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.SimpleCommand;
import ivorius.reccomplex.network.PacketReopenGui;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandReopen extends SimpleCommand {
   public CommandReopen() {
      super(RCConfig.commandPrefix + "reopen");
      this.permitFor(2);
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
      RecurrentComplex.network.sendTo(new PacketReopenGui(), player);
   }
}
