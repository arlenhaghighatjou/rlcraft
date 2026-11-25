package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenCustomStructures;
import com.dhanantry.scapeandrunparasites.world.gen.structure.WorldGenStructure;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteBall extends WorldGenParasiteGenAbstract {
   public WorldGenParasiteBall(boolean notify) {
      super(notify);
   }

   public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos position) {
      int i = 12;
      boolean flag = true;
      if (worldIn.getBlockState(position.down()).getBlock() == Blocks.AIR) {
         return false;
      } else if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight()) {
         for(int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
            int k = 4;
            MutableBlockPos blockpos$mutableblockpos = new MutableBlockPos();

            for(int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
               for(int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                  if (j >= 0 && j < worldIn.getHeight()) {
                     if (!this.isReplaceable(worldIn, blockpos$mutableblockpos.setPos(l, j, i1))) {
                        flag = false;
                     }
                  } else {
                     flag = false;
                  }
               }
            }
         }

         if (!flag) {
            return false;
         } else {
            int extra = rand.nextInt(4);
            BlockPos current = position.up(2 + extra);
            WorldGenCustomStructures.generateInPosition(new WorldGenStructure("ball"), new EaglercraftRandom(), worldIn, current, 3);
            BlockPos center = new BlockPos(position.getX(), position.getY(), position.getZ());
            boolean skip = true;

            for(int z = 1; z <= 4; ++z) {
               if (rand.nextDouble() <= 0.5D && skip) {
                  skip = false;
               } else {
                  BlockPos root = center.up(3 + extra);
                  root = this.getDirectionRoot(root, z, 3);
                  this.placeThin(worldIn, root);
                  root = root.down();
                  this.placeThin(worldIn, root);
                  root = this.getDirectionRoot(root, z, 1);
                  this.placeThin(worldIn, root);

                  while(!worldIn.getBlockState(root.down()).isFullBlock()) {
                     root = root.down();
                     this.placeThin(worldIn, root);
                  }

                  for(int j = 1; j <= 4; ++j) {
                     BlockPos roots = this.getDirectionRoot(root, j, 1);
                     if (!worldIn.getBlockState(roots).isFullBlock()) {
                        this.placeThin(worldIn, roots);
                     }
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private void placeThin(World worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos).getBlock() == Blocks.AIR) {
         this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteThin.getDefaultState());
      } else {
         this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteTrunk.getDefaultState().withProperty(BlockParasiteTrunk.VARIANT, BlockParasiteTrunk.EnumType.TREE));
      }

   }

   private BlockPos getDirectionRoot(BlockPos center, int direction, int times) {
      switch(direction) {
      case 1:
         return center.north(times);
      case 2:
         return center.east(times);
      case 3:
         return center.south(times);
      default:
         return center.west(times);
      }
   }
}
