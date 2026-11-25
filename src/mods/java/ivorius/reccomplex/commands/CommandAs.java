package ivorius.reccomplex.commands;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.MCP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;

public class CommandAs extends CommandExpecting {
   public CommandAs() {
   }

   public String getName() {
      return RCConfig.commandPrefix + "as";
   }

   public int getRequiredPermissionLevel() {
      return RCConfig.asCommandPermissionLevel;
   }

   public void expect(Expect expect) {
      expect.then(MCE::entity).required().then(MCE::command).required().then(MCE::commandArguments, (p) -> {
         return p.get(1);
      }).repeat();
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      Entity entity = (Entity)parameters.get(0).to(MCP.entity(server, commandSender)).require();
      String command = buildString(args, 1);
      server.commandManager.executeCommand(entity, command);
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
