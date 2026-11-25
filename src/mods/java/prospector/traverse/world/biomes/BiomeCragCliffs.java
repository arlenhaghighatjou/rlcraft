package prospector.traverse.world.biomes;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSand.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import prospector.traverse.config.TraverseConfig;
import prospector.traverse.init.TraverseBlocks;
import prospector.traverse.util.TUtils;
import prospector.traverse.world.WorldGenConstants;
import prospector.traverse.world.features.WorldGenPlant;

public class BiomeCragCliffs extends Biome implements WorldGenConstants {
   protected static final WorldGenBlockBlob BOULDER_FEATURE = new WorldGenBlockBlob((Block)TraverseBlocks.blocks.get("blue_rock_cobblestone"), 2);
   public static final WorldGenerator COLD_GRASS_FEATURE = new WorldGenPlant(((Block)TraverseBlocks.blocks.get("cold_grass")).getDefaultState());
   public static IBlockState blueRock;
   public static BiomeProperties properties = new BiomeProperties("Crag Cliffs");

   public void decorate(World worldIn, EaglercraftRandom rand, BlockPos pos) {
      if (!TraverseConfig.disallowBoulders && TerrainGen.decorate(worldIn, rand, pos, EventType.ROCK)) {
         int genChance = rand.nextInt(9);
         if (genChance == 0) {
            int k6 = rand.nextInt(16) + 8;
            int l = rand.nextInt(16) + 8;
            BlockPos blockpos = worldIn.getHeight(pos.add(k6, 0, l));
            BOULDER_FEATURE.generate(worldIn, rand, blockpos);
         }
      }

      super.decorate(worldIn, rand, pos);
   }

   public BiomeCragCliffs() {
      super(properties);
      blueRock = TUtils.getBlock("blue_rock").getDefaultState();
      this.topBlock = blueRock;
      this.fillerBlock = Blocks.STONE.getDefaultState();
      this.decorator.treesPerChunk = -999;
      this.decorator.extraTreeChance = -999.0F;
      this.decorator.flowersPerChunk = -999;
      this.decorator.grassPerChunk = 2;
      this.decorator.generateFalls = false;
      this.spawnableCreatureList.clear();
      this.spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 2, 1, 2));
   }

   public int getModdedBiomeGrassColor(int original) {
      return super.getModdedBiomeGrassColor(-7306931);
   }

   public WorldGenerator getRandomWorldGenForGrass(EaglercraftRandom rand) {
      return COLD_GRASS_FEATURE;
   }

   public int getSkyColorByTemp(float currentTemperature) {
      return TraverseConfig.disableCustomSkies ? super.getSkyColorByTemp(currentTemperature) : -7807489;
   }

   public int getModdedBiomeFoliageColor(int original) {
      return super.getModdedBiomeFoliageColor(-6389427);
   }

   public void genTerrainBlocks(World worldIn, EaglercraftRandom rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      int i = worldIn.getSeaLevel();
      IBlockState iblockstate = this.topBlock;
      IBlockState iblockstate1 = this.fillerBlock;
      int j = -1;
      int k = (int)(noiseVal / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
      int l = x & 15;
      int i1 = z & 15;
      MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos();

      for(int j1 = 255; j1 >= 0; --j1) {
         if (j1 <= rand.nextInt(5)) {
            chunkPrimerIn.setBlockState(i1, j1, l, BEDROCK);
         } else {
            IBlockState iblockstate2 = chunkPrimerIn.getBlockState(i1, j1, l);
            if (iblockstate2.getMaterial() == Material.AIR) {
               j = -1;
            } else if (iblockstate2.getBlock() == Blocks.STONE) {
               if (j == -1) {
                  if (k <= 0) {
                     iblockstate = AIR;
                     iblockstate1 = STONE;
                  } else if (j1 >= i - 4 && j1 <= i + 1) {
                     iblockstate = this.topBlock;
                     iblockstate1 = this.fillerBlock;
                  }

                  if (j1 < i && (iblockstate == null || iblockstate.getMaterial() == Material.AIR)) {
                     if (this.getTemperature(blockpos$mutableblockpos.setPos(x, j1, z)) < 0.15F) {
                        iblockstate = ICE;
                     } else {
                        iblockstate = WATER;
                     }
                  }

                  j = k;
                  if (j1 >= i - 1) {
                     chunkPrimerIn.setBlockState(i1, j1, l, iblockstate);
                  } else if (j1 < i - 7 - k) {
                     iblockstate = AIR;
                     iblockstate1 = STONE;
                     chunkPrimerIn.setBlockState(i1, j1, l, GRAVEL);
                  } else {
                     chunkPrimerIn.setBlockState(i1, j1, l, iblockstate1);
                  }
               } else if (j > 0) {
                  --j;
                  chunkPrimerIn.setBlockState(i1, j1, l, iblockstate1);
                  if (j == 0 && iblockstate1.getBlock() == Blocks.SAND && k > 1) {
                     j = rand.nextInt(4) + Math.max(0, j1 - 63);
                     iblockstate1 = iblockstate1.getValue(BlockSand.VARIANT) == EnumType.RED_SAND ? RED_SANDSTONE : SANDSTONE;
                  }

                  if (j == 0 && iblockstate == blueRock && k > 1) {
                     j = rand.nextInt(4) + Math.max(0, j1 - 63);
                     iblockstate = blueRock;
                  }

                  if (j == 0 && iblockstate1 == blueRock && k > 1) {
                     j = rand.nextInt(4) + Math.max(0, j1 - 63);
                     iblockstate1 = blueRock;
                  }
               }
            }
         }
      }

   }

   static {
      properties.setTemperature(0.4F);
      properties.setRainfall(0.0F);
      properties.setBaseHeight(3.7F);
      properties.setHeightVariation(0.5F);
   }
}
