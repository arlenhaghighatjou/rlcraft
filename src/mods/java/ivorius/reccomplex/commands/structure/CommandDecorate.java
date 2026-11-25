package ivorius.reccomplex.commands.structure;

import ivorius.ivtoolkit.blocks.BlockSurfaceArea;
import ivorius.ivtoolkit.blocks.BlockSurfacePos;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex..shadow.mcopts.commands.CommandExpecting;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.MCE;
import ivorius.reccomplex.commands.parameters.IvP;
import ivorius.reccomplex.commands.parameters.RCP;
import ivorius.reccomplex.commands.parameters.expect.RCE;
import ivorius.reccomplex.world.gen.feature.WorldGenStructures;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;

public class CommandDecorate extends CommandExpecting {
   public CommandDecorate() {
   }

   @Nonnull
   protected static BlockSurfacePos getChunkPos(BlockSurfacePos point) {
      return new BlockSurfacePos(point.getX() >> 4, point.getZ() >> 4);
   }

   public String getName() {
      return RCConfig.commandPrefix + "decorate";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void expect(Expect expect) {
      expect.then(MCE::xz).atOnce(2).descriptionU("x1", "z1").then(MCE::xz).atOnce(2).descriptionU("x2", "z2").named("exp").words(RCE::structurePredicate).flag("one");
   }

   public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {
      Expect var10001 = this.expect();
      var10001.getClass();
      Parameters parameters = Parameters.of(args, var10001::declare);
      BlockSurfaceArea area = new BlockSurfaceArea((BlockSurfacePos)parameters.get(0).to(IvP.surfacePos(commandSender.getPosition(), false)).require(), (BlockSurfacePos)parameters.get(2).to(IvP.surfacePos(commandSender.getPosition(), false)).require());
      BlockSurfaceArea chunkArea = new BlockSurfaceArea(getChunkPos(area.getPoint1()), getChunkPos(area.getPoint2()));
      Predicate<Structure> structurePredicate = (Predicate)parameters.get("exp").to(RCP::structurePredicate).optional().orElse((structureInfo) -> {
         return true;
      });
      WorldServer world = (WorldServer)commandSender.getEntityWorld();
      if (parameters.has("one")) {
         BlockSurfacePos lower = area.getLowerCorner();
         int[] size = area.areaSize();
         BlockPos pos = new BlockPos(lower.x + world.rand.nextInt(size[0]), 0, lower.z + world.rand.nextInt(size[1]));
         if (!WorldGenStructures.generateOneStructureInChunk(world.rand, new ChunkPos(pos), world, world.getBiome(pos))) {
            throw RecurrentComplex.translations.commandException("commands.rcdecorateone.none");
         }
      } else {
         chunkArea.forEach((coord) -> {
            WorldGenStructures.decorate(world, world.rand, new ChunkPos(coord.x, coord.z), structurePredicate);
         });
      }

   }
}
