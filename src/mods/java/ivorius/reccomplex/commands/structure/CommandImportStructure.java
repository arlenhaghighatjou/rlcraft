package ivorius.reccomplex.commands.structure;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.MCP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.commands.parameters.IvP;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.commands.parameters.expect.RCE;
import ivorius.reccomplex.operation.OperationGenerateStructure;
import ivorius.reccomplex.operation.OperationRegistry;
import ivorius.reccomplex.utils.RCBlockAreas;
import ivorius.reccomplex.world.gen.feature.StructureGenerator;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CommandImportStructure extends CommandExpecting {
   public CommandImportStructure() {
   }

   public String getName() {
      return RCConfig.commandPrefix + "import";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void expect(Expect expect) {
      expect.then(RCE::structure).then(MCE.pos("x", "y", "z")).named("dimension", "d").then(MCE::dimension).named("rotation", "r").then(MCE::rotation).flag("mirror", "m").flag("select", "s");
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      String structureID = (String)parameters.get(0).require();
      Structure<?> structure = (Structure)parameters.get(0).to(RCP::structure).require();
      WorldServer world = (WorldServer)parameters.get("dimension").to(MCP.dimension(server, sender)).require();
      AxisAlignedTransform2D transform = (AxisAlignedTransform2D)parameters.get(IvP.transform("rotation", "mirror")).optional().orElse(AxisAlignedTransform2D.ORIGINAL);
      BlockPos pos = (BlockPos)parameters.get(MCP.pos("x", "y", "z", sender.getPosition(), false)).require();
      StructureGenerator<?> generator = (new StructureGenerator(structure)).world(world).transform(transform).lowerCoord(pos).asSource(true);
      StructureBoundingBox boundingBox = (StructureBoundingBox)generator.boundingBox().get();
      if (structure instanceof GenericStructure && world == sender.getEntityWorld()) {
         OperationRegistry.queueOperation((new OperationGenerateStructure((GenericStructure)structure, structureID, transform, pos, true)).withStructureID(structureID).prepare(world), sender);
      } else {
         generator.generate();
      }

      if (parameters.has("select")) {
         RCCommands.select(sender, RCBlockAreas.from(boundingBox));
      }

   }
}
