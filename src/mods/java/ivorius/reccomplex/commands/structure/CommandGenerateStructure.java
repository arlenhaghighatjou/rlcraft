package ivorius.reccomplex.commands.structure;

import ivorius.ivtoolkit.blocks.BlockSurfacePos;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.SimpleCommand;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.MCP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.commands.parameters.IvP;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.commands.parameters.expect.IvE;
import ivorius.reccomplex.commands.parameters.expect.RCE;
import ivorius.reccomplex.operation.OperationGenerateStructure;
import ivorius.reccomplex.operation.OperationRegistry;
import ivorius.reccomplex.utils.RCBlockAreas;
import ivorius.reccomplex.utils.RCStrings;
import ivorius.reccomplex.world.gen.feature.StructureGenerator;
import ivorius.reccomplex.world.gen.feature.structure.Placer;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.apache.commons.lang3.RandomStringUtils;

public class CommandGenerateStructure extends SimpleCommand {
   public CommandGenerateStructure() {
      super(RCConfig.commandPrefix + "gen");
      this.permitFor(2);
   }

   public void expect(Expect expect) {
      expect.then(RCE::structure).required().then(IvE.surfacePos("x", "z")).named("dimension", "d").then(MCE::dimension).named("gen").then(RCE::generationType, (p) -> {
         return p.get(0);
      }).named("rotation", "r").then(MCE::rotation).named("seed").words(RCE::randomString).descriptionU("seed").flag("mirror", "m").flag("select", "s").flag("suggest", "t");
   }

   @ParametersAreNonnullByDefault
   public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      String structureID = (String)parameters.get(0).require();
      Structure<?> structure = (Structure)parameters.get(0).to(RCP::structure).require();
      WorldServer world = (WorldServer)parameters.get("dimension").to(MCP.dimension(server, sender)).require();
      AxisAlignedTransform2D transform = (AxisAlignedTransform2D)parameters.get(IvP.transform("rotation", "mirror")).optional().orElse((Object)null);
      GenerationType generationType = (GenerationType)parameters.get("gen").to(RCP::generationType, structure).require();
      BlockSurfacePos pos = (BlockSurfacePos)parameters.get(IvP.surfacePos("x", "z", sender.getPosition(), false)).require();
      String seed = (String)parameters.get("seed").optional().orElseGet(() -> {
         return RandomStringUtils.randomAlphanumeric(20).toUpperCase();
      });
      boolean suggest = parameters.has("suggest");
      Placer placer = generationType.placer();
      StructureGenerator<?> generator = (new StructureGenerator(structure)).world(world).generationInfo(generationType).seed(RCStrings.seed(seed)).structureID(structureID).randomPosition(pos, placer).fromCenter(true).maturity(suggest ? StructureSpawnContext.GenerateMaturity.SUGGEST : StructureSpawnContext.GenerateMaturity.FIRST).transform(transform);
      if (structure instanceof GenericStructure && world == sender.getEntityWorld()) {
         GenericStructure genericStructureInfo = (GenericStructure)structure;
         if (!OperationRegistry.queueOperation((new OperationGenerateStructure(genericStructureInfo, generationType.id(), generator.transform(), (BlockPos)generator.lowerCoord().orElse((Object)null), false)).withSeed(seed).withStructureID(structureID).prepare(generator.instanceData()), sender)) {
            return;
         }
      } else {
         StructureGenerator.GenerationResult result = generator.generate();
         if (result instanceof StructureGenerator.GenerationResult.Failure) {
            String reason = ((StructureGenerator.GenerationResult.Failure)result).description;
            throw RecurrentComplex.translations.commandException("commands.strucGen.failure", reason);
         }
      }

      if (parameters.has("select")) {
         RCCommands.select(sender, RCBlockAreas.from((StructureBoundingBox)generator.boundingBox().get()));
      }

   }
}
