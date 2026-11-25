package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteColonyB2 extends WorldGenParasiteColonyBase {
   public WorldGenParasiteColonyB2(boolean notify, int stage) {
      super(notify, stage);
      this.wall = SRPBlocks.ParasiteRubbleDense.getDefaultState().withProperty(BlockParasiteRubbleDense.VARIANT, BlockParasiteRubbleDense.EnumType.WALL);
      this.tacle = SRPBlocks.ParasiteStain.getDefaultState().withProperty(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.FEELER);
      this.floor = SRPBlocks.ParasiteStain.getDefaultState().withProperty(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.DIRT);
   }

   public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos position) {
      this.placeWallsFour(worldIn, position.down(3), 3, rand, 1, this.floorColony);
      this.addFloor(worldIn, position.down(1), 10, true);
      this.placeBlock(worldIn, position.down(), this.floor);

      for(int i = 0; i <= 3; ++i) {
         BlockPos helper = this.getDirectionRoot(position, i, 1);
         BlockPos rootH = helper;
         this.placeBlock(worldIn, helper, this.wall);

         for(int o = 0; o < 2; ++o) {
            for(int times = 1; times <= 1; ++times) {
               if (o == 0) {
                  helper = this.getDirectionRoot(rootH, (i + 1) % 4, times);
               } else {
                  helper = this.getDirectionRoot(rootH, (i + 3) % 4, times);
               }

               if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
                  this.placeBlock(worldIn, helper.down(), this.wall);
               }

               this.placeBlock(worldIn, helper, this.wall);
            }
         }

         helper = this.getDirectionRoot(rootH, i, 1);
         rootH = helper;
         this.placeBlock(worldIn, helper, this.wall);

         for(int o = 0; o < 2; ++o) {
            for(int times = 1; times <= 1; ++times) {
               if (o == 0) {
                  helper = this.getDirectionRoot(rootH, (i + 1) % 4, times);
               } else {
                  helper = this.getDirectionRoot(rootH, (i + 3) % 4, times);
               }

               if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
                  this.placeBlock(worldIn, helper.down(), this.wall);
               }

               this.placeBlock(worldIn, helper, this.wall);
            }
         }

         helper = this.getDirectionRoot(rootH, i, 1);
         this.placeBlock(worldIn, helper, this.wall);
         helper = this.getDirectionRoot(helper, i, 1);
         this.placeBlock(worldIn, helper, this.wall);
      }

      BlockPos var16 = position.up(1);
      BlockPos layer = var16;

      for(int i = 0; i <= 3; ++i) {
         var16 = this.getDirectionRoot(layer, i, 1);
         BlockPos rootH = var16;
         this.placeBlock(worldIn, var16, this.wall);

         for(int o = 0; o < 2; ++o) {
            for(int times = 1; times <= 1; ++times) {
               if (o == 0) {
                  var16 = this.getDirectionRoot(rootH, (i + 1) % 4, times);
               } else {
                  var16 = this.getDirectionRoot(rootH, (i + 3) % 4, times);
               }

               if (!worldIn.getBlockState(var16.down()).isFullBlock()) {
                  this.placeBlock(worldIn, var16.down(), this.wall);
               }

               this.placeBlock(worldIn, var16, this.wall);
            }
         }

         var16 = this.getDirectionRoot(rootH, i, 1);
         this.placeBlock(worldIn, var16, this.wall);
      }

      var16 = layer.up(1);
      layer = var16;

      for(int i = 0; i <= 3; ++i) {
         var16 = this.getDirectionRoot(layer, i, 1);
         BlockPos rootH = var16;
         this.placeBlock(worldIn, var16, this.wall);

         for(int o = 0; o < 2; ++o) {
            for(int times = 1; times <= 1; ++times) {
               if (o == 0) {
                  var16 = this.getDirectionRoot(rootH, (i + 1) % 4, times);
               } else {
                  var16 = this.getDirectionRoot(rootH, (i + 3) % 4, times);
               }

               if (!worldIn.getBlockState(var16.down()).isFullBlock()) {
                  this.placeBlock(worldIn, var16.down(), this.wall);
               }

               this.placeBlock(worldIn, var16, this.wall);
            }
         }
      }

      var16 = layer.up(1);
      int tall = 10;

      for(int current = 0; current <= tall; var16 = var16.up()) {
         ++current;

         for(int i = 0; i <= 3; ++i) {
            this.placeBlock(worldIn, this.getDirectionRoot(var16, i, 1), this.wall);
         }
      }

      var16 = this.placeWallsOne(worldIn, var16, 2, rand, 10, this.wall, position.getY());
      var16 = this.placeWallsTwo(worldIn, var16, 1, rand, 12, this.wall);
      var16 = this.placeWallsThree(worldIn, var16, 3, rand, 13, this.wall);
      var16 = this.placeWallsFour(worldIn, var16, 5, rand, 16, this.wall);
      var16 = this.placeWallsThree(worldIn, var16, 3, rand, 21, this.wall);
      if (rand.nextInt(3) == 0) {
         var16 = this.placeWallsFour(worldIn, var16, 5, rand, 24, this.wall);
         var16 = this.placeWallsThree(worldIn, var16, 3, rand, 29, this.wall);
      }

      this.placeBlock(worldIn, var16, this.wall);
      switch(this.type) {
      case 1:
      case 2:
      case 3:
      default:
         this.replaceLayer(worldIn, position.up(0), 8, this.wall, SRPBlocks.ParasiteRubble.getDefaultState().withProperty(BlockParasiteRubble.VARIANT, BlockParasiteRubble.EnumType.BONE));
         this.replaceLayer(worldIn, position.up(1), 8, this.wall, SRPBlocks.ParasiteRubble.getDefaultState().withProperty(BlockParasiteRubble.VARIANT, BlockParasiteRubble.EnumType.BONE));
         this.replaceLayer(worldIn, position.up(2), 8, this.wall, SRPBlocks.ParasiteRubble.getDefaultState().withProperty(BlockParasiteRubble.VARIANT, BlockParasiteRubble.EnumType.BONE));
         this.addFloor(worldIn, position.up(tall + 4), 10, false);
         return true;
      }
   }

   private BlockPos placeWallsOne(World worldIn, BlockPos position, int loop, EaglercraftRandom rand, int longer, IBlockState state, int oY) {
      BlockPos currentP = position;

      for(int current = 0; current < loop; currentP = currentP.up()) {
         BlockPos helpRoot = currentP;

         for(int i = 0; i <= 3; ++i) {
            BlockPos helper = this.getDirectionRoot(helpRoot, i, 1);
            BlockPos rootH = helper;

            for(int o = 0; o < 2; ++o) {
               for(int times = 1; times <= 2; ++times) {
                  if (o == 0) {
                     helper = this.getDirectionRoot(rootH, (i + 1) % 4, times + 1);
                     if (times == 2 && current == 0) {
                        BlockPos atm = helper.down();
                        this.placeBlock(worldIn, atm, state);

                        while(!worldIn.getBlockState(atm.down()).isFullBlock() && atm.down().getY() >= 1) {
                           atm = this.getDirectionRoot(atm, (i + 4) % 4, 1);
                           atm = this.getDirectionRoot(atm, (i + 3) % 4, 1);
                           int llimit = atm.getY() < oY ? 5 : 2;
                           int llimitO = atm.getY() < oY ? 4 : 0;
                           atm = this.placeColumn(worldIn, atm, rand.nextInt(llimit) + 2 + llimitO, rand, 0.0D, this.tacle);
                        }
                     }
                  } else {
                     helper = this.getDirectionRoot(rootH, (i + 3) % 4, times + 1);
                     if (times == 2 && current == 0) {
                        BlockPos atm = helper.down();
                        this.placeBlock(worldIn, atm, state);

                        while(!worldIn.getBlockState(atm.down()).isFullBlock() && atm.down().getY() >= 1) {
                           atm = this.getDirectionRoot(atm, (i + 4) % 4, 1);
                           atm = this.getDirectionRoot(atm, (i + 1) % 4, 1);
                           int llimit = atm.getY() < oY ? 5 : 2;
                           int llimitO = atm.getY() < oY ? 4 : 0;
                           atm = this.placeColumn(worldIn, atm, rand.nextInt(llimit) + 2 + llimitO, rand, 0.0D, this.tacle);
                        }
                     }
                  }

                  this.placeBlock(worldIn, helper, state);
                  if (current == 0 && rand.nextBoolean()) {
                     this.addVines(worldIn, helper.down(), rand, longer);
                  }
               }
            }

            helper = this.getDirectionRoot(rootH, i, 1);
            rootH = helper;

            for(int o = 0; o < 2; ++o) {
               for(int times = 1; times <= 2; ++times) {
                  if (o == 0) {
                     helper = this.getDirectionRoot(rootH, (i + 1) % 4, times);
                  } else {
                     helper = this.getDirectionRoot(rootH, (i + 3) % 4, times);
                  }

                  this.placeBlock(worldIn, helper, state);
                  if (current == 0 && rand.nextBoolean()) {
                     this.addVines(worldIn, helper.down(), rand, longer);
                  }
               }
            }

            helper = this.getDirectionRoot(rootH, i, 1);
            this.placeBlock(worldIn, helper, state);
            if (current == 0 && rand.nextBoolean()) {
               this.addVines(worldIn, helper.down(), rand, longer);
            }

            this.placeBlock(worldIn, this.getDirectionRoot(helper, i, 1), state);
            if (current == 0 && rand.nextBoolean()) {
               this.addVines(worldIn, this.getDirectionRoot(helper, i, 1).down(), rand, longer);
            }
         }

         ++current;
      }

      return currentP;
   }

   private BlockPos placeWallsTwo(World worldIn, BlockPos position, int loop, EaglercraftRandom rand, int longer, IBlockState state) {
      BlockPos currentP = position;

      for(int current = 0; current < loop; currentP = currentP.up()) {
         BlockPos helpRoot = currentP;

         for(int i = 0; i <= 3; ++i) {
            BlockPos helper = this.getDirectionRoot(helpRoot, i, 2);
            BlockPos rootH = helper;

            for(int o = 0; o < 2; ++o) {
               for(int times = 1; times <= 2; ++times) {
                  if (o == 0) {
                     helper = this.getDirectionRoot(rootH, (i + 1) % 4, times + 1);
                  } else {
                     helper = this.getDirectionRoot(rootH, (i + 3) % 4, times + 1);
                  }

                  this.placeBlock(worldIn, helper, state);
                  if (current == 0 && rand.nextBoolean()) {
                     this.addVines(worldIn, helper.down(), rand, longer);
                  }
               }
            }

            helper = this.getDirectionRoot(rootH, i, 1);
            rootH = helper;

            for(int o = 0; o < 2; ++o) {
               for(int times = 1; times <= 2; ++times) {
                  if (o == 0) {
                     helper = this.getDirectionRoot(rootH, (i + 1) % 4, times);
                  } else {
                     helper = this.getDirectionRoot(rootH, (i + 3) % 4, times);
                  }

                  this.placeBlock(worldIn, helper, state);
                  if (current == 0 && rand.nextBoolean()) {
                     this.addVines(worldIn, helper.down(), rand, longer);
                  }
               }
            }

            helper = this.getDirectionRoot(rootH, i, 1);
            rootH = helper;

            for(int o = 0; o < 2; ++o) {
               for(int times = 1; times <= 1; ++times) {
                  if (o == 0) {
                     helper = this.getDirectionRoot(rootH, (i + 1) % 4, times);
                  } else {
                     helper = this.getDirectionRoot(rootH, (i + 3) % 4, times);
                  }

                  this.placeBlock(worldIn, helper, state);
                  if (current == 0 && rand.nextBoolean()) {
                     this.addVines(worldIn, helper.down(), rand, longer);
                  }
               }
            }

            helper = this.getDirectionRoot(rootH, i, 1);
            this.placeBlock(worldIn, helper, state);
            if (current == 0 && rand.nextBoolean()) {
               this.addVines(worldIn, helper.down(), rand, longer);
            }

            this.placeBlock(worldIn, this.getDirectionRoot(helper, (i + 2) % 4, 1), state);
            if (current == 0 && rand.nextBoolean()) {
               this.addVines(worldIn, this.getDirectionRoot(helper, (i + 2) % 4, 1).down(), rand, longer);
            }
         }

         ++current;
      }

      return currentP;
   }

   private BlockPos placeWallsThree(World worldIn, BlockPos position, int loop, EaglercraftRandom rand, int longer, IBlockState state) {
      BlockPos currentP = position;

      for(int current = 0; current < loop; currentP = currentP.up()) {
         BlockPos helpRoot = currentP;

         for(int i = 0; i <= 3; ++i) {
            BlockPos helper = this.getDirectionRoot(helpRoot, i, 2);
            BlockPos rootH = helper;

            for(int o = 0; o < 2; ++o) {
               for(int times = 1; times <= 2; ++times) {
                  if (o == 0) {
                     helper = this.getDirectionRoot(rootH, (i + 1) % 4, times + 1);
                  } else {
                     helper = this.getDirectionRoot(rootH, (i + 3) % 4, times + 1);
                  }

                  this.placeBlock(worldIn, helper, state);
                  if (current == 0 && rand.nextBoolean()) {
                     this.addVines(worldIn, helper.down(), rand, longer);
                  }
               }
            }

            helper = this.getDirectionRoot(rootH, i, 1);
            rootH = helper;

            for(int o = 0; o < 2; ++o) {
               for(int times = 1; times <= 3; ++times) {
                  if (o == 0) {
                     helper = this.getDirectionRoot(rootH, (i + 1) % 4, times);
                  } else {
                     helper = this.getDirectionRoot(rootH, (i + 3) % 4, times);
                  }

                  this.placeBlock(worldIn, helper, state);
                  if (current == 0 && rand.nextBoolean()) {
                     this.addVines(worldIn, helper.down(), rand, longer);
                  }
               }
            }

            helper = this.getDirectionRoot(rootH, i, 1);
            rootH = helper;

            for(int o = 0; o < 2; ++o) {
               for(int times = 1; times <= 1; ++times) {
                  if (o == 0) {
                     helper = this.getDirectionRoot(rootH, (i + 1) % 4, times);
                  } else {
                     helper = this.getDirectionRoot(rootH, (i + 3) % 4, times);
                  }

                  this.placeBlock(worldIn, helper, state);
                  if (current == 0 && rand.nextBoolean()) {
                     this.addVines(worldIn, helper.down(), rand, longer);
                  }
               }
            }

            helper = this.getDirectionRoot(rootH, i, 1);
            this.placeBlock(worldIn, helper, state);
            if (current == 0 && rand.nextBoolean()) {
               this.addVines(worldIn, helper.down(), rand, longer);
            }

            this.placeBlock(worldIn, this.getDirectionRoot(helper, (i + 2) % 4, 1), state);
            if (current == 0 && rand.nextBoolean()) {
               this.addVines(worldIn, this.getDirectionRoot(helper, (i + 2) % 4, 1).down(), rand, longer);
            }
         }

         ++current;
      }

      return currentP;
   }

   private BlockPos placeWallsFour(World worldIn, BlockPos position, int loop, EaglercraftRandom rand, int longer, IBlockState state) {
      BlockPos currentP = position;

      for(int current = 0; current < loop; currentP = currentP.up()) {
         BlockPos helpRoot = currentP;

         for(int i = 0; i <= 3; ++i) {
            BlockPos helper = this.getDirectionRoot(helpRoot, i, 4);
            BlockPos rootH = helper;

            for(int o = 0; o < 2; ++o) {
               for(int times = 1; times <= 3; ++times) {
                  if (o == 0) {
                     helper = this.getDirectionRoot(rootH, (i + 1) % 4, times);
                  } else {
                     helper = this.getDirectionRoot(rootH, (i + 3) % 4, times);
                  }

                  this.placeBlock(worldIn, helper, state);
                  if (current == 0 && rand.nextBoolean()) {
                     this.addVines(worldIn, helper.down(), rand, longer);
                  }
               }
            }

            helper = this.getDirectionRoot(rootH, i, 1);
            rootH = helper;

            for(int o = 0; o < 2; ++o) {
               for(int times = 1; times <= 1; ++times) {
                  if (o == 0) {
                     helper = this.getDirectionRoot(rootH, (i + 1) % 4, times);
                  } else {
                     helper = this.getDirectionRoot(rootH, (i + 3) % 4, times);
                  }

                  this.placeBlock(worldIn, helper, state);
                  if (current == 0 && rand.nextBoolean()) {
                     this.addVines(worldIn, helper.down(), rand, longer);
                  }
               }
            }

            this.getDirectionRoot(rootH, i, 1);
            this.placeBlock(worldIn, rootH, state);
            if (current == 0 && rand.nextBoolean()) {
               this.addVines(worldIn, rootH.down(), rand, longer);
            }
         }

         ++current;
      }

      return currentP;
   }
}
