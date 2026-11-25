package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.Random;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteSpine extends WorldGenParasiteGenAbstract {
   public WorldGenParasiteSpine(boolean notify) {
      super(notify);
   }

   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = 28;
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
            BlockPos current = position;
            int partner = rand.nextInt(4);
            BlockPos twoCurrent = this.directionToGrow(position, partner, false);
            int atmm = (partner + 1) % 4;

            while(!worldIn.getBlockState(current.down()).isFullBlock() && current.down().getY() >= 1) {
               current = current.down();
               twoCurrent = twoCurrent.down();
               this.placeTrunk(worldIn, current);
               this.placeTrunk(worldIn, twoCurrent);

               for(int z = 0; z <= 3; ++z) {
                  this.placeTrunk(worldIn, this.directionToGrow(current, z, false));
                  this.placeTrunk(worldIn, this.directionToGrow(twoCurrent, z, false));
               }
            }

            twoCurrent = this.directionToGrow(position, partner, false);
            this.placeStair(worldIn, position, atmm, true, 1, false);
            current = this.placeColumn(worldIn, position, 4, rand, partner, 27, true);
            this.placeStair(worldIn, current, atmm, true, 0, false);
            this.placeColumn(worldIn, this.directionToGrow(position, (atmm + 2) % 4, false), 5, rand, partner, -1, false);
            this.placeStair(worldIn, twoCurrent, atmm, true, 1, false);
            twoCurrent = this.placeColumn(worldIn, twoCurrent, 4, rand, (partner + 2) % 4, 27, true);
            this.placeStair(worldIn, twoCurrent, atmm, true, 0, false);
            this.placeColumn(worldIn, this.directionToGrow(this.directionToGrow(position, partner, false), (atmm + 2) % 4, false), 5, rand, partner, -1, false);
            this.placeStair(worldIn, twoCurrent.down(4), partner, true, 1, false);
            this.placeStair(worldIn, current.down(4), (partner + 2) % 4, true, 1, false);
            current = this.directionToGrow(current.up(), atmm, true);
            current = this.placeColumn(worldIn, current, 2, rand, partner, 14, true);
            this.placeStair(worldIn, current, atmm, true, 0, false);
            twoCurrent = this.directionToGrow(twoCurrent.up(), atmm, true);
            twoCurrent = this.placeColumn(worldIn, twoCurrent, 2, rand, (partner + 2) % 4, 14, true);
            this.placeStair(worldIn, twoCurrent, atmm, true, 0, false);
            int randomG = rand.nextInt(4) + 3;
            current = this.directionToGrow(current.up(), atmm, true);
            this.placeStair(worldIn, current.down(), (atmm + 2) % 4, false, 0, false);
            current = this.placeColumn(worldIn, current, randomG, rand, partner, 7, true);
            this.placeStair(worldIn, current, atmm, true, 0, false);
            twoCurrent = this.directionToGrow(twoCurrent.up(), atmm, true);
            this.placeStair(worldIn, twoCurrent.down(), (atmm + 2) % 4, false, 0, false);
            twoCurrent = this.placeColumn(worldIn, twoCurrent, randomG, rand, (partner + 2) % 4, 7, true);
            this.placeStair(worldIn, twoCurrent, atmm, true, 0, false);
            randomG = rand.nextInt(5) + 6;
            current = this.directionToGrow(current.up(), atmm, true);
            this.placeStair(worldIn, current.down(), (atmm + 2) % 4, false, 0, false);
            current = this.placeColumn(worldIn, current, randomG, rand, partner, 2, true);
            this.placeStair(worldIn, current, atmm, true, 0, false);
            twoCurrent = this.directionToGrow(twoCurrent.up(), atmm, true);
            this.placeStair(worldIn, twoCurrent.down(), (atmm + 2) % 4, false, 0, false);
            twoCurrent = this.placeColumn(worldIn, twoCurrent, randomG, rand, (partner + 2) % 4, 2, true);
            this.placeStair(worldIn, twoCurrent, atmm, true, 0, false);
            randomG = rand.nextInt(5) + 6;
            current = this.directionToGrow(current.up(), atmm, true);
            this.placeStair(worldIn, current.down(), (atmm + 2) % 4, false, 0, false);
            current = this.placeColumn(worldIn, current, randomG, rand, partner, 0, true);
            this.placeStair(worldIn, current, atmm, true, 0, false);
            twoCurrent = this.directionToGrow(twoCurrent.up(), atmm, true);
            this.placeStair(worldIn, twoCurrent.down(), (atmm + 2) % 4, false, 0, false);
            twoCurrent = this.placeColumn(worldIn, twoCurrent, randomG, rand, (partner + 2) % 4, 0, true);
            this.placeStair(worldIn, twoCurrent, atmm, true, 0, false);
            return true;
         }
      } else {
         return false;
      }
   }

   private void placeStair(World worldIn, BlockPos pos, int direction, boolean bottom, int times, boolean slab) {
      switch(direction) {
      case 0:
         if (bottom) {
            this.setBlockAndNotifyAdequately(worldIn, pos.north(times), SRPBlocks.ParasiteRubbleBoneStair.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM));
         } else {
            this.setBlockAndNotifyAdequately(worldIn, pos.north(times), SRPBlocks.ParasiteRubbleBoneStair.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.TOP));
            if (slab) {
               this.setBlockAndNotifyAdequately(worldIn, pos.north(times + 1), SRPBlocks.ParasiteRubbleSlabHalf.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.TOP));
            }
         }
         break;
      case 1:
         if (bottom) {
            this.setBlockAndNotifyAdequately(worldIn, pos.east(times), SRPBlocks.ParasiteRubbleBoneStair.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM));
         } else {
            this.setBlockAndNotifyAdequately(worldIn, pos.east(times), SRPBlocks.ParasiteRubbleBoneStair.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.TOP));
            if (slab) {
               this.setBlockAndNotifyAdequately(worldIn, pos.east(times + 1), SRPBlocks.ParasiteRubbleSlabHalf.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.TOP));
            }
         }
         break;
      case 2:
      default:
         if (bottom) {
            this.setBlockAndNotifyAdequately(worldIn, pos.south(times), SRPBlocks.ParasiteRubbleBoneStair.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM));
         } else {
            this.setBlockAndNotifyAdequately(worldIn, pos.south(times), SRPBlocks.ParasiteRubbleBoneStair.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.TOP));
            if (slab) {
               this.setBlockAndNotifyAdequately(worldIn, pos.south(times + 1), SRPBlocks.ParasiteRubbleSlabHalf.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.TOP));
            }
         }
         break;
      case 3:
         if (bottom) {
            this.setBlockAndNotifyAdequately(worldIn, pos.west(times), SRPBlocks.ParasiteRubbleBoneStair.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM));
         } else {
            this.setBlockAndNotifyAdequately(worldIn, pos.west(times), SRPBlocks.ParasiteRubbleBoneStair.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.TOP));
            if (slab) {
               this.setBlockAndNotifyAdequately(worldIn, pos.west(times + 1), SRPBlocks.ParasiteRubbleSlabHalf.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.TOP));
            }
         }
      }

   }

   private void placeTrunk(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteRubble.getDefaultState());
   }

   private BlockPos placeColumn(World worldIn, BlockPos pos, int in, Random rand, int direction, int slabs, boolean stair) {
      int current = pos.getY();
      int atm = current;
      int times = 0;

      BlockPos newPos;
      for(newPos = pos; current < atm + in; ++times) {
         if (times % 2 == 0 && stair) {
            if (slabs > 20) {
               this.placeStair(worldIn, newPos, direction, false, 2, false);
               slabs -= 10;
            } else if (slabs > 10) {
               slabs -= 10;
               this.placeStair(worldIn, newPos, direction, false, 2, false);
               this.placeStair(worldIn, newPos.up(), (direction + 2) % 4, true, -2, false);
               this.placeStair(worldIn, newPos.up(), direction, false, 3, slabs > 5);
            } else {
               this.placeStair(worldIn, newPos, direction, false, 2, slabs > 0);
               --slabs;
            }
         }

         this.placeTrunk(worldIn, newPos);
         newPos = newPos.up();
         ++current;
      }

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
