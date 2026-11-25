package rtg.api.world.gen.feature;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import rtg.RTGConfig;

public class WorldGenShrubRTG extends WorldGenerator {
   private int varSize;
   private IBlockState logBlock;
   private IBlockState leaveBlock;
   private boolean varSand;

   public WorldGenShrubRTG(int size, IBlockState log, IBlockState leav, boolean sand) {
      this.varSize = size;
      this.varSand = sand;
      this.logBlock = log;
      this.leaveBlock = leav;
   }

   public boolean generate(World world, EaglercraftRandom rand, BlockPos pos) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      int width = this.varSize > 6 ? 6 : this.varSize;
      int height = this.varSize > 3 ? 2 : 1;

      for(int i = 0; i < this.varSize; ++i) {
         int rX = rand.nextInt(width * 2) - width;
         int rY = rand.nextInt(height);
         int rZ = rand.nextInt(width * 2) - width;
         if (i == 0 && this.varSize > 4) {
            this.buildLeaves(world, x + rX, y, z + rZ, 3);
         } else if (i == 1 && this.varSize > 2) {
            this.buildLeaves(world, x + rX, y, z + rZ, 2);
         } else {
            this.buildLeaves(world, x + rX, y + rY, z + rZ, 1);
         }
      }

      return true;
   }

   public void buildLeaves(World world, int x, int y, int z, int size) {
      IBlockState b = world.getBlockState(new BlockPos(x, y - 2, z));
      IBlockState b1 = world.getBlockState(new BlockPos(x, y - 1, z));
      if (b != Blocks.SAND.getDefaultState() && b1 != Blocks.SAND.getDefaultState() || RTGConfig.treesCanGenerateOnSand()) {
         if ((b.getMaterial() == Material.GRASS || b.getMaterial() == Material.GROUND || this.varSand && b.getMaterial() == Material.SAND) && b1 != Blocks.WATER.getDefaultState()) {
            if (!RTGConfig.shrubsBelowSurface() && b1.getMaterial() != Material.AIR && b1.getMaterial() != Material.VINE && b1.getMaterial() != Material.PLANTS && b1 != Blocks.SNOW_LAYER.getDefaultState()) {
               return;
            }

            for(int i = -size; i <= size; ++i) {
               for(int j = -1; j <= 1; ++j) {
                  for(int k = -size; k <= size; ++k) {
                     if (Math.abs(i) + Math.abs(j) + Math.abs(k) <= size) {
                        this.buildBlock(world, x + i, y + j, z + k, this.leaveBlock);
                     }
                  }
               }
            }

            world.setBlockState(new BlockPos(x, y - 1, z), this.logBlock, 0);
         }

      }
   }

   public void buildBlock(World world, int x, int y, int z, IBlockState block) {
      IBlockState b = world.getBlockState(new BlockPos(x, y, z));
      if (b.getBlock() != Blocks.REEDS) {
         if (b.getMaterial() == Material.AIR || b.getMaterial() == Material.VINE || b.getMaterial() == Material.PLANTS || b == Blocks.SNOW_LAYER) {
            world.setBlockState(new BlockPos(x, y, z), block, 0);
         }

      }
   }
}
