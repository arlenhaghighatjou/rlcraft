package prospector.traverse.world.biomes;

import java.util.Random;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenFossils;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import prospector.traverse.world.WorldGenConstants;
import prospector.traverse.world.features.WorldGenCustomSwampTree;

public class BiomeLushSwamp extends Biome implements WorldGenConstants {
   protected static final WorldGenCustomSwampTree CUSTOM_SWAMP_TREE_FEATURE = new WorldGenCustomSwampTree(true, 7);
   protected static final IBlockState WATER_LILY = Blocks.WATERLILY.getDefaultState();
   public static BiomeProperties properties = new BiomeProperties("Lush Swamp");

   public BiomeLushSwamp() {
      super(properties);
      this.decorator.treesPerChunk = 2;
      this.decorator.flowersPerChunk = 5;
      this.decorator.deadBushPerChunk = 1;
      this.decorator.mushroomsPerChunk = 4;
      this.decorator.reedsPerChunk = 10;
      this.decorator.clayPerChunk = 1;
      this.decorator.waterlilyPerChunk = 4;
      this.decorator.sandPatchesPerChunk = 0;
      this.decorator.grassPerChunk = 10;
      this.spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 1, 1, 1));
   }

   public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
      return CUSTOM_SWAMP_TREE_FEATURE;
   }

   public EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
      return EnumFlowerType.BLUE_ORCHID;
   }

   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      double d0 = GRASS_COLOR_NOISE.getValue((double)x * 0.25D, (double)z * 0.25D);
      if (d0 > 0.0D) {
         int i = x & 15;
         int j = z & 15;

         for(int k = 255; k >= 0; --k) {
            if (chunkPrimerIn.getBlockState(j, k, i).getMaterial() != Material.AIR) {
               if (k == 62 && chunkPrimerIn.getBlockState(j, k, i).getBlock() != Blocks.WATER) {
                  chunkPrimerIn.setBlockState(j, k, i, WATER);
                  if (d0 < 0.12D) {
                     chunkPrimerIn.setBlockState(j, k + 1, i, WATER_LILY);
                  }
               }
               break;
            }
         }
      }

      this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
   }

   public void decorate(World worldIn, Random rand, BlockPos pos) {
      super.decorate(worldIn, rand, pos);
      if (TerrainGen.decorate(worldIn, rand, pos, EventType.FOSSIL) && rand.nextInt(64) == 0) {
         (new WorldGenFossils()).generate(worldIn, rand, pos);
      }

   }

   public int getModdedBiomeGrassColor(int original) {
      return super.getModdedBiomeGrassColor(-11675592);
   }

   public int getModdedBiomeFoliageColor(int original) {
      return super.getModdedBiomeFoliageColor(-11675592);
   }

   public void addDefaultFlowers() {
      this.addFlower(Blocks.RED_FLOWER.getDefaultState().withProperty(Blocks.RED_FLOWER.getTypeProperty(), EnumFlowerType.BLUE_ORCHID), 10);
   }

   static {
      properties.setTemperature(Biomes.SWAMPLAND.getDefaultTemperature());
      properties.setBaseHeight(Biomes.SWAMPLAND.getBaseHeight());
      properties.setHeightVariation(Biomes.SWAMPLAND.getHeightVariation());
   }
}
