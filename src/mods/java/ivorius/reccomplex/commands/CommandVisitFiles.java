package ivorius.reccomplex.commands;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.commands.parameters.expect.RCE;
import ivorius.reccomplex.files.loading.ResourceDirectory;
import java.util.function.Consumer;
import java.util.function.Function;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandVisitFiles extends CommandExpecting {
   public CommandVisitFiles() {
   }

   public String getName() {
      return RCConfig.commandPrefix + "files";
   }

   public int getRequiredPermissionLevel() {
      return 0;
   }

   public void expect(Expect expect) {
      expect.named("directory", "d").then(RCE::resourceDirectory);
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      ResourceDirectory directory = (ResourceDirectory)parameters.get("directory").to(RCP::resourceDirectory).optional().orElse(ResourceDirectory.ACTIVE);
      OpenGlHelper.openFile(directory.toFile());
   }
}
