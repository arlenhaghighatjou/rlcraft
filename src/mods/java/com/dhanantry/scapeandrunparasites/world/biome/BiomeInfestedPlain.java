package com.dhanantry.scapeandrunparasites.world.biome;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBush;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTree;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTreeThin;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.BiomeProperties;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeInfestedPlain extends BiomeParasite {
   public static WorldGenAbstractTree treeP = new WorldGenParasiteTree(false);
   public static WorldGenAbstractTree treePT = new WorldGenParasiteTreeThin(false);
   public WorldGenerator grassP1 = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.EYE, 1);
   public WorldGenerator grassP2 = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.POP, 2);

   public BiomeInfestedPlain() {
      super((new BiomeProperties("Parasite Biome")).setBaseHeight(0.13F).setHeightVariation(0.5F).setWaterColor(16711680));
      this.decorator.treesPerChunk = 1;
      this.decorator.extraTreeChance = 0.0F;
      this.decorator.grassPerChunk = 15;
      this.decorator.flowersPerChunk = 4;
      this.decorator.deadBushPerChunk = 2;
      this.decorator.cactiPerChunk = 15;
      this.decorator.bigMushroomsPerChunk = 1;
   }

   public WorldGenAbstractTree getRandomTreeFeature(EaglercraftRandom rand) {
      return rand.nextInt(3) == 0 ? treePT : treeP;
   }

   public WorldGenerator getRandomWorldGenForGrass(EaglercraftRandom rand) {
      return rand.nextInt(2) == 0 ? this.grassP2 : this.grassP1;
   }

   public void decorate(World worldIn, EaglercraftRandom rand, BlockPos pos) {
      super.decorate(worldIn, rand, pos);
   }

   public void genTerrainBlocks(World worldIn, EaglercraftRandom rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      super.genTerrainBlocks(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
      double d0 = 1.0D;
      if (d0 > 0.0D) {
         int i = x & 15;
         int j = z & 15;

         for(int k = 255; k >= 0; --k) {
            if (chunkPrimerIn.getBlockState(j, k, i).getMaterial() != Material.AIR) {
               if (k == 62 && chunkPrimerIn.getBlockState(j, k, i).getBlock() != Blocks.WATER) {
                  chunkPrimerIn.setBlockState(j, k, i, WATER);
               }
               break;
            }
         }
      }

   }
}
