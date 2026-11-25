package ivorius.reccomplex.commands.schematic;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.commands.parameters.expect.RCE;
import ivorius.reccomplex.files.loading.ResourceDirectory;
import ivorius.reccomplex.network.PacketEditStructureHandler;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import ivorius.reccomplex.world.gen.feature.structure.schematics.SchematicFile;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandConvertSchematic extends CommandExpecting {
   public CommandConvertSchematic() {
   }

   public String getName() {
      return "convert";
   }

   public void expect(Expect expect) {
      expect.then(RCE::schematic).named("id").then(RCE::randomString).descriptionU("export id").named("from").then(RCE::structure).named("directory", "d").then(RCE::resourceDirectory);
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
      if (args.length < 1) {
         throw RecurrentComplex.translations.wrongUsageException("commands.rcconvertschematic.usage");
      } else {
         ResourceDirectory directory = (ResourceDirectory)parameters.get("directory").to(RCP::resourceDirectory).optional().orElse((Object)null);
         String schematicName = (String)parameters.get(0).require();
         String structureID = (String)parameters.get("id").optional().orElse(schematicName);
         SchematicFile schematicFile = CommandImportSchematic.parseSchematic(schematicName);
         GenericStructure from = (GenericStructure)parameters.get("from").to(RCP::structureFromBlueprint, commandSender).require();
         from.worldDataCompound = CommandExportSchematic.toWorldData(schematicFile).createTagCompound();
         PacketEditStructureHandler.openEditStructure(player, from, player.getPosition(), structureID, directory);
      }
   }

   public int getRequiredPermissionLevel() {
      return 4;
   }
}
