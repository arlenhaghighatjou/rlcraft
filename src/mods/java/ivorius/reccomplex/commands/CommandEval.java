package ivorius.reccomplex.commands;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.NaP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.utils.expression.DependencyExpression;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandEval extends CommandExpecting {
   public CommandEval() {
   }

   public String getName() {
      return RCConfig.commandPrefix + "eval";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void expect(Expect expect) {
      expect.next(RCConfig.globalToggles.keySet().stream().map((s) -> {
         return "global:" + s;
      })).descriptionU("dependency expression");
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      DependencyExpression matcher = (DependencyExpression)parameters.get(0).rest(NaP::join).to(RCP::expression, new DependencyExpression()).require();
      boolean result = matcher.test(RecurrentComplex.saver);
      commandSender.sendMessage(RecurrentComplex.translations.get(result ? "commands.rceval.result.true" : "commands.rceval.result.false"));
   }
}
