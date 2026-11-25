package com.github.alexthe666.iceandfire.world;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class WorldGenAnimalFarm extends WorldGenerator {
   public WorldGenAnimalFarm() {
   }

   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (worldIn == null) {
         return false;
      } else {
         Biome biome = worldIn.getBiome(position);
         boolean sandy = BiomeDictionary.hasType(biome, Type.SANDY);
         Block fence = Blocks.OAK_FENCE;
         Block fence_gate = Blocks.OAK_FENCE_GATE;
         if (BiomeDictionary.hasType(biome, Type.SAVANNA)) {
            fence = Blocks.ACACIA_FENCE;
            fence_gate = Blocks.ACACIA_FENCE_GATE;

            for(int animals = 0; animals < rand.nextInt(2) + 1; ++animals) {
               EntityAnimal animal = new EntityCow(worldIn);
               animal.setPositionAndRotation((double)((float)position.getX() + 0.5F + (float)(-3 + rand.nextInt(6))), (double)((float)position.getY() + 1.5F), (double)((float)position.getZ() + 0.5F + (float)(-3 + rand.nextInt(6))), rand.nextFloat() * 360.0F, 0.0F);
               worldIn.spawnEntity(animal);
            }
         } else if (BiomeDictionary.hasType(biome, Type.CONIFEROUS)) {
            fence = Blocks.SPRUCE_FENCE;
            fence_gate = Blocks.SPRUCE_FENCE_GATE;

            for(int animals = 0; animals < rand.nextInt(2) + 1; ++animals) {
               EntityAnimal animal = new EntitySheep(worldIn);
               animal.setPositionAndRotation((double)((float)position.getX() + 0.5F + (float)(-3 + rand.nextInt(6))), (double)((float)position.getY() + 1.5F), (double)((float)position.getZ() + 0.5F + (float)(-3 + rand.nextInt(6))), rand.nextFloat() * 360.0F, 0.0F);
               worldIn.spawnEntity(animal);
            }
         } else if (BiomeDictionary.hasType(biome, Type.JUNGLE)) {
            fence = Blocks.JUNGLE_FENCE;
            fence_gate = Blocks.JUNGLE_FENCE_GATE;

            for(int animals = 0; animals < rand.nextInt(2) + 1; ++animals) {
               EntityAnimal animal = new EntityChicken(worldIn);
               animal.setPositionAndRotation((double)((float)position.getX() + 0.5F + (float)(-3 + rand.nextInt(6))), (double)((float)position.getY() + 1.5F), (double)((float)position.getZ() + 0.5F + (float)(-3 + rand.nextInt(6))), rand.nextFloat() * 360.0F, 0.0F);
               worldIn.spawnEntity(animal);
            }
         } else if (BiomeDictionary.hasType(biome, Type.FOREST) && BiomeDictionary.hasType(biome, Type.DENSE)) {
            fence = Blocks.DARK_OAK_FENCE;
            fence_gate = Blocks.DARK_OAK_FENCE_GATE;
         } else if (biome == Biomes.BIRCH_FOREST || biome == Biomes.BIRCH_FOREST_HILLS || biome == Biomes.MUTATED_BIRCH_FOREST || biome == Biomes.MUTATED_BIRCH_FOREST_HILLS) {
            fence = Blocks.BIRCH_FENCE;
            fence_gate = Blocks.BIRCH_FENCE_GATE;
         }

         if (BiomeDictionary.hasType(biome, Type.SANDY)) {
            for(int animals = 0; animals < rand.nextInt(2) + 1; ++animals) {
               EntityAnimal animal = new EntityPig(worldIn);
               animal.setPositionAndRotation((double)((float)position.getX() + 0.5F + (float)(-3 + rand.nextInt(6))), (double)((float)position.getY() + 1.5F), (double)((float)position.getZ() + 0.5F + (float)(-3 + rand.nextInt(6))), rand.nextFloat() * 360.0F, 0.0F);
               worldIn.spawnEntity(animal);
            }
         }

         if (BiomeDictionary.hasType(biome, Type.PLAINS)) {
            for(int animals = 0; animals < rand.nextInt(2) + 1; ++animals) {
               EntityAnimal animal = new EntityChicken(worldIn);
               animal.setPositionAndRotation((double)((float)position.getX() + 0.5F + (float)(-3 + rand.nextInt(6))), (double)((float)position.getY() + 1.5F), (double)((float)position.getZ() + 0.5F + (float)(-3 + rand.nextInt(6))), rand.nextFloat() * 360.0F, 0.0F);
               worldIn.spawnEntity(animal);
            }
         }

         for(int x = -4; x < 5; ++x) {
            for(int z = -4; z < 5; ++z) {
               if ((x % 4 == 0 || z % 4 == 0 || x % -4 == 0 || z % -4 == 0) && Math.abs(x) != 0 && Math.abs(z) != 0) {
                  worldIn.setBlockState(position.add(x, 0, z), sandy ? Blocks.SAND.getDefaultState() : Blocks.GRASS.getDefaultState());
                  worldIn.setBlockState(position.add(x, 1, z), fence.getDefaultState());
               } else {
                  worldIn.setBlockState(position.add(x, 0, z), Blocks.GRASS_PATH.getDefaultState());
               }

               if (x == 0) {
                  worldIn.setBlockState(position.add(0, 1, 4), fence_gate.getDefaultState().withProperty(BlockFenceGate.FACING, EnumFacing.SOUTH));
                  worldIn.setBlockState(position.add(0, 1, -4), fence_gate.getDefaultState().withProperty(BlockFenceGate.FACING, EnumFacing.NORTH));
                  worldIn.setBlockState(position.add(0, 0, 4), Blocks.GRASS_PATH.getDefaultState());
                  worldIn.setBlockState(position.add(0, 0, -4), Blocks.GRASS_PATH.getDefaultState());
               }

               if (z == 0) {
                  worldIn.setBlockState(position.add(4, 1, 0), fence_gate.getDefaultState().withProperty(BlockFenceGate.FACING, EnumFacing.EAST));
                  worldIn.setBlockState(position.add(-4, 1, 0), fence_gate.getDefaultState().withProperty(BlockFenceGate.FACING, EnumFacing.WEST));
                  worldIn.setBlockState(position.add(4, 0, 0), Blocks.GRASS_PATH.getDefaultState());
                  worldIn.setBlockState(position.add(-4, 0, 0), Blocks.GRASS_PATH.getDefaultState());
               }
            }
         }

         return true;
      }
   }
}
