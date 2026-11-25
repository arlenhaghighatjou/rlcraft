package prospector.traverse.world.biomes;

import java.util.Random;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import prospector.traverse.config.TraverseConfig;
import prospector.traverse.world.WorldGenConstants;
import prospector.traverse.world.features.TraverseTreeGenerator;

public class BiomeAutumnalWoodedHills extends Biome implements WorldGenConstants {
   public static BiomeProperties properties = new BiomeProperties("Autumnal Wooded Hills");

   public BiomeAutumnalWoodedHills() {
      super(properties);
      this.decorator.treesPerChunk = 10;
      this.decorator.flowersPerChunk = 4;
      this.decorator.grassPerChunk = 6;
      this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 5, 4, 4));
   }

   public int getModdedBiomeGrassColor(int original) {
      return super.getModdedBiomeGrassColor(-2702787);
   }

   public int getSkyColorByTemp(float currentTemperature) {
      return TraverseConfig.disableCustomSkies ? super.getSkyColorByTemp(currentTemperature) : -1053479;
   }

   public int getModdedBiomeFoliageColor(int original) {
      return super.getModdedBiomeFoliageColor(-2960609);
   }

   public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
      int num = rand.nextInt(5);
      if (num == 0) {
         return new TraverseTreeGenerator(true, 4, OAK_LOG, RED_AUTUMNAL_LEAVES);
      } else if (num == 1) {
         return new TraverseTreeGenerator(true, 4, OAK_LOG, BROWN_AUTUMNAL_LEAVES);
      } else if (num == 2) {
         return new TraverseTreeGenerator(true, 4, OAK_LOG, ORANGE_AUTUMNAL_LEAVES);
      } else {
         return (WorldGenAbstractTree)(num == 3 ? new TraverseTreeGenerator(true, 4, OAK_LOG, YELLOW_AUTUMNAL_LEAVES) : TREE_FEATURE);
      }
   }

   static {
      properties.setTemperature(Biomes.FOREST.getDefaultTemperature());
      properties.setRainfall(Biomes.FOREST.getRainfall());
      properties.setBaseHeight(Biomes.EXTREME_HILLS.getBaseHeight());
      properties.setHeightVariation(Biomes.EXTREME_HILLS.getHeightVariation());
   }
}
