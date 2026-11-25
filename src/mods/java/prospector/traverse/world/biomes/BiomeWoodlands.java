package prospector.traverse.world.biomes;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockFlower.EnumFlowerColor;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenShrub;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import prospector.traverse.config.TraverseConfig;
import prospector.traverse.world.WorldGenConstants;
import prospector.traverse.world.features.WorldGenFallenTree;

public class BiomeWoodlands extends Biome implements WorldGenConstants {
   protected static final WorldGenFallenTree FALLEN_TREE_FEATURE = new WorldGenFallenTree(true);
   public static BiomeProperties properties = new BiomeProperties("Woodlands");

   public BiomeWoodlands() {
      super(properties);
      this.decorator.treesPerChunk = 4;
      this.decorator.flowersPerChunk = 6;
      this.decorator.grassPerChunk = 16;
      this.spawnableCreatureList.add(new SpawnListEntry(EntityWolf.class, 2, 4, 4));
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
      if (d0 < -0.8D) {
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
      return super.getModdedBiomeGrassColor(-6706859);
   }

   public int getSkyColorByTemp(float currentTemperature) {
      return TraverseConfig.disableCustomSkies ? super.getSkyColorByTemp(currentTemperature) : -7818015;
   }

   public int getModdedBiomeFoliageColor(int original) {
      return super.getModdedBiomeFoliageColor(-8085942);
   }

   public WorldGenAbstractTree getRandomTreeFeature(EaglercraftRandom rand) {
      int num = rand.nextInt(3);
      return (WorldGenAbstractTree)(num == 0 ? new WorldGenShrub(OAK_LOG, OAK_LEAVES) : TREE_FEATURE);
   }

   public void decorate(World worldIn, EaglercraftRandom rand, BlockPos pos) {
      if (TerrainGen.decorate(worldIn, rand, pos, EventType.TREE)) {
         int genChance = rand.nextInt(5);
         if (genChance == 0) {
            int k6 = rand.nextInt(16) + 8;
            int l = rand.nextInt(16) + 8;
            BlockPos blockpos = worldIn.getHeight(pos.add(k6, 0, l));
            FALLEN_TREE_FEATURE.generate(worldIn, rand, blockpos);
         }
      }

      super.decorate(worldIn, rand, pos);
   }

   static {
      properties.setTemperature(0.8F);
      properties.setRainfall(0.4F);
      properties.setBaseHeight(0.2F);
      properties.setHeightVariation(0.05F);
   }
}
