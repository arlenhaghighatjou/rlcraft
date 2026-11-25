package prospector.traverse.world.biomes;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockFlower.EnumFlowerColor;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import prospector.traverse.world.WorldGenConstants;

public class BiomeLushHills extends Biome implements WorldGenConstants {
   public static BiomeProperties properties = new BiomeProperties("Lush Hills");

   public BiomeLushHills() {
      super(properties);
      this.decorator.treesPerChunk = 2;
      this.decorator.flowersPerChunk = 10;
      this.decorator.grassPerChunk = 3;
      this.flowers.clear();

      for(EnumFlowerType type : EnumFlowerType.values()) {
         if (type.getBlockType() != EnumFlowerColor.YELLOW) {
            if (type == EnumFlowerType.BLUE_ORCHID) {
               type = EnumFlowerType.POPPY;
            }

            this.addFlower(Blocks.RED_FLOWER.getDefaultState().withProperty(Blocks.RED_FLOWER.getTypeProperty(), type), 10);
         }
      }

   }

   public EnumFlowerType pickRandomFlower(EaglercraftRandom rand, BlockPos pos) {
      double d0 = GRASS_COLOR_NOISE.getValue((double)pos.getX() / 200.0D, (double)pos.getZ() / 200.0D);
      if (d0 < -0.9D) {
         int j = rand.nextInt(4);
         switch(j) {
         case 0:
            return EnumFlowerType.ORANGE_TULIP;
         case 1:
            return EnumFlowerType.RED_TULIP;
         case 2:
            return EnumFlowerType.PINK_TULIP;
         case 3:
         default:
            return EnumFlowerType.WHITE_TULIP;
         }
      } else if (rand.nextInt(3) > 0) {
         int i = rand.nextInt(3);
         return i == 0 ? EnumFlowerType.POPPY : (i == 1 ? EnumFlowerType.HOUSTONIA : EnumFlowerType.OXEYE_DAISY);
      } else {
         return EnumFlowerType.DANDELION;
      }
   }

   public int getModdedBiomeGrassColor(int original) {
      return super.getModdedBiomeGrassColor(-8396738);
   }

   public int getModdedBiomeFoliageColor(int original) {
      return super.getModdedBiomeFoliageColor(-10950093);
   }

   public WorldGenAbstractTree getRandomTreeFeature(EaglercraftRandom rand) {
      return TREE_FEATURE;
   }

   static {
      properties.setTemperature(0.5F);
      properties.setRainfall(0.8F);
      properties.setBaseHeight(0.4F);
      properties.setHeightVariation(0.3F);
   }
}
