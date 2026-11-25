package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenCustomStructures;
import com.dhanantry.scapeandrunparasites.world.gen.structure.WorldGenStructure;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteBigBall extends WorldGenParasiteGenAbstract {
   public WorldGenParasiteBigBall(boolean notify) {
      super(notify);
   }

   public boolean generate(World worldIn, Random rand, BlockPos position) {
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
            int extra = rand.nextInt(6) + 2;
            BlockPos current = position.up(9 + extra);
            WorldGenCustomStructures.generateInPosition(new WorldGenStructure("ballbig"), new Random(), worldIn, current, 6);
            BlockPos center = new BlockPos(position.getX(), position.getY(), position.getZ());
            boolean skip = true;

            for(int z = 0; z <= 3; ++z) {
               if (rand.nextDouble() <= 0.15D && skip) {
                  skip = false;
               } else {
                  BlockPos root = center.up(12 + extra);
                  root = this.getDirectionRoot(root, z, 4);
                  root = this.placeColumn(worldIn, root, 3, rand, 0.0D);
                  int direction = rand.nextInt(4);
                  boolean glag = true;

                  for(boolean side = false; glag; direction = rand.nextInt(4)) {
                     int grow = rand.nextInt(4) + 2;
                     root = this.directionToGrow(root.down(), direction, side);
                     if (rand.nextDouble() <= 0.2D) {
                        BlockPos atm = this.directionToGrow(root, direction, !side);
                        this.placeColumn(worldIn, this.directionToGrow(atm, direction, !side), grow, rand, 0.0D);
                     }

                     BlockPos var25 = this.placeColumn(worldIn, root, grow, rand, 0.0D);
                     BlockPos var26 = this.directionToGrow(var25.down(), direction, !side);
                     root = this.placeColumn(worldIn, var26, rand.nextInt(2) + 2, rand, 0.0D);
                     side = !side;
                     glag = worldIn.getBlockState(root).isFullBlock();
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private void placeTrunk(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteTrunk.getDefaultState().withProperty(BlockParasiteTrunk.VARIANT, BlockParasiteTrunk.EnumType.TREE));
   }

   private BlockPos getDirectionRoot(BlockPos center, int direction, int times) {
      switch(direction) {
      case 0:
         return center.north(times);
      case 1:
         return center.east(times);
      case 2:
      default:
         return center.south(times);
      case 3:
         return center.west(times);
      }
   }

   private BlockPos placeColumn(World worldIn, BlockPos pos, int in, Random rand, double extraChance) {
      --in;
      int current = pos.getY();
      int atm = current;
      int times = 0;

      BlockPos newPos;
      for(newPos = pos; current > atm - in; ++times) {
         this.placeTrunk(worldIn, newPos);
         newPos = newPos.down();
         --current;
      }

      this.placeTrunk(worldIn, newPos);
      return newPos;
   }

   private BlockPos directionToGrow(BlockPos atm, int choice, boolean reverse) {
      if (reverse) {
         switch(choice) {
         case 0:
            atm = atm.south();
            break;
         case 1:
            atm = atm.west();
            break;
         case 2:
         default:
            atm = atm.north();
            break;
         case 3:
            atm = atm.east();
         }

         return atm;
      } else {
         switch(choice) {
         case 0:
            atm = atm.north();
            break;
         case 1:
            atm = atm.east();
            break;
         case 2:
         default:
            atm = atm.south();
            break;
         case 3:
            atm = atm.west();
         }

         return atm;
      }
   }
}
