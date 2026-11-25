package prospector.traverse.world.biomes;

import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSand.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.BiomeDesert;
import net.minecraft.world.biome.Biome.BiomeProperties;
import prospector.traverse.config.TraverseConfig;
import prospector.traverse.world.WorldGenConstants;

public class BiomeRedDesert extends BiomeDesert implements WorldGenConstants {
   protected static final IBlockState RED_SAND = Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT, EnumType.RED_SAND);
   public static BiomeProperties properties = new BiomeProperties("Red Desert");

   public BiomeRedDesert() {
      super(properties);
      this.topBlock = RED_SAND;
      this.fillerBlock = RED_SAND;
      this.decorator.generateFalls = false;
   }

   public int getSkyColorByTemp(float currentTemperature) {
      return TraverseConfig.disableCustomSkies ? super.getSkyColorByTemp(currentTemperature) : -6691;
   }

   static {
      properties.setTemperature(Biomes.DESERT.getDefaultTemperature());
      properties.setRainfall(Biomes.DESERT.getRainfall());
      properties.setRainDisabled();
      properties.setBaseHeight(Biomes.DESERT.getBaseHeight());
      properties.setHeightVariation(Biomes.DESERT.getHeightVariation());
   }
}
