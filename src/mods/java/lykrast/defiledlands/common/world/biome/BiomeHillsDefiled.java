package lykrast.defiledlands.common.world.biome;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import lykrast.defiledlands.common.init.ModBlocks;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.chunk.ChunkPrimer;

public class BiomeHillsDefiled extends BiomeDefiled {
   public static BiomeProperties properties = new BiomeProperties("Defiled Hills");

   public BiomeHillsDefiled() {
      super(properties);
   }

   public void genTerrainBlocks(World worldIn, EaglercraftRandom rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      this.topBlock = ModBlocks.grassDefiled.getDefaultState();
      this.fillerBlock = ModBlocks.dirtDefiled.getDefaultState();
      if (noiseVal > 1.0D) {
         this.topBlock = STONE;
         this.fillerBlock = STONE;
      }

      this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
   }

   static {
      properties.setTemperature(Biomes.EXTREME_HILLS.getDefaultTemperature());
      properties.setRainfall(Biomes.EXTREME_HILLS.getRainfall());
      properties.setBaseHeight(Biomes.EXTREME_HILLS.getBaseHeight());
      properties.setHeightVariation(Biomes.EXTREME_HILLS.getHeightVariation());
   }
}
