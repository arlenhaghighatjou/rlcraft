package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteColonyBS1 extends WorldGenParasiteColonyBase {
   private IBlockState floor = SRPBlocks.ParasiteStain.getDefaultState().withProperty(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.DIRT);
   private IBlockState tacle = SRPBlocks.ParasiteStain.getDefaultState().withProperty(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.FEELER);
   private IBlockState wall = SRPBlocks.ParasiteRubbleDense.getDefaultState().withProperty(BlockParasiteRubbleDense.VARIANT, BlockParasiteRubbleDense.EnumType.WALL);

   public WorldGenParasiteColonyBS1(boolean notify, int stage) {
      super(notify, stage);
   }

   public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos position) {
      this.placeWallsTopIn(worldIn, position.down(3), 3, false, rand, 1, this.floorColony);
      this.addFloor(worldIn, position.down(1), 10, true);
      int l1 = 9;
      BlockPos helper = this.placeWallsBottom(worldIn, position, l1, rand, this.wall, position.getY());
      int l2 = 5;
      helper = this.placeWallsTopIn(worldIn, helper, l2, true, rand, 12, this.wall);
      this.addFloor(worldIn, helper.down(l2 + 0), 10, false);
      switch(this.type) {
      case 1:
      case 2:
      case 3:
      default:
         this.replaceLayer(worldIn, position.up(0), 10, this.wall, SRPBlocks.ParasiteRubble.getDefaultState().withProperty(BlockParasiteRubble.VARIANT, BlockParasiteRubble.EnumType.BONE));
         return true;
      }
   }

   private BlockPos placeWallsBottom(World worldIn, BlockPos position, int loop, EaglercraftRandom rand, IBlockState state, int oY) {
      BlockPos currentP = position;

      for(int current = 0; current < loop; currentP = currentP.up()) {
         BlockPos helpRoot = currentP;

         for(int i = 0; i <= 3; ++i) {
            BlockPos helper = this.getDirectionRoot(helpRoot, i, 2);
            BlockPos rootH = helper;

            for(int o = 0; o < 1; ++o) {
               for(int times = 1; times <= 1; ++times) {
                  if (o == 0) {
                     helper = this.getDirectionRoot(rootH, (i + 1) % 4, times + 1);
                  } else {
                     helper = this.getDirectionRoot(rootH, (i + 3) % 4, times + 1);
                  }

                  if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
                     this.placeBlock(worldIn, helper.down(), state);
                  }

                  this.placeBlock(worldIn, helper, state);
               }
            }

            helper = this.getDirectionRoot(rootH, i, 1);
            rootH = helper;

            for(int o = 0; o < 2; ++o) {
               for(int times = 1; times <= 1; ++times) {
                  if (o == 0) {
                     helper = this.getDirectionRoot(rootH, (i + 1) % 4, times);
                     if (current == 2 && rand.nextInt(2) == 0) {
                        BlockPos var28 = this.directionToGrow(helper, i, false);
                        this.placeBlock(worldIn, var28, this.tacle);

                        while(!worldIn.getBlockState(var28.down()).isFullBlock() && var28.down().getY() >= 1) {
                           if (rand.nextInt(1) == 0) {
                              var28 = this.directionToGrow(var28, i * 10, true);
                           } else {
                              var28 = this.directionToGrow(var28, i, false);
                           }

                           int llimit = var28.getY() < oY ? 5 : 2;
                           int llimitO = var28.getY() < oY ? 4 : 0;
                           var28 = this.placeColumn(worldIn, var28, rand.nextInt(llimit) + 2 + llimitO, rand, 0.0D, this.tacle);
                        }

                        this.placeBlock(worldIn, var28.down(), this.tacle);
                     }

                     if (current == loop - 3) {
                        BlockPos var30 = this.directionToGrow(helper, i, false);
                        if (worldIn.getBlockState(var30.down(4)).getBlock() == Blocks.AIR) {
                           BlockPos var34 = this.directionToGrow(helper, i, false);
                           this.placeBlock(worldIn, var34, this.tacle);

                           while(!worldIn.getBlockState(var34.down()).isFullBlock() && var34.down().getY() >= 1) {
                              if (rand.nextInt(1) == 0) {
                                 var34 = this.directionToGrow(var34, i * 10, true);
                              } else {
                                 var34 = this.directionToGrow(var34, i, false);
                              }

                              int llimit = var34.getY() < oY ? 5 : 2;
                              int llimitO = var34.getY() < oY ? 4 : 0;
                              var34 = this.placeColumn(worldIn, var34, rand.nextInt(llimit) + 2 + llimitO, rand, 0.0D, this.tacle);
                           }

                           this.placeBlock(worldIn, var34.down(), this.tacle);
                        }
                     }
                  } else {
                     helper = this.getDirectionRoot(rootH, (i + 3) % 4, times);
                     if (current == 5 && times != 1 && rand.nextInt(2) == 0) {
                        BlockPos trunk = this.directionToGrow(helper, i, false);
                        this.placeBlock(worldIn, trunk, this.tacle);

                        while(!worldIn.getBlockState(trunk.down()).isFullBlock() && trunk.down().getY() >= 1) {
                           if (rand.nextInt(1) == 0) {
                              trunk = this.directionToGrow(trunk, i * 10 + 1, true);
                           } else {
                              trunk = this.directionToGrow(trunk, i, false);
                           }

                           int llimit = trunk.getY() < oY ? 5 : 2;
                           int llimitO = trunk.getY() < oY ? 4 : 0;
                           trunk = this.placeColumn(worldIn, trunk, rand.nextInt(llimit) + 2 + llimitO, rand, 0.0D, this.tacle);
                        }

                        this.placeBlock(worldIn, trunk.down(), this.tacle);
                     }

                     if (current == loop - 2 && times != 1) {
                        BlockPos var27 = this.directionToGrow(helper, i, false);
                        if (worldIn.getBlockState(var27.down(5)).getBlock() == Blocks.AIR) {
                           BlockPos var31 = this.directionToGrow(helper, i, false);
                           this.placeBlock(worldIn, var31, this.tacle);

                           while(!worldIn.getBlockState(var31.down()).isFullBlock() && var31.down().getY() >= 1) {
                              if (rand.nextInt(1) == 0) {
                                 var31 = this.directionToGrow(var31, i * 10 + 1, true);
                              } else {
                                 var31 = this.directionToGrow(var31, i, false);
                              }

                              int llimit = var31.getY() < oY ? 5 : 2;
                              int llimitO = var31.getY() < oY ? 4 : 0;
                              var31 = this.placeColumn(worldIn, var31, rand.nextInt(llimit) + 2 + llimitO, rand, 0.0D, this.tacle);
                           }

                           this.placeBlock(worldIn, var31.down(), this.tacle);
                        }
                     }
                  }

                  if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
                     this.placeBlock(worldIn, helper.down(), state);
                  }

                  this.placeBlock(worldIn, helper, state);
               }
            }

            helper = this.getDirectionRoot(rootH, i, 0);
            if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
               this.placeBlock(worldIn, helper.down(), state);
            }

            this.placeBlock(worldIn, helper, state);
         }

         ++current;
      }

      return currentP;
   }

   private BlockPos placeWallsTopIn(World worldIn, BlockPos position, int loop, boolean vine, EaglercraftRandom rand, int longer, IBlockState state) {
      BlockPos currentP = position;

      for(int current = 0; current < loop; currentP = currentP.up()) {
         BlockPos helpRoot = currentP;

         for(int i = 0; i <= 3; ++i) {
            BlockPos helper = this.getDirectionRoot(helpRoot, i, 3);
            BlockPos rootH = helper;

            for(int o = 0; o < 2; ++o) {
               for(int times = 1; times <= 1; ++times) {
                  if (o == 0) {
                     helper = this.getDirectionRoot(rootH, (i + 1) % 4, times + 1);
                  } else {
                     helper = this.getDirectionRoot(rootH, (i + 3) % 4, times + 1);
                  }

                  this.placeBlock(worldIn, helper, state);
                  if (vine && current == 0 && rand.nextBoolean()) {
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
                  if (vine && current == 0 && rand.nextBoolean()) {
                     this.addVines(worldIn, helper.down(), rand, longer);
                  }
               }
            }

            helper = this.getDirectionRoot(rootH, i, 0);
            this.placeBlock(worldIn, helper, state);
            if (vine && current == 0 && rand.nextBoolean()) {
               this.addVines(worldIn, helper.down(), rand, longer);
            }
         }

         ++current;
      }

      return currentP;
   }

   private void addEntrance(World worldIn, BlockPos position, EaglercraftRandom rand, IBlockState state) {
      int chance = 1;

      for(int i = 0; i <= 3; ++i) {
         if (rand.nextInt(chance) == 0) {
            BlockPos helper2 = this.getDirectionRoot(position, i, 5);
            BlockPos root = helper2;

            for(int door = 0; door < 5; ++door) {
               worldIn.setBlockState(this.getDirectionRoot(helper2.up(door), i, 1), Blocks.AIR.getDefaultState());
               if (door == 4) {
                  this.addVines(worldIn, this.getDirectionRoot(helper2.up(door), i, 1), rand, rand.nextInt(4) + 2);
               }

               if (door == 4) {
                  BlockPos atmd = this.getDirectionRoot(helper2.up(door), i, 1);

                  for(int o = 0; o < 2; ++o) {
                     if (o == 0) {
                        helper2 = this.getDirectionRoot(atmd, (i + 1) % 4, 1);
                     } else {
                        helper2 = this.getDirectionRoot(atmd, (i + 3) % 4, 1);
                     }

                     this.placeBlock(worldIn, helper2, state);
                     this.addVines(worldIn, helper2.down(), rand, rand.nextInt(4) + 2);
                  }
               }
            }

            for(int o = 0; o < 2; ++o) {
               for(int times = 1; times <= 2; ++times) {
                  if (o == 0) {
                     helper2 = this.getDirectionRoot(root, (i + 1) % 4, times);
                  } else {
                     helper2 = this.getDirectionRoot(root, (i + 3) % 4, times);
                  }

                  if (times == 2) {
                     helper2 = this.getDirectionRoot(helper2, i, 1);
                     this.placeColumn(worldIn, helper2.up(3), 3, rand, 0.0D, this.wall);
                     helper2 = this.getDirectionRoot(helper2, i, 1);
                     this.placeBlock(worldIn, helper2, state);

                     for(int door = 0; door < 1; ++door) {
                        if (o == 0) {
                           helper2 = this.getDirectionRoot(root, (i + 1) % 4, 3);
                        } else {
                           helper2 = this.getDirectionRoot(root, (i + 3) % 4, 3);
                        }

                        this.placeBlock(worldIn, helper2, state);
                        helper2 = this.getDirectionRoot(helper2, i, 1);
                        this.placeBlock(worldIn, helper2, state);
                     }
                  } else {
                     for(int door = 0; door < 4; ++door) {
                        worldIn.setBlockState(helper2.up(door), Blocks.AIR.getDefaultState());
                     }
                  }
               }
            }

            chance += 2 + i;
         }
      }

   }
}
