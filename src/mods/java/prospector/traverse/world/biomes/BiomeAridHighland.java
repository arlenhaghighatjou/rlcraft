package prospector.traverse.world.biomes;

import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDesert;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import prospector.traverse.world.WorldGenConstants;

public class BiomeAridHighland extends BiomeDesert implements WorldGenConstants {
   public static BiomeProperties properties = new BiomeProperties("Arid Highland");

   public BiomeAridHighland() {
      super(properties);
      this.topBlock = SAND;
      this.fillerBlock = SAND;
      this.decorator.generateFalls = false;
      this.decorator.treesPerChunk = 1;
   }

   public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
      return (WorldGenAbstractTree)(rand.nextInt(3) == 0 ? TREE_FEATURE : ACACIA_TREE_FEATURE);
   }

   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      if (noiseVal > 1.0D) {
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
      return super.getModdedBiomeGrassColor(-4534920);
   }

   public int getModdedBiomeFoliageColor(int original) {
      return super.getModdedBiomeFoliageColor(-8347602);
   }

   public void decorate(World worldIn, Random rand, BlockPos pos) {
      if (TerrainGen.decorate(worldIn, rand, pos, EventType.TREE) && rand.nextInt(5) == 0) {
         int k6 = rand.nextInt(16) + 8;
         int l = rand.nextInt(16) + 8;
         BlockPos blockpos = worldIn.getHeight(pos.add(k6, 0, l));
         OAK_SHRUB_FEATURE.generate(worldIn, rand, blockpos);
      }

      super.decorate(worldIn, rand, pos);
   }

   static {
      properties.setTemperature(Biomes.DESERT.getDefaultTemperature());
      properties.setRainfall(Biomes.DESERT.getRainfall());
      properties.setRainDisabled();
      properties.setBaseHeight(1.3F);
      properties.setHeightVariation(0.4F);
   }
}
