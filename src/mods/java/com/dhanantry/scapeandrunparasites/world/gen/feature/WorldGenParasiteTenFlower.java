package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteTenFlower extends WorldGenParasiteGenAbstract {
   public WorldGenParasiteTenFlower(boolean notify) {
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
            int lag = 0;
            BlockPos current = position;

            while(!worldIn.getBlockState(current.down()).isFullBlock() && current.down().getY() >= 1) {
               current = current.down();

               for(int yyy = 0; yyy <= lag; ++yyy) {
                  for(int xs = -1; xs <= 1; ++xs) {
                     for(int zs = -1; zs <= 1; ++zs) {
                        this.placeTrunk(worldIn, new BlockPos(current.getX() + xs, current.getY(), current.getZ() + zs));
                     }
                  }
               }
            }

            current = position;
            lag = rand.nextInt(2) + 1;

            for(int yyy = 0; yyy <= lag; ++yyy) {
               for(int xs = -1; xs <= 1; ++xs) {
                  for(int zs = -1; zs <= 1; ++zs) {
                     this.placeTrunk(worldIn, new BlockPos(current.getX() + xs, current.getY(), current.getZ() + zs));
                  }
               }

               current = current.up();
            }

            current = current.down();
            int times = 0;
            int dirHelper = 0;

            for(int dir = 0; dir <= 3; ++dir) {
               BlockPos var27 = this.getDirectionRoot(current, dir, 2);
               this.placeTrunk(worldIn, var27);
            }

            current = current.up();

            for(int dir = 0; dir <= 3; ++dir) {
               BlockPos var28 = current;
               times = 1;

               for(int var19 = rand.nextInt(2) + 3; times <= var19; ++times) {
                  var28 = this.getDirectionRoot(var28, dir, 1);
                  this.placeTrunk(worldIn, var28);
               }

               var28 = var28.up();
               this.placeTrunk(worldIn, var28);
               var28 = this.getDirectionRoot(var28, dir, 1);
               this.placeTrunk(worldIn, var28);
               BlockPos posHelper0 = var28;

               for(int help = 1; help <= 3; help += 2) {
                  dirHelper = (dir + help) % 4;
                  var28 = this.getDirectionRoot(posHelper0, dirHelper, 1);
                  var28 = var28.up();
                  var28 = this.placeColumn(worldIn, var28, rand.nextInt(3) + 1, rand, 0.0D);
                  int firrr = help != 1 || dir != 0 && dir != 3 ? 1 : 0;
                  if (dir == 2 && help == 3) {
                     firrr = 0;
                  }

                  if (dir == 1 && help == 3) {
                     firrr = 0;
                  }

                  var28 = this.directionToGrow(var28.down(), dir * 10 + firrr, true);
                  var28 = this.placeColumn(worldIn, var28, rand.nextInt(3) + 1, rand, 0.0D);
                  var28 = this.directionToGrow(var28, dir * 10 + firrr, true);
                  this.placeColumn(worldIn, var28.down(), rand.nextInt(3) + 1, rand, 0.0D);
               }
            }

            current = this.placeColumn(worldIn, current, rand.nextInt(4) + 4, rand, 0.0D);

            for(int dir = 0; dir <= 3; ++dir) {
               BlockPos var37 = this.placeColumn(worldIn, this.getDirectionRoot(current, dir, 1), rand.nextInt(3) + 2, rand, 0.0D);
               int glob = 3;
               var37 = var37.down(2);

               for(int kkk = 0; kkk <= glob; ++kkk) {
                  if (kkk == 0) {
                     this.placeColumn(worldIn, this.getDirectionRoot(var37, dir, 1), 1, rand, 0.0D);
                     var37 = var37.up();
                  }

                  var37 = this.getDirectionRoot(var37, dir, kkk == 0 ? 2 : 1);
                  var37 = this.placeColumn(worldIn, var37, rand.nextInt(4) + 1, rand, 0.0D);
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private void placeTrunk(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteStain.getDefaultState().withProperty(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.FEELER));
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

   private BlockPos placeColumn(World worldIn, BlockPos pos, int in, Random rand, double extraChance) {
      int current = pos.getY();
      int atm = current;
      int times = 0;

      BlockPos newPos;
      for(newPos = pos; current < atm + in; ++times) {
         this.placeTrunk(worldIn, newPos);
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
