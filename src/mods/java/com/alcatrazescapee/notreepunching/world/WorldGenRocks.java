package com.alcatrazescapee.notreepunching.world;

import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import com.alcatrazescapee.notreepunching.ModConfig;
import com.alcatrazescapee.notreepunching.common.blocks.BlockRock;
import com.alcatrazescapee.notreepunching.util.types.Stone;
import com.google.common.collect.Sets;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGenRocks implements IWorldGenerator {
   private static final Set<Material> MATERIALS = Sets.newHashSet(new Material[]{Material.GROUND, Material.SAND, Material.GRASS, Material.CLAY, Material.GOURD, Material.SNOW});

   public WorldGenRocks() {
   }

   public void generate(EaglercraftRandom random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
      if (world.provider != null && world.provider.getDimension() == 0 && ModConfig.GENERAL.looseRocksGeneration) {
         for(int i = 0; i < ModConfig.GENERAL.looseRocksFrequency; ++i) {
            int xCoord = chunkX * 16 + random.nextInt(16) + 8;
            int zCoord = chunkZ * 16 + random.nextInt(16) + 8;
            if ((double)random.nextFloat() < 0.5D) {
               this.placeRock(world, CoreHelpers.getTopSolidBlock(world, new BlockPos(xCoord, 0, zCoord)), random);
            }
         }
      }

   }

   private void placeRock(World world, BlockPos pos, EaglercraftRandom random) {
      IBlockState state = world.getBlockState(pos.up());
      if (!state.getMaterial().isLiquid() && !state.isOpaqueCube() && state.getBlock().isReplaceable(world, pos.up())) {
         IBlockState stateDown = world.getBlockState(pos);
         if (stateDown.isOpaqueCube() && MATERIALS.contains(stateDown.getMaterial())) {
            Stone stone = Stone.getFromBlock(world.getBlockState(pos.down(6)), random);
            Block stoneBlock = stone.isEnabled() ? BlockRock.get(stone) : BlockRock.get(Stone.STONE);
            world.setBlockState(pos.up(), stoneBlock.getDefaultState());
         }
      }

   }
}
