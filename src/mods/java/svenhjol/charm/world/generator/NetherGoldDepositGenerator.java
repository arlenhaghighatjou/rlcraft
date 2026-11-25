package svenhjol.charm.world.generator;

import java.util.Random;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;
import svenhjol.charm.world.feature.NetherGoldDeposits;

public class NetherGoldDepositGenerator implements IWorldGenerator {
   public static WorldGenMinable generator;

   public NetherGoldDepositGenerator() {
      generator = new WorldGenMinable(NetherGoldDeposits.ore.getDefaultState(), NetherGoldDeposits.clusterSize, BlockMatcher.forBlock(Blocks.NETHERRACK));
   }

   public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (world.provider instanceof WorldProviderHell) {
         for(int i = 0; i < NetherGoldDeposits.clusterCount; ++i) {
            int x = chunkX * 16 + rand.nextInt(16);
            int y = rand.nextInt(128);
            int z = chunkZ * 16 + rand.nextInt(16);
            generator.generate(world, rand, new BlockPos(x, y, z));
         }

      }
   }
}
