package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockBiomeCore;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenParasiteNodeCore extends WorldGenParasiteGenAbstract {
   private int core;

   public WorldGenParasiteNodeCore(boolean notify, int stage) {
      super(notify);
      this.core = stage;
   }

   public boolean generate(World worldIn, Random rand, BlockPos position) {
      switch(this.core) {
      case 1:
         BlockPos helper = position;
         int down = 5;

         while(down >= 0 && helper.down().getY() >= 1) {
            helper = helper.down();
            this.placeTrunk(worldIn, helper);
            --down;

            for(int yyy = 0; yyy <= 0; ++yyy) {
               for(int xs = -1; xs <= 1; ++xs) {
                  for(int zs = -1; zs <= 1; ++zs) {
                     BlockPos helperTwo = new BlockPos(helper.getX() + xs, helper.getY(), helper.getZ() + zs);
                     this.placeTrunk(worldIn, helperTwo);

                     for(int z = 0; z <= 3; ++z) {
                        this.placeTrunk(worldIn, this.directionToGrow(helperTwo, z, false));
                        if (xs == 0 && zs == 0) {
                           BlockPos helperTwo2 = helperTwo;

                           for(int kkk = 0; kkk <= 2; ++kkk) {
                              helperTwo = this.directionToGrow(helperTwo, z, false);
                           }

                           this.placeDirt(worldIn, helperTwo);
                           helperTwo = helperTwo2;
                        }
                     }
                  }
               }
            }
         }

         down = 2;

         while(down >= 0 && helper.down().getY() >= 1) {
            helper = helper.down();
            this.placeTrunk(worldIn, helper);
            --down;

            for(int yyy = 0; yyy <= 0; ++yyy) {
               for(int xs = -1; xs <= 1; ++xs) {
                  for(int zs = -1; zs <= 1; ++zs) {
                     BlockPos var21 = new BlockPos(helper.getX() + xs, helper.getY(), helper.getZ() + zs);
                     this.placeTrunk(worldIn, var21);

                     for(int z = 0; z <= 3; ++z) {
                        if (worldIn.getBlockState(this.directionToGrow(var21, z, false)).getBlock() != SRPBlocks.ParasiteRubbleDense) {
                           this.placeDirt(worldIn, this.directionToGrow(var21, z, false));
                        }
                     }
                  }
               }
            }
         }

         this.placeDirt(worldIn, position.down());
         worldIn.updateBlockTick(position.down(), worldIn.getBlockState(position.down()).getBlock(), 60, 5);
         this.placeCore(worldIn, position, this.core);

         for(int i = 0; i <= 3; ++i) {
            helper = this.directionToGrow(position, i, false);
            if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
               this.placeTrunk(worldIn, helper.down());
            }

            this.placeTrunk(worldIn, helper);
            BlockPos rootH = helper;

            for(int o = 0; o < 2; ++o) {
               if (o == 0) {
                  helper = this.directionToGrow(rootH, (i + 1) % 4, false);
               } else {
                  helper = this.directionToGrow(rootH, (i + 3) % 4, false);
               }

               if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
                  this.placeTrunk(worldIn, helper.down());
               }

               this.placeTrunk(worldIn, helper);
            }

            helper = this.directionToGrow(rootH, i, false);
            if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
               this.placeTrunk(worldIn, helper.down());
            }

            this.placeTrunk(worldIn, helper);
            rootH = helper;

            for(int o = 0; o < 2; ++o) {
               if (o == 0) {
                  helper = this.directionToGrow(rootH, (i + 1) % 4, false);
               } else {
                  helper = this.directionToGrow(rootH, (i + 3) % 4, false);
               }

               if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
                  this.placeTrunk(worldIn, helper.down());
               }

               this.placeTrunk(worldIn, helper);
            }

            helper = this.directionToGrow(rootH, i, false);
            if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
               this.placeTrunk(worldIn, helper.down());
            }

            this.placeTrunk(worldIn, helper);
         }

         position = position.up();
         this.placeTrunk(worldIn, position);
         this.placePeri(worldIn, position);
         position = position.up();
         this.placeTrunk(worldIn, position);
         break;
      case 2:
         position = position.up(2);
         this.placeLiquid(worldIn, position);

         for(int i = 0; i < 3; ++i) {
            this.placePeri(worldIn, position);
            this.placeLiquid(worldIn, position);
            position = position.up();
         }

         int dir = 0;

         for(int i = 0; i <= 3; ++i) {
            BlockPos var23 = this.getDirectionRoot(position.down(), i, 2);
            BlockPos root = var23;

            for(int o = 0; o < 2; ++o) {
               if (o == 0) {
                  if (rand.nextInt(2) == 0) {
                     dir = (i + 1) % 4;
                     var23 = this.directionToGrow(root, dir, false);
                     this.placeTen(worldIn, var23);
                     var23 = this.directionToGrow(var23, i, false);
                     this.placeTen(worldIn, var23);

                     for(; !worldIn.getBlockState(var23.down()).isFullBlock() && var23.down().getY() >= 1; var23 = this.placeColumn(worldIn, var23, rand.nextInt(2) + 2, rand, 0.0D)) {
                        if (rand.nextInt(1) == 0) {
                           var23 = this.directionToGrow(var23, i * 10, true);
                        } else {
                           var23 = this.directionToGrow(var23, i, false);
                        }
                     }

                     this.placeDirt(worldIn, var23.down());
                  }
               } else if (rand.nextInt(2) == 0) {
                  dir = (i + 3) % 4;
                  var23 = this.directionToGrow(root, dir, false);
                  this.placeTen(worldIn, var23);
                  var23 = this.directionToGrow(var23, i, false);
                  this.placeTen(worldIn, var23);

                  while(!worldIn.getBlockState(var23.down()).isFullBlock() && var23.down().getY() >= 1) {
                     if (rand.nextInt(1) == 0) {
                        var23 = this.directionToGrow(var23, i * 10 + 1, true);
                     } else {
                        var23 = this.directionToGrow(var23, i, false);
                     }

                     this.placeTen(worldIn, var23);
                     var23 = this.placeColumn(worldIn, var23, rand.nextInt(2) + 2, rand, 0.0D);
                  }

                  this.placeDirt(worldIn, var23.down());
               }
            }
         }

         this.placeLiquid(worldIn, position);

         for(int i = 0; i < 5; ++i) {
            this.placePeri(worldIn, position);
            this.placeLiquid(worldIn, position);
            position = position.up();
         }

         for(int i = 0; i <= 3; ++i) {
            BlockPos var30 = this.getDirectionRoot(position.down(), i, 2);
            BlockPos var40 = var30;

            for(int o = 0; o < 2; ++o) {
               if (o == 0) {
                  if (rand.nextInt(2) == 0) {
                     dir = (i + 1) % 4;
                     var30 = this.directionToGrow(var40, dir, false);
                     this.placeTen(worldIn, var30);
                     var30 = this.directionToGrow(var30, i, false);
                     this.placeTen(worldIn, var30);

                     while(!worldIn.getBlockState(var30.down()).isFullBlock() && var30.down().getY() >= 1) {
                        if (rand.nextInt(1) == 0) {
                           var30 = this.directionToGrow(var30, i * 10, true);
                        } else {
                           var30 = this.directionToGrow(var30, i, false);
                        }

                        this.placeTen(worldIn, var30);
                        var30 = this.placeColumn(worldIn, var30, rand.nextInt(2) + 1, rand, 0.0D);
                     }

                     this.placeDirt(worldIn, var30.down());
                  }
               } else if (rand.nextInt(2) == 0) {
                  dir = (i + 3) % 4;
                  var30 = this.directionToGrow(var40, dir, false);
                  this.placeTen(worldIn, var30);
                  var30 = this.directionToGrow(var30, i, false);
                  this.placeTen(worldIn, var30);

                  while(!worldIn.getBlockState(var30.down()).isFullBlock() && var30.down().getY() >= 1) {
                     if (rand.nextInt(1) == 0) {
                        var30 = this.directionToGrow(var30, i * 10 + 1, true);
                     } else {
                        var30 = this.directionToGrow(var30, i, false);
                     }

                     this.placeTen(worldIn, var30);
                     var30 = this.placeColumn(worldIn, var30, rand.nextInt(2) + 1, rand, 0.0D);
                  }

                  this.placeDirt(worldIn, var30.down());
               }
            }
         }

         this.placeLiquid(worldIn, position);

         for(int i = 0; i < 5; ++i) {
            this.placePeri(worldIn, position);
            this.placeLiquid(worldIn, position);
            position = position.up();
         }

         this.placeTrunk(worldIn, position);
      case 3:
      }

      return true;
   }

   private void placeTen(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteStain.getDefaultState().withProperty(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.FEELER));
   }

   private void placeTrunk(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteRubbleDense.getDefaultState().withProperty(BlockParasiteRubbleDense.VARIANT, BlockParasiteRubbleDense.EnumType.BIOME));
   }

   private void placeCore(World worldIn, BlockPos pos, int stage) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.BiomeHeart.getDefaultState().withProperty(BlockBiomeCore.ACTIVE, Integer.valueOf(stage)));
   }

   private void placeDirt(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteStain.getDefaultState());
   }

   private void placeLiquid(World worldIn, BlockPos pos) {
      this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.DeadBlood.getDefaultState());
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
         this.placeTen(worldIn, newPos);
         newPos = newPos.down();
         ++current;
      }

      this.placeTen(worldIn, newPos);
      return newPos;
   }

   private void placePeri(World worldIn, BlockPos position) {
      for(int i = 0; i <= 3; ++i) {
         BlockPos helper = this.directionToGrow(position, i, false);
         if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
            this.placeTrunk(worldIn, helper.down());
         }

         this.placeTrunk(worldIn, helper);
         BlockPos rootH = helper;

         for(int o = 0; o < 2; ++o) {
            if (o == 0) {
               helper = this.directionToGrow(rootH, (i + 1) % 4, false);
            } else {
               helper = this.directionToGrow(rootH, (i + 3) % 4, false);
            }

            if (!worldIn.getBlockState(helper.down()).isFullBlock()) {
               this.placeTrunk(worldIn, helper.down());
            }

            this.placeTrunk(worldIn, helper);
         }
      }

   }

   private BlockPos directionToGrow(BlockPos atm, int choice, boolean sideCurse) {
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
            atm = atm.south();
            break;
         case 11:
            atm = atm.east();
            atm = atm.north();
            break;
         case 20:
            atm = atm.south();
            atm = atm.west();
            break;
         case 21:
            atm = atm.south();
            atm = atm.east();
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

   private void positionSides(int choice) {
   }
}
