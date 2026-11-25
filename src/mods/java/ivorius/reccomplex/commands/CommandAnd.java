package ivorius.reccomplex.commands;

import ivorius.ivtoolkit.world.MockWorld;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameter;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex.commands.parameters.RCP;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommandAnd extends CommandExpecting implements CommandVirtual {
   public CommandAnd() {
   }

   public String getName() {
      return RCConfig.commandPrefix + "&";
   }

   public void expect(Expect expect) {
      expect.split(this::expectSubcommand).required().repeat();
   }

   public void expectSubcommand(Expect expect) {
      expect.stopInterpreting().then(MCE::command).then(MCE::commandArguments, (p) -> {
         return p.get(0);
      }).repeat();
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);

      for(String param : (List)parameters.get(0).varargsList().require()) {
         server.commandManager.executeCommand(commandSender, param);
      }

   }

   public void execute(MockWorld world, ICommandSender sender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

      for(String param : (List)parameters.get(0).varargsList().require()) {
         String[] commandArgs = param.split(" ");
         CommandVirtual command = (CommandVirtual)Parameter.makeUp("command", 0, commandArgs[0]).to(RCP::virtualCommand, server).require();
         command.execute(world, sender, (String[])Arrays.stream(commandArgs).skip(1L).toArray((x$0) -> {
            return new String[x$0];
         }));
      }

   }
}
