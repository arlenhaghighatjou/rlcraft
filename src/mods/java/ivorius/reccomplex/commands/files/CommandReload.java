package ivorius.reccomplex.commands.files;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameter;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.files.loading.LeveledRegistry;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandReload extends CommandExpecting {
   public CommandReload() {
   }

   public String getName() {
      return RCConfig.commandPrefix + "reload";
   }

   public int getRequiredPermissionLevel() {
      return 4;
   }

   public void expect(Expect expect) {
      expect.any(LeveledRegistry.Level.CUSTOM, LeveledRegistry.Level.MODDED, LeveledRegistry.Level.SERVER).descriptionU("level");
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      LeveledRegistry.Level level = (LeveledRegistry.Level)parameters.get(0).map(LeveledRegistry.Level::valueOf).optional().orElse(LeveledRegistry.Level.CUSTOM);

      try {
         RCCommands.tryReload(RecurrentComplex.loader, level);
         commandSender.sendMessage(RecurrentComplex.translations.format("commands.strucReload.success", level));
      } catch (IllegalArgumentException var7) {
         throw RecurrentComplex.translations.wrongUsageException("commands.strucReload.usage");
      }
   }
}
