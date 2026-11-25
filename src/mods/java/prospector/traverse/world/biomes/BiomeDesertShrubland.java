package prospector.traverse.world.biomes;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDesert;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenShrub;
import prospector.traverse.world.WorldGenConstants;

public class BiomeDesertShrubland extends BiomeDesert implements WorldGenConstants {
   protected static final IBlockState SAND = Blocks.SAND.getDefaultState();
   protected static final IBlockState GRASS = Blocks.GRASS.getDefaultState();
   public static BiomeProperties properties = new BiomeProperties("Desert Shrubland");

   public BiomeDesertShrubland() {
      super(properties);
      this.topBlock = SAND;
      this.fillerBlock = SAND;
      this.decorator.generateFalls = false;
      this.decorator.treesPerChunk = 1;
   }

   public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
      return new WorldGenShrub(OAK_LOG, OAK_LEAVES);
   }

   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      if (noiseVal > 1.5D) {
         this.topBlock = GRASS;
         this.fillerBlock = SAND;
      } else {
         this.topBlock = SAND;
         this.fillerBlock = SAND;
      }

      this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
   }

   public int getSkyColorByTemp(float currentTemperature) {
      return Biomes.DESERT.getSkyColorByTemp(currentTemperature);
   }

   public int getModdedBiomeGrassColor(int original) {
      return super.getModdedBiomeGrassColor(-2044324);
   }

   static {
      properties.setTemperature(Biomes.DESERT.getDefaultTemperature());
      properties.setRainfall(Biomes.DESERT.getRainfall());
      properties.setRainDisabled();
      properties.setBaseHeight(Biomes.DESERT.getBaseHeight());
      properties.setHeightVariation(Biomes.DESERT.getHeightVariation());
   }
}
