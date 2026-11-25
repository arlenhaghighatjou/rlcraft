package ivorius.reccomplex.commands.structure;

import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.commands.parameters.expect.RCE;
import ivorius.reccomplex.files.loading.ResourceDirectory;
import ivorius.reccomplex.network.PacketEditStructureHandler;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandEditStructure extends CommandExpecting {
   public CommandEditStructure() {
   }

   public String getName() {
      return RCConfig.commandPrefix + "edit";
   }

   public int getRequiredPermissionLevel() {
      return 4;
   }

   public void expect(Expect expect) {
      expect.then(RCE::structure).named("id").then(RCE::randomString).descriptionU("export id").named("from").then(RCE::structure).named("directory", "d").then(RCE::resourceDirectory);
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      EntityPlayerMP entityPlayerMP = getCommandSenderAsPlayer(commandSender);
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      ResourceDirectory directory = (ResourceDirectory)parameters.get("directory").to(RCP::resourceDirectory).optional().orElse((Object)null);
      String structureID = (String)parameters.get("id").optional().orElse(parameters.get(0).require());
      GenericStructure base = (GenericStructure)parameters.get(0).to((p) -> {
         return RCP.genericStructure(p, false);
      }).require();
      GenericStructure from = (GenericStructure)parameters.get("from").to((p) -> {
         return RCP.genericStructure(p, false);
      }).optional().orElse(base);
      if (base != from) {
         from = from.copyAsGenericStructure();
         from.worldDataCompound = base.worldDataCompound.copy();
      }

      PacketEditStructureHandler.openEditStructure(entityPlayerMP, from, entityPlayerMP.getPosition(), structureID, directory);
   }
}
