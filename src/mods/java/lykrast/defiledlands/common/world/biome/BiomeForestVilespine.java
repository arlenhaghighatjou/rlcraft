package lykrast.defiledlands.common.world.biome;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome.BiomeProperties;

public class BiomeForestVilespine extends BiomeDefiled {
   public static BiomeProperties properties = new BiomeProperties("Vilespine Forest");

   public BiomeForestVilespine() {
      super(properties);
      this.decorator.treesPerChunk = 0;
      this.decorator.extraTreeChance = 0.05F;
      this.vilespinePerChunk = 500;
   }

   static {
      properties.setTemperature(Biomes.FOREST.getDefaultTemperature());
      properties.setRainfall(Biomes.FOREST.getRainfall());
      properties.setBaseHeight(Biomes.FOREST.getBaseHeight());
      properties.setHeightVariation(Biomes.JUNGLE.getHeightVariation());
   }
}
