package com.dhanantry.scapeandrunparasites.world.gen;

import com.dhanantry.scapeandrunparasites.world.gen.structure.WorldGenStructure;
import java.util.ArrayList;
import java.util.Arrays;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenCustomStructures implements IWorldGenerator {
   public WorldGenCustomStructures() {
   }

   public void generate(EaglercraftRandom random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
   }

   private void generateStructure(WorldGenerator generator, World world, EaglercraftRandom random, int chunkX, int chunkZ, int chance, Block topBlock, Class<?>... classes) {
      ArrayList<Class<?>> classList = new ArrayList(Arrays.asList(classes));
      int x = chunkX * 16 + random.nextInt(15);
      int z = chunkZ * 16 + random.nextInt(15);
      int y = calculateHeight(world, x, z, topBlock);
      BlockPos pos = new BlockPos(x, y, z);
      Class<?> biome = world.provider.getBiomeForCoords(pos).getClass();
      boolean debug = true;
      if ((world.getWorldType() != WorldType.FLAT || debug) && (classList.contains(biome) || debug) && random.nextInt(chance) == 0) {
         generator.generate(world, random, pos);
      }

   }

   private static int calculateHeight(World world, int x, int z, Block topBlock) {
      int y = world.getHeight();

      Block block;
      for(boolean flag = false; !flag && y-- >= 0; flag = block == topBlock) {
         block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
      }

      return y;
   }

   public static void generateInPosition(WorldGenStructure generator, EaglercraftRandom random, World world, BlockPos pos, int offset) {
      generator.generate(world, random, new BlockPos(pos.getX() - offset, pos.getY(), pos.getZ() - offset));
   }
}
