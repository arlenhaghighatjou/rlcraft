package lykrast.defiledlands.common.world.biome;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome.BiomeProperties;

public class BiomePlainsDefiled extends BiomeDefiled {
   public static BiomeProperties properties = new BiomeProperties("Defiled Plains");

   public BiomePlainsDefiled() {
      super(properties);
      this.decorator.treesPerChunk = 0;
      this.decorator.extraTreeChance = 0.05F;
   }

   static {
      properties.setTemperature(Biomes.PLAINS.getDefaultTemperature());
      properties.setRainfall(Biomes.PLAINS.getRainfall());
      properties.setBaseHeight(Biomes.PLAINS.getBaseHeight());
      properties.setHeightVariation(Biomes.PLAINS.getHeightVariation());
   }
}
