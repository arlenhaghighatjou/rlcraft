package prospector.traverse.world.biomes;

import java.util.Random;
import net.minecraft.block.BlockFlower.EnumFlowerColor;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import prospector.traverse.world.WorldGenConstants;

public class BiomeMeadow extends Biome implements WorldGenConstants {
   public static BiomeProperties properties = new BiomeProperties("Meadow");

   public BiomeMeadow() {
      super(properties);
      this.decorator.treesPerChunk = 0;
      this.decorator.extraTreeChance = 0.2F;
      this.decorator.flowersPerChunk = 15;
      this.decorator.grassPerChunk = 15;
      this.decorator.reedsPerChunk = 2;
      this.spawnableCreatureList.add(new SpawnListEntry(EntityHorse.class, 5, 2, 4));
      this.spawnableCreatureList.add(new SpawnListEntry(EntityDonkey.class, 1, 1, 2));
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

   public EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
      double d0 = GRASS_COLOR_NOISE.getValue((double)pos.getX() / 200.0D, (double)pos.getZ() / 200.0D);
      if (d0 < -0.8D) {
         int j = rand.nextInt(5);
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
      return super.getModdedBiomeGrassColor(-10105773);
   }

   public int getModdedBiomeFoliageColor(int original) {
      return super.getModdedBiomeFoliageColor(-10627510);
   }

   static {
      properties.setTemperature(0.8F);
      properties.setRainfall(0.7F);
      properties.setBaseHeight(0.1F);
      properties.setHeightVariation(0.0F);
   }
}
