package lykrast.defiledlands.common.world.biome;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome.BiomeProperties;

public class BiomeIcePlainsDefiled extends BiomeDefiled {
   public static BiomeProperties properties = new BiomeProperties("Defiled Ice Plains");

   public BiomeIcePlainsDefiled() {
      super(properties);
   }

   static {
      properties.setTemperature(Biomes.ICE_PLAINS.getDefaultTemperature());
      properties.setRainfall(Biomes.ICE_PLAINS.getRainfall());
      properties.setBaseHeight(Biomes.ICE_PLAINS.getBaseHeight());
      properties.setHeightVariation(Biomes.ICE_PLAINS.getHeightVariation());
   }
}
