package prospector.traverse.world.biomes;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenShrub;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import prospector.traverse.config.TraverseConfig;
import prospector.traverse.world.WorldGenConstants;
import prospector.traverse.world.features.WorldGenFallenTree;

public class BiomeBadlands extends Biome implements WorldGenConstants {
   protected static final WorldGenFallenTree FALLEN_TREE_FEATURE = new WorldGenFallenTree(true);
   protected static final WorldGenBlockBlob COBBLESTONE_BOULDER_FEATURE = new WorldGenBlockBlob(Blocks.COBBLESTONE, 1);
   protected static final WorldGenLakes LAVA_LAKE_FEATURE = new WorldGenLakes(Blocks.LAVA);
   public static BiomeProperties properties = new BiomeProperties("Badlands");

   public BiomeBadlands() {
      super(properties);
      this.decorator.treesPerChunk = 1;
      this.decorator.extraTreeChance = 4.0F;
      this.decorator.flowersPerChunk = 0;
      this.decorator.grassPerChunk = 16;
      this.spawnableCreatureList.clear();
      this.spawnableCreatureList.add(new SpawnListEntry(EntitySheep.class, 4, 2, 4));
      this.spawnableCreatureList.add(new SpawnListEntry(EntityPig.class, 3, 2, 4));
      this.spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 3, 2, 4));
      this.spawnableCreatureList.add(new SpawnListEntry(EntityCow.class, 2, 2, 4));
      this.spawnableCreatureList.add(new SpawnListEntry(EntityRabbit.class, 1, 2, 7));
   }

   public int getModdedBiomeGrassColor(int original) {
      return super.getModdedBiomeGrassColor(-2373764);
   }

   public int getSkyColorByTemp(float currentTemperature) {
      return TraverseConfig.disableCustomSkies ? super.getSkyColorByTemp(currentTemperature) : -12650;
   }

   public int getModdedBiomeFoliageColor(int original) {
      return super.getModdedBiomeFoliageColor(-4013720);
   }

   public WorldGenAbstractTree getRandomTreeFeature(Random rand) {
      int num = rand.nextInt(2);
      return (WorldGenAbstractTree)(num == 0 ? new WorldGenShrub(OAK_LOG, OAK_LEAVES) : TREE_FEATURE);
   }

   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      if (noiseVal > 1.5D) {
         this.topBlock = Blocks.STONE.getDefaultState();
         this.fillerBlock = Blocks.STONE.getDefaultState();
      } else {
         this.topBlock = Blocks.GRASS.getDefaultState();
         this.fillerBlock = Blocks.DIRT.getDefaultState();
      }

      this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
   }

   public void decorate(World worldIn, Random rand, BlockPos pos) {
      if (TerrainGen.decorate(worldIn, rand, pos, EventType.TREE)) {
         int fallenTreeChance = rand.nextInt(3);
         if (fallenTreeChance == 0) {
            int k6 = rand.nextInt(16) + 8;
            int l = rand.nextInt(16) + 8;
            BlockPos blockpos = worldIn.getHeight(pos.add(k6, 0, l));
            FALLEN_TREE_FEATURE.generate(worldIn, rand, blockpos);
         }
      }

      if (!TraverseConfig.disallowBoulders && TerrainGen.decorate(worldIn, rand, pos, EventType.ROCK)) {
         int boulderChance = rand.nextInt(5);
         if (boulderChance == 0) {
            int k6 = rand.nextInt(16) + 8;
            int l = rand.nextInt(16) + 8;
            BlockPos blockpos = worldIn.getHeight(pos.add(k6, 0, l));
            COBBLESTONE_BOULDER_FEATURE.generate(worldIn, rand, blockpos);
         }
      }

      if (TerrainGen.decorate(worldIn, rand, pos, EventType.LAKE_LAVA)) {
         int boulderChance = rand.nextInt(12);
         if (boulderChance == 0) {
            int k6 = rand.nextInt(16) + 8;
            int l = rand.nextInt(16) + 8;
            BlockPos blockpos = worldIn.getHeight(pos.add(k6, 0, l));
            LAVA_LAKE_FEATURE.generate(worldIn, rand, blockpos);
         }
      }

      if (TerrainGen.decorate(worldIn, rand, pos, EventType.GRASS)) {
         DOUBLE_PLANT_GENERATOR.setPlantType(EnumPlantType.GRASS);

         for(int i = 0; i < 7; ++i) {
            int j = rand.nextInt(16) + 8;
            int k = rand.nextInt(16) + 8;
            int l = rand.nextInt(worldIn.getHeight(pos.add(j, 0, k)).getY() + 32);
            DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(j, l, k));
         }
      }

      super.decorate(worldIn, rand, pos);
   }

   static {
      properties.setTemperature(0.6F);
      properties.setRainfall(0.1F);
      properties.setBaseHeight(0.6F);
      properties.setHeightVariation(0.55F);
   }
}
