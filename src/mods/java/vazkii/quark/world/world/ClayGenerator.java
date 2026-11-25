package vazkii.quark.world.world;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.world.feature.ClayGeneration;

public class ClayGenerator implements IWorldGenerator {
   private final int clusterCount;
   private final WorldGenMinable generator;

   public ClayGenerator(int clusterSize, int clusterCount) {
      this.clusterCount = clusterCount;
      this.generator = new WorldGenMinable(Blocks.CLAY.getDefaultState(), clusterSize);
   }

   public void generate(EaglercraftRandom rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      for(int i = 0; i < this.clusterCount; ++i) {
         int x = chunkX * 16 + rand.nextInt(16);
         int y = ClayGeneration.minHeight + rand.nextInt(ClayGeneration.maxHeight - ClayGeneration.minHeight);
         int z = chunkZ * 16 + rand.nextInt(16);
         this.generator.generate(world, rand, new BlockPos(x, y, z));
      }

   }
}
