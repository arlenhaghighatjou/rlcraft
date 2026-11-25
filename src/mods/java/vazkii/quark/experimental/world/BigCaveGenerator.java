package vazkii.quark.experimental.world;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;
import vazkii.quark.experimental.features.BiggerCaves;

public class BigCaveGenerator extends MapGenCaves {
   private final Random rand2 = new Random();

   public BigCaveGenerator() {
   }

   protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int originalX, int originalZ, @Nonnull ChunkPrimer chunkPrimerIn) {
      int i = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(15) + 1) + 1);
      this.rand2.setSeed(worldIn.getSeed());
      if (this.rand.nextInt(7) != 0) {
         i = 0;
      }

      for(int j = 0; j < i; ++j) {
         double d0 = (double)(chunkX * 16 + this.rand.nextInt(16));
         double d1 = (double)this.rand.nextInt(this.rand.nextInt(120) + 8);
         double d2 = (double)(chunkZ * 16 + this.rand.nextInt(16));
         int k = 1;
         if (this.rand.nextInt(4) == 0) {
            this.addRoom(this.rand.nextLong(), originalX, originalZ, chunkPrimerIn, d0, d1, d2);
            k += this.rand.nextInt(4);
         }

         for(int l = 0; l < k; ++l) {
            float f = this.rand.nextFloat() * 6.2831855F;
            float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float f2 = this.rand.nextFloat() * BiggerCaves.overallCaveSizeVariance + BiggerCaves.overallCaveSizeBase;
            if (this.rand.nextInt(10) == 0) {
               f2 *= this.rand.nextFloat() * this.rand.nextFloat() * BiggerCaves.bigCaveSizeVariance + BiggerCaves.bigCaveSizeBase;
            }

            if (BiggerCaves.generateHugeCaves && this.rand2.nextInt(BiggerCaves.hugeCaveChance) == 0 && d1 < (double)BiggerCaves.hugeCaveMaxY) {
               f2 = BiggerCaves.hugeCaveSizeBase + this.rand2.nextFloat() * BiggerCaves.hugeCaveSizeVariance;
            }

            this.addTunnel(this.rand.nextLong(), originalX, originalZ, chunkPrimerIn, d0, d1, d2, f2, f, f1, 0, 0, 1.0D);
         }
      }

   }
}
