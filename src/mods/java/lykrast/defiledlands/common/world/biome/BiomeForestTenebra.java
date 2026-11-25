package lykrast.defiledlands.common.world.biome;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome.BiomeProperties;

public class BiomeForestTenebra extends BiomeDefiled {
   public static BiomeProperties properties = new BiomeProperties("Tenebra Forest");

   public BiomeForestTenebra() {
      super(properties);
      this.decorator.treesPerChunk = 10;
   }

   static {
      properties.setTemperature(Biomes.FOREST.getDefaultTemperature());
      properties.setRainfall(Biomes.FOREST.getRainfall());
      properties.setBaseHeight(Biomes.FOREST.getBaseHeight());
      properties.setHeightVariation(Biomes.FOREST.getHeightVariation());
   }
}
