package lykrast.defiledlands.common.world.biome;

import lykrast.defiledlands.common.init.ModBlocks;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome.BiomeProperties;

public class BiomeDesertDefiled extends BiomeDefiled {
   public static BiomeProperties properties = new BiomeProperties("Defiled Desert");

   public BiomeDesertDefiled() {
      super(properties);
      this.topBlock = ModBlocks.sandDefiled.getDefaultState();
      this.fillerBlock = ModBlocks.sandDefiled.getDefaultState();
      this.decorator.treesPerChunk = -999;
   }

   static {
      properties.setTemperature(Biomes.DESERT.getDefaultTemperature());
      properties.setRainfall(Biomes.DESERT.getRainfall());
      properties.setBaseHeight(Biomes.DESERT.getBaseHeight());
      properties.setHeightVariation(Biomes.DESERT.getHeightVariation());
   }
}
