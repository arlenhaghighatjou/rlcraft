package prospector.traverse.world.biomes;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import prospector.traverse.world.WorldGenConstants;
import prospector.traverse.world.features.WorldGenFirTree;

public class BiomeConiferousForest extends Biome implements WorldGenConstants {
   protected static final WorldGenFirTree FIR_TREE_FEATURE = new WorldGenFirTree(true);
   public final boolean isSnowy;

   public BiomeConiferousForest(boolean isSnowy) {
      super(getProperties(isSnowy));
      this.isSnowy = isSnowy;
      this.decorator.treesPerChunk = 7;
      this.decorator.flowersPerChunk = 0;
      this.decorator.grassPerChunk = 16;
      this.decorator.mushroomsPerChunk = 2;
      this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 5, 4, 4));
   }

   public static BiomeProperties getProperties(boolean isSnowy) {
      BiomeProperties properties;
      if (isSnowy) {
         properties = new BiomeProperties("Snowy Coniferous Forest");
         properties.setTemperature(0.0F);
         properties.setRainfall(0.5F);
         properties.setSnowEnabled();
      } else {
         properties = new BiomeProperties("Temperate Rainforest");
         properties.setTemperature(0.6F);
         properties.setRainfall(0.9F);
      }

      properties.setBaseHeight(0.4F);
      properties.setHeightVariation(1.1F);
      return properties;
   }

   public WorldGenAbstractTree getRandomTreeFeature(EaglercraftRandom rand) {
      return FIR_TREE_FEATURE;
   }

   public int getModdedBiomeGrassColor(int original) {
      return super.getModdedBiomeGrassColor(-13401547);
   }

   public int getModdedBiomeFoliageColor(int original) {
      return super.getModdedBiomeFoliageColor(-13401547);
   }
}
