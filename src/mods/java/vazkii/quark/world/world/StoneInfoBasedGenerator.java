package vazkii.quark.world.world;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import vazkii.quark.base.handler.BiomeTypeConfigHandler;
import vazkii.quark.world.feature.RevampStoneGen;

public class StoneInfoBasedGenerator extends MultiChunkFeatureGenerator {
   public final Supplier<RevampStoneGen.StoneInfo> infoSupplier;
   public final String name;
   private final IBlockState state;
   private final long seedXor;

   public StoneInfoBasedGenerator(Supplier<RevampStoneGen.StoneInfo> infoSupplier, IBlockState state, String name) {
      this.infoSupplier = infoSupplier;
      this.name = name;
      this.state = state;
      this.seedXor = (long)name.hashCode();
   }

   public boolean canGenerate(World world) {
      RevampStoneGen.StoneInfo info = (RevampStoneGen.StoneInfo)this.infoSupplier.get();
      return info.enabled && info.dims.canSpawnHere(world);
   }

   public boolean canGenerateInBiome(Biome b) {
      return BiomeTypeConfigHandler.biomeTypeIntersectCheck(((RevampStoneGen.StoneInfo)this.infoSupplier.get()).allowedBiomes, b);
   }

   public boolean isSourceValid(World world, BlockPos pos) {
      return this.canGenerateInBiome(world.getBiome(pos));
   }

   public long modifyWorldSeed(long seed) {
      return seed ^ this.seedXor;
   }

   public int getFeatureRadius() {
      RevampStoneGen.StoneInfo info = (RevampStoneGen.StoneInfo)this.infoSupplier.get();
      return info.clusterSize;
   }

   public void generateChunkPart(BlockPos src, Random random, int chunkX, int chunkZ, World world) {
      RevampStoneGen.StoneInfo info = (RevampStoneGen.StoneInfo)this.infoSupplier.get();
      this.forEachChunkBlock(chunkX, chunkZ, info.lowerBound - info.clusterSize, info.upperBound + info.clusterSize, (pos) -> {
         if (this.canPlaceBlock(world, pos) && pos.distanceSq(src) < (double)(info.clusterSize * info.clusterSize)) {
            world.setBlockState(pos, this.state, 0);
         }

      });
   }

   public boolean canPlaceBlock(World world, BlockPos pos) {
      return world.getBlockState(pos).getBlock() == Blocks.STONE;
   }

   public BlockPos[] getSourcesInChunk(Random random, int chunkX, int chunkZ) {
      RevampStoneGen.StoneInfo info = (RevampStoneGen.StoneInfo)this.infoSupplier.get();
      int chance = info.clusterRarity;
      int amount = 1;
      if (info.clustersRarityPerChunk) {
         chance = 1;
         amount = info.clusterRarity;
      }

      BlockPos[] sources;
      if (chance > 0 && random.nextInt(chance) == 0) {
         sources = new BlockPos[amount];
         int lower = Math.abs(info.lowerBound);
         int range = Math.abs(info.upperBound - info.lowerBound);

         for(int i = 0; i < amount && range > 0; ++i) {
            int x = chunkX * 16 + random.nextInt(16) + 8;
            int y = random.nextInt(range) + lower;
            int z = chunkZ * 16 + random.nextInt(16) + 8;
            BlockPos pos = new BlockPos(x, y, z);
            sources[i] = pos;
         }
      } else {
         sources = new BlockPos[0];
      }

      return sources;
   }
}
