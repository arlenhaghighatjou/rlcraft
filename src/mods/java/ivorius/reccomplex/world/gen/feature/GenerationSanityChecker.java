package ivorius.reccomplex.world.gen.feature;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import ivorius.reccomplex.RecurrentComplex;
import java.util.function.Predicate;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class GenerationSanityChecker {
   protected static final TIntSet FAILED_DIMENSIONS = new TIntHashSet();

   public GenerationSanityChecker() {
   }

   public static void init() {
      FAILED_DIMENSIONS.clear();
      GameRegistry.registerWorldGenerator((random, chunkX, chunkZ, world, chunkGenerator, chunkProvider) -> {
         ChunkPos pos = new ChunkPos(chunkX, chunkZ);
         if (WorldStructureGenerationData.get(world).checkChunk(pos)) {
            if (FAILED_DIMENSIONS.add(world.provider.getDimension())) {
               RecurrentComplex.logger.error(String.format("Chunk finished generating without Forge population being triggered (dimension %d). This is a bug with the dimension - please report this to the dimension's author. Recurrent Complex will proceed to generate in compatibility mode.", world.provider.getDimension()));
            }

            WorldGenStructures.decorate((WorldServer)world, random, pos, (Predicate)null);
         }
      }, 1);
   }
}
