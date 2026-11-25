package ivorius.reccomplex.commands.structure;

import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.capability.SelectionOwner;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.commands.parameters.expect.RCE;
import ivorius.reccomplex.files.loading.ResourceDirectory;
import ivorius.reccomplex.network.PacketEditStructureHandler;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;

public class CommandExportStructure extends CommandExpecting {
   public CommandExportStructure() {
   }

   public String getName() {
      return RCConfig.commandPrefix + "export";
   }

   public int getRequiredPermissionLevel() {
      return 4;
   }

   public void expect(Expect expect) {
      expect.then(RCE::structure).descriptionU("copy structure").named("id").then(RCE::randomString).descriptionU("export id").named("directory", "d").then(RCE::resourceDirectory);
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
      ResourceDirectory directory = (ResourceDirectory)parameters.get("directory").to(RCP::resourceDirectory).optional().orElse((Object)null);
      String structureID = (String)parameters.get("id").optional().orElse(parameters.get(0).optional().orElse((Object)null));
      GenericStructure from = (GenericStructure)parameters.get(0).to(RCP::structureFromBlueprint, commandSender).require();
      SelectionOwner selectionOwner = RCCommands.getSelectionOwner(commandSender, (EnumFacing)null, true);
      RCCommands.assertSize(commandSender, selectionOwner);
      from.worldDataCompound = IvWorldData.capture(commandSender.getEntityWorld(), selectionOwner.getSelection(), true).createTagCompound();
      PacketEditStructureHandler.openEditStructure(player, from, selectionOwner.getSelection().getLowerCorner(), structureID, directory);
   }
}
