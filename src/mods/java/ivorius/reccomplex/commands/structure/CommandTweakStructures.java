package ivorius.reccomplex.commands.structure;

import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.network.PacketSpawnTweaksHandler;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandTweakStructures extends CommandExpecting {
   public CommandTweakStructures() {
   }

   public String getName() {
      return "tweak";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void expect(Expect expect) {
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      PacketSpawnTweaksHandler.openGUI(getCommandSenderAsPlayer(commandSender));
   }
}
