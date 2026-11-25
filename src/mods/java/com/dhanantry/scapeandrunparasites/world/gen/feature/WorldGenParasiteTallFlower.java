package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteTallFlower extends WorldGenParasiteGenAbstract {
   private IBlockState plant = SRPBlocks.ParasiteTrunk.getDefaultState().withProperty(BlockParasiteTrunk.VARIANT, BlockParasiteTrunk.EnumType.PLANT);
   private IBlockState petal = SRPBlocks.ParasiteStain.getDefaultState().withProperty(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.FEELER);
   private IBlockState base = SRPBlocks.ParasiteStain.getDefaultState().withProperty(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.FLESH);

   public WorldGenParasiteTallFlower(boolean notify) {
      super(notify);
   }

   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int i = 25;
      boolean flag = true;
      if (worldIn.getBlockState(position.down()).getBlock() == Blocks.AIR) {
         return false;
      } else if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight()) {
         for(int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
            int k = 2;
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
            int lag = 0;
            BlockPos current = position;

            while(!worldIn.getBlockState(current.down()).isFullBlock() && current.down().getY() >= 1) {
               current = current.down();

               for(int yyy = 0; yyy <= lag; ++yyy) {
                  for(int xs = -1; xs <= 1; ++xs) {
                     for(int zs = -1; zs <= 1; ++zs) {
                        this.placeBlock(worldIn, new BlockPos(current.getX() + xs, current.getY(), current.getZ() + zs), this.base);
                     }
                  }
               }
            }

            current = position;
            lag = rand.nextInt(2) + 1;

            for(int yyy = 0; yyy < lag; ++yyy) {
               for(int xs = -1; xs <= 1; ++xs) {
                  for(int zs = -1; zs <= 1; ++zs) {
                     this.placeBlock(worldIn, new BlockPos(current.getX() + xs, current.getY(), current.getZ() + zs), this.base);
                  }
               }

               current = current.up();
            }

            current = current.down();
            int times = 0;
            int dirHelper = 0;
            current = current.up();
            this.placeBlock(worldIn, current, this.base);

            for(int dir = 0; dir <= 3; ++dir) {
               BlockPos var27 = this.getDirectionRoot(current, dir, 1);
               this.placeColumn(worldIn, var27, 2, rand, 0.0D, this.base);
            }

            current = this.placeColumn(worldIn, current, 5, rand, 0.0D, this.plant);
            if (rand.nextInt(8) != 0) {
               current = this.getDirectionRoot(current, rand.nextInt(4), 1);
            }

            current = this.placeColumn(worldIn, current, 5, rand, 0.0D, this.plant);
            if (rand.nextInt(8) != 0) {
               current = this.getDirectionRoot(current, rand.nextInt(4), 1);
            }

            current = this.placeColumn(worldIn, current, 5, rand, 0.0D, this.plant);
            if (rand.nextInt(5) != 0) {
               if (rand.nextInt(8) != 0) {
                  current = this.getDirectionRoot(current, rand.nextInt(4), 1);
               }

               current = this.placeColumn(worldIn, current, 5, rand, 0.0D, this.plant);
            }

            for(int dir = 0; dir <= 3; ++dir) {
               BlockPos var28 = this.getDirectionRoot(current, dir, 1);
               this.placeBlock(worldIn, var28, this.petal);
            }

            current = this.placeColumn(worldIn, current, 1, rand, 0.0D, this.petal);
            lag = 2;

            for(int yyy = 0; yyy < lag; ++yyy) {
               for(int xs = -1; xs <= 1; ++xs) {
                  for(int zs = -1; zs <= 1; ++zs) {
                     this.placeBlock(worldIn, new BlockPos(current.getX() + xs, current.getY(), current.getZ() + zs), this.petal);
                  }
               }

               current = current.up();
            }

            this.placeBlock(worldIn, current, this.petal);

            for(int dir = 0; dir <= 3; ++dir) {
               BlockPos var29 = this.getDirectionRoot(current.down(2), dir, 2);
               var29 = this.placeColumn(worldIn, var29, 4, rand, 0.0D, this.petal);
               var29 = this.getDirectionRoot(var29, dir, 1);
               var29 = this.placeColumn(worldIn, var29, 2, rand, 0.0D, this.petal);
               var29 = this.getDirectionRoot(var29, dir, 1);
               this.placeColumn(worldIn, var29, 1, rand, 0.0D, this.petal);
            }

            for(int dir = 0; dir <= 3; ++dir) {
               BlockPos var34 = this.getDirectionRoot(current.down(2), dir, 2);
               var34 = this.directionToGrow(var34, (dir + 1) % 4, false);
               var34 = this.directionToGrow(var34, (dir + 1) % 4, false);
               this.placeColumn(worldIn, var34, 3, rand, 0.0D, this.petal);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private void placeBlock(World worldIn, BlockPos pos, IBlockState state) {
      this.setBlockAndNotifyAdequately(worldIn, pos, state);
   }

   private BlockPos getDirectionRoot(BlockPos center, int direction, int times) {
      switch(direction) {
      case 0:
         return center.north(times);
      case 1:
         return center.east(times);
      case 2:
         return center.south(times);
      default:
         return center.west(times);
      }
   }

   private BlockPos placeColumn(World worldIn, BlockPos pos, int in, Random rand, double extraChance, IBlockState state) {
      int current = pos.getY();
      int atm = current;
      int times = 0;

      BlockPos newPos;
      for(newPos = pos; current < atm + in; ++times) {
         this.placeBlock(worldIn, newPos, state);
         newPos = newPos.up();
         ++current;
      }

      return newPos;
   }

   private BlockPos directionToGrow(BlockPos atm, int choice, boolean sideCurse) {
      atm = atm.up();
      if (sideCurse) {
         switch(choice) {
         case 0:
            atm = atm.north();
            atm = atm.east();
            break;
         case 1:
            atm = atm.north();
            atm = atm.west();
            break;
         case 10:
            atm = atm.east();
            atm = atm.north();
            break;
         case 11:
            atm = atm.east();
            atm = atm.south();
            break;
         case 20:
            atm = atm.south();
            atm = atm.east();
            break;
         case 21:
            atm = atm.south();
            atm = atm.west();
            break;
         case 30:
            atm = atm.west();
            atm = atm.north();
            break;
         default:
            atm = atm.west();
            atm = atm.south();
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
