package ivorius.reccomplex.commands.schematic;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.MCP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex.commands.parameters.IvP;
import ivorius.reccomplex.commands.parameters.expect.RCE;
import ivorius.reccomplex.operation.OperationGenerateSchematic;
import ivorius.reccomplex.operation.OperationRegistry;
import ivorius.reccomplex.world.gen.feature.structure.schematics.SchematicFile;
import ivorius.reccomplex.world.gen.feature.structure.schematics.SchematicLoader;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandImportSchematic extends CommandExpecting {
   public CommandImportSchematic() {
   }

   @Nonnull
   protected static SchematicFile parseSchematic(String schematicName) throws CommandException {
      SchematicFile schematicFile;
      try {
         schematicFile = SchematicLoader.loadSchematicByName(schematicName);
      } catch (SchematicFile.UnsupportedSchematicFormatException var3) {
         throw RecurrentComplex.translations.commandException("commands.rcimportschematic.format", schematicName, var3.format);
      }

      if (schematicFile == null) {
         throw RecurrentComplex.translations.commandException("commands.rcimportschematic.missing", schematicName, SchematicLoader.getLookupFolderName());
      } else {
         return schematicFile;
      }
   }

   public String getName() {
      return "import";
   }

   public void expect(Expect expect) {
      expect.then(RCE::schematic).then(MCE.pos("x", "y", "z")).named("rotation", "r").then(MCE::rotation).flag("mirror", "m");
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10000 = this.expect();
      Function<Parameters, Parameters> c = var10000::declare;
      Parameters parameters = Parameters.of(args, c);
      if (args.length < 1) {
         throw RecurrentComplex.translations.wrongUsageException("commands.rcimportschematic.usage");
      } else {
         SchematicFile schematicFile = parseSchematic((String)parameters.get(0).require());
         BlockPos pos = (BlockPos)parameters.get(MCP.pos("x", "y", "z", commandSender.getPosition(), false)).require();
         AxisAlignedTransform2D transform = (AxisAlignedTransform2D)parameters.get(IvP.transform("rotation", "mirror")).optional().orElse(AxisAlignedTransform2D.ORIGINAL);
         OperationRegistry.queueOperation(new OperationGenerateSchematic(schematicFile, transform, pos), commandSender);
      }
   }

   public int getRequiredPermissionLevel() {
      return 4;
   }
}
