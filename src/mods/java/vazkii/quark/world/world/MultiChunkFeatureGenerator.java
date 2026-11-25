package vazkii.quark.world.world;

import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class MultiChunkFeatureGenerator {
   public MultiChunkFeatureGenerator() {
   }

   public void generate(int chunkX, int chunkZ, World world) {
      if (this.canGenerate(world)) {
         int radius = this.getFeatureRadius();
         int chunkRadius = (int)Math.ceil((double)radius / 16.0D);
         long worldSeed = this.modifyWorldSeed(world.getSeed());
         Random worldRandom = new Random(worldSeed);
         long xSeed = worldRandom.nextLong() >> 3;
         long zSeed = worldRandom.nextLong() >> 3;
         long ourChunkSeed = xSeed * (long)chunkX + zSeed * (long)chunkZ ^ worldSeed;

         for(int x = chunkX - chunkRadius; x <= chunkX + chunkRadius; ++x) {
            for(int z = chunkZ - chunkRadius; z <= chunkZ + chunkRadius; ++z) {
               long chunkSeed = xSeed * (long)x + zSeed * (long)z ^ worldSeed;
               Random chunkRandom = new Random(chunkSeed);
               BlockPos[] sources = this.getSourcesInChunk(chunkRandom, x, z);

               for(BlockPos source : sources) {
                  if (source != null && this.isSourceValid(world, source)) {
                     this.generateChunkPart(source, chunkRandom, chunkX, chunkZ, world);
                  }
               }
            }
         }

      }
   }

   public long modifyWorldSeed(long seed) {
      return seed;
   }

   public boolean isSourceValid(World world, BlockPos pos) {
      return true;
   }

   public boolean canGenerate(World world) {
      return true;
   }

   public boolean shouldOffset() {
      return true;
   }

   public abstract int getFeatureRadius();

   public abstract void generateChunkPart(BlockPos var1, Random var2, int var3, int var4, World var5);

   public abstract BlockPos[] getSourcesInChunk(Random var1, int var2, int var3);

   public void forEachChunkBlock(int chunkX, int chunkZ, int minY, int maxY, Consumer<BlockPos> func) {
      BlockPos first = new BlockPos(chunkX * 16, 0, chunkZ * 16);
      minY = Math.max(1, minY);
      maxY = Math.min(255, maxY);
      if (this.shouldOffset()) {
         first = first.add(8, 0, 8);
      }

      for(int x = 0; x < 16; ++x) {
         for(int y = minY; y < maxY; ++y) {
            for(int z = 0; z < 16; ++z) {
               func.accept(first.add(x, y, z));
            }
         }
      }

   }

   public boolean isInsideChunk(BlockPos pos, int chunkX, int chunkZ) {
      int x = chunkX * 16;
      int z = chunkZ * 16;
      return pos.getX() > x && pos.getZ() > z && pos.getX() < x + 16 && pos.getZ() < z + 16;
   }
}
