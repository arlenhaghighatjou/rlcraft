package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteCanister;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteTreeThin extends WorldGenParasiteTreeAbstract {
   public WorldGenParasiteTreeThin(boolean notify) {
      super(notify);
   }

   public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos position) {
      int i = 16;
      boolean flag = true;
      if (worldIn.getBlockState(position.down()).getBlock() == Blocks.AIR) {
         return false;
      } else if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight()) {
         for(int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
            int k = 1;
            if (j == position.getY()) {
               k = 0;
            }

            if (j >= position.getY() + 1 + i - 2) {
               k = 2;
            }

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
            BlockPos current = position;

            while(!worldIn.getBlockState(current.down()).isFullBlock() && current.down().getY() >= 1) {
               current = current.down();
               this.placeTrunk(worldIn, current);
            }

            current = this.placeColumn(worldIn, position, 3, rand, 0.0D, 4);
            this.grow(worldIn, current.north(), rand, 1);
            this.grow(worldIn, current.east(), rand, 2);
            this.grow(worldIn, current.south(), rand, 3);
            this.grow(worldIn, current.west(), rand, 4);
            return true;
         }
      } else {
         return false;
      }
   }

   private void placeThin(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteThin.getDefaultState());
   }

   private void placeCanister(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteCanister.getDefaultState().withProperty(BlockParasiteCanister.VARIANT, BlockParasiteCanister.EnumType.LUMP));
   }

   private void placeTrunk(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteTrunk.getDefaultState().withProperty(BlockParasiteTrunk.VARIANT, BlockParasiteTrunk.EnumType.TREE));
   }

   private BlockPos placeColumn(World worldIn, BlockPos pos, int times, EaglercraftRandom rand, double extraChance, int minimum) {
      int current = pos.getY();
      int atm = current;
      BlockPos newPos = pos;
      times = rand.nextInt(times) + minimum;
      if (worldIn.getBlockState(pos.down()).getBlock() == Blocks.AIR && rand.nextDouble() <= extraChance) {
         this.placeCanister(worldIn, pos.down());
      }

      while(current < atm + times && minimum != 1) {
         this.placeThin(worldIn, newPos);
         newPos = newPos.up();
         ++current;
      }

      this.placeThin(worldIn, newPos);
      return newPos;
   }

   private void grow(World worldIn, BlockPos pos, EaglercraftRandom rand, int direction) {
      pos = this.placeColumn(worldIn, pos, 2, rand, 0.3D, 2);
      if (rand.nextInt(3) == 0) {
         pos = this.directionToGrow(pos, direction);
         pos = this.placeColumn(worldIn, pos, 3, rand, 0.5D, 2);
         if (rand.nextInt(5) == 0) {
            pos = this.directionToGrow(pos, direction);
            this.placeColumn(worldIn, pos, 3, rand, 0.7D, 2);
         } else {
            pos = this.directionToGrow(pos, direction);
            this.placeColumn(worldIn, pos, 1, rand, 0.7D, 1);
         }
      } else {
         pos = this.directionToGrow(pos, direction);
         this.placeColumn(worldIn, pos, 1, rand, 0.5D, 1);
      }

   }

   private BlockPos directionToGrow(BlockPos pos, int choice) {
      switch(choice) {
      case 1:
         return pos.north();
      case 2:
         return pos.east();
      case 3:
         return pos.south();
      default:
         return pos.west();
      }
   }
}
