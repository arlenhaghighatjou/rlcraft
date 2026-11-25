package com.ferreusveritas.dynamictrees.worldgen;

import com.ferreusveritas.dynamictrees.api.worldgen.IRadiusCoordinator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class BiomeRadiusCoordinator implements IRadiusCoordinator {
   public NoiseGeneratorPerlin noiseGenerator = new NoiseGeneratorPerlin(new Random(96L), 1);
   protected final TreeGenerator treeGenerator;
   protected final World world;
   protected int pass;
   protected Function<Integer, Integer> chunkMultipass;

   public BiomeRadiusCoordinator(TreeGenerator treeGenerator, World world) {
      this.world = world;
      this.treeGenerator = treeGenerator;
   }

   public int getRadiusAtCoords(int x, int z) {
      int rad = ((Integer)this.chunkMultipass.apply(Integer.valueOf(this.pass))).intValue();
      if (rad >= 2 && rad <= 8) {
         return rad;
      } else {
         double scale = 128.0D;
         Biome biome = this.world.getBiome(new BlockPos(x + 8, 0, z + 8));
         double noiseDensity = (this.noiseGenerator.getValue((double)x / scale, (double)z / scale) + 1.0D) / 2.0D;
         double density = this.treeGenerator.getBiomeDataBase(this.world).getDensity(biome).getDensity(this.world.rand, noiseDensity);
         double size = (1.0D - density) * 9.0D;
         int kindaRandom = (x * 674365771 ^ z * 254326997) >> 4;
         int shakelow = (kindaRandom & 3) % 3;
         int shakehigh = (kindaRandom & 12) % 3;
         return MathHelper.clamp((int)size, 2 + shakelow, 8 - shakehigh);
      }
   }

   public boolean runPass(int chunkX, int chunkZ, int pass) {
      this.pass = pass;
      if (pass == 0) {
         Biome biome = this.world.getBiome(new BlockPos((chunkX << 4) + 8, 0, (chunkZ << 4) + 8));
         this.chunkMultipass = this.treeGenerator.getBiomeDataBase(this.world).getMultipass(biome);
      }

      return ((Integer)this.chunkMultipass.apply(Integer.valueOf(pass))).intValue() >= 0;
   }
}
