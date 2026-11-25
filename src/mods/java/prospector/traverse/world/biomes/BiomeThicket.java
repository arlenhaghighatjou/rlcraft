package prospector.traverse.world.biomes;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
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
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import prospector.traverse.world.WorldGenConstants;
import prospector.traverse.world.features.WorldGenPatch;
import prospector.traverse.world.features.WorldGenSurfacePatch;

public class BiomeThicket extends Biome implements WorldGenConstants {
   protected static final WorldGenSurfacePatch PODZOL_PATCH_FEATURE = new WorldGenSurfacePatch(Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.PODZOL), 6);
   protected static final WorldGenPatch GRAVEL_PATCH_FEATURE = new WorldGenPatch(Blocks.GRAVEL.getDefaultState(), 4);
   public static BiomeProperties properties = new BiomeProperties("Thicket");

   public BiomeThicket() {
      super(properties);
      this.decorator.treesPerChunk = 35;
      this.decorator.extraTreeChance = 4.0F;
      this.decorator.flowersPerChunk = 0;
      this.decorator.grassPerChunk = 3;
      this.spawnableCreatureList.clear();
      this.spawnableCreatureList.add(new SpawnListEntry(EntitySheep.class, 4, 2, 4));
      this.spawnableCreatureList.add(new SpawnListEntry(EntityPig.class, 3, 2, 4));
      this.spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 3, 2, 4));
      this.spawnableCreatureList.add(new SpawnListEntry(EntityCow.class, 2, 2, 4));
      this.spawnableCreatureList.add(new SpawnListEntry(EntityRabbit.class, 1, 2, 7));
   }

   public int getModdedBiomeGrassColor(int original) {
      return super.getModdedBiomeGrassColor(-9122463);
   }

   public int getModdedBiomeFoliageColor(int original) {
      return super.getModdedBiomeFoliageColor(-9588895);
   }

   public WorldGenAbstractTree getRandomTreeFeature(EaglercraftRandom rand) {
      return TALLER_OAK_TREE_FEATURE;
   }

   public void genTerrainBlocks(World worldIn, EaglercraftRandom rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      if (noiseVal > 1.5D) {
         this.topBlock = Blocks.GRASS.getDefaultState();
         this.fillerBlock = Blocks.DIRT.getDefaultState();
      } else {
         this.topBlock = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.COARSE_DIRT);
         this.fillerBlock = Blocks.DIRT.getDefaultState();
      }

      this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
   }

   public void decorate(World worldIn, EaglercraftRandom rand, BlockPos pos) {
      int podzolChance = rand.nextInt(4);
      if (podzolChance == 0) {
         int k6 = rand.nextInt(16) + 8;
         int l = rand.nextInt(16) + 8;
         BlockPos blockpos = worldIn.getHeight(pos.add(k6, 0, l));
         PODZOL_PATCH_FEATURE.generate(worldIn, rand, blockpos);
      }

      int gravelChance = rand.nextInt(5);
      if (gravelChance == 0) {
         int k6 = rand.nextInt(16) + 8;
         int l = rand.nextInt(16) + 8;
         BlockPos blockpos = worldIn.getHeight(pos.add(k6, 0, l));
         GRAVEL_PATCH_FEATURE.generate(worldIn, rand, blockpos);
      }

      if (TerrainGen.decorate(worldIn, rand, pos, EventType.TREE)) {
         int fallenTreeChance = rand.nextInt(2);
         if (fallenTreeChance == 0) {
            int k6 = rand.nextInt(16) + 8;
            int l = rand.nextInt(16) + 8;
            BlockPos blockpos = worldIn.getHeight(pos.add(k6, 0, l));
            OAK_FALLEN_TREE_FEATURE.generate(worldIn, rand, blockpos);
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

      if (TerrainGen.decorate(worldIn, rand, pos, EventType.TREE)) {
         int k6 = rand.nextInt(16) + 8;
         int l = rand.nextInt(16) + 8;
         BlockPos blockpos = worldIn.getHeight(pos.add(k6, 0, l));
         OAK_SHRUB_FEATURE.generate(worldIn, rand, blockpos);
      }

      super.decorate(worldIn, rand, pos);
   }

   static {
      properties.setTemperature(0.8F);
      properties.setRainfall(0.3F);
      properties.setBaseHeight(0.2F);
      properties.setHeightVariation(0.3F);
   }
}
