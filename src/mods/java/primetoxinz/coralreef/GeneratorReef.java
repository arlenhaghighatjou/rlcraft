package primetoxinz.coralreef;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import org.apache.commons.lang3.ArrayUtils;

public class GeneratorReef implements IWorldGenerator {
   protected static final int OCTAVES = 2;
   protected static final double NOISE_SCALE = 0.03125D;
   protected static final NoiseGeneratorOctaves CORAL_REEF_NOISE = new NoiseGeneratorOctaves(new Random(3364L), 2);
   public static final int CHUNK_SIZE = 16;
   private WorldGenerator genReef = new WorldGenReef(CoralReef.REEF.getDefaultState().withProperty(BlockReef.TYPES, Integer.valueOf(0)));
   private WorldGenerator genReefRock = new WorldGenRock(CoralReef.REEF.getDefaultState().withProperty(BlockReef.TYPES, Integer.valueOf(1)));
   private double[] noise = new double[256];

   public GeneratorReef() {
   }

   public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (ArrayUtils.isEmpty(CoralReef.ConfigHandler.dimensions) || ArrayUtils.contains(CoralReef.ConfigHandler.dimensions, world.provider.getDimension())) {
         this.reef(world, random, chunkX, chunkZ);
         if (random.nextDouble() <= CoralReef.ConfigHandler.rock.chance) {
            this.rock(world, random, chunkX, chunkZ);
         }
      }

   }

   private void rock(World world, Random rand, int chunkX, int chunkZ) {
      int x = chunkX * 16 + 8 + rand.nextInt(16);
      int z = chunkZ * 16 + 8 + rand.nextInt(16);
      BlockPos pos = getTop(world, x, z);
      this.genReefRock.generate(world, rand, pos.up());
   }

   private boolean reefFromNoise(int x, int y, int z, Random rand) {
      double d = this.noise[x * 16 + z] / 2.0D;
      int seaLevel = 64;
      double ybelow = (double)Math.max(0, seaLevel - y);
      d = d * (1.81E-4D * ybelow * ybelow - 0.1705D * ybelow + 4.0D);
      d = d - rand.nextDouble() * 0.1D;
      d = Math.pow(d, 5.0D);
      return d > 0.005D;
   }

   private void reef(World world, Random rand, int chunkX, int chunkZ) {
      this.noise = CORAL_REEF_NOISE.generateNoiseOctaves(this.noise, chunkX * 16, 0, chunkZ * 16, 16, 1, 16, 0.03125D, 1.0D, 0.03125D);

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            BlockPos pos = getTop(world, chunkX * 16 + x + 8, chunkZ * 16 + z + 8);
            if (this.reefFromNoise(x, pos.getY(), z, rand) && (ArrayUtils.isEmpty(CoralReef.ConfigHandler.biomes) || ArrayUtils.contains(CoralReef.ConfigHandler.biomes, world.getBiome(pos).getRegistryName().getResourcePath().toLowerCase()))) {
               this.genReef.generate(world, rand, pos);
            }
         }
      }

   }

   public static BlockPos getTop(World world, int x, int z) {
      Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
      MutableBlockPos blockPos = new MutableBlockPos(world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)));

      while(blockPos.getY() >= 0) {
         IBlockState state = chunk.getBlockState(blockPos);
         if (state.getMaterial().blocksMovement() && state.getMaterial() != Material.ICE && state.getMaterial() != Material.LEAVES && !state.getBlock().isFoliage(world, blockPos)) {
            break;
         }

         blockPos.setY(blockPos.getY() - 1);
      }

      return blockPos;
   }
}
