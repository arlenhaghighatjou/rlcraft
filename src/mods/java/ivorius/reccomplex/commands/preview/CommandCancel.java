package ivorius.reccomplex.commands.preview;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.SimpleCommand;
import ivorius.reccomplex.capability.RCEntityInfo;
import ivorius.reccomplex.commands.RCCommands;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;

public class CommandCancel extends SimpleCommand {
   public CommandCancel() {
      super(RCConfig.commandPrefix + "cancel");
      this.permitFor(2);
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      EntityPlayer player = getCommandSenderAsPlayer(commandSender);
      RCEntityInfo RCEntityInfo = RCCommands.getStructureEntityInfo(player, (EnumFacing)null);
      if (!RCEntityInfo.cancelOperation(commandSender.getEntityWorld(), player)) {
         throw RecurrentComplex.translations.commandException("commands.rc.noOperation");
      }
   }
}
