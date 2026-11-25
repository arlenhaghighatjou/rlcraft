package vazkii.quark.world.world.tree;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenSwamp;
import vazkii.quark.world.feature.TreeVariants;

public class WorldGenSwampTree extends WorldGenSwamp {
   private static final IBlockState TRUNK = Blocks.LOG.getDefaultState().withProperty(BlockOldLog.VARIANT, EnumType.OAK);
   private final IBlockState leaf;
   private final boolean addVines;

   public WorldGenSwampTree(boolean addVines) {
      this.addVines = addVines;
      this.leaf = TreeVariants.variant_leaves.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
   }

   public boolean generate(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos) {
      int i;
      for(i = rand.nextInt(4) + 5; worldIn.getBlockState(pos.down()).getMaterial() == Material.WATER; pos = pos.down()) {
         ;
      }

      boolean generating = true;
      if (pos.getY() >= 1 && pos.getY() + i + 1 <= 256) {
         for(int y = pos.getY(); y <= pos.getY() + 1 + i; ++y) {
            int k = 1;
            if (y == pos.getY()) {
               k = 0;
            }

            if (y >= pos.getY() + 1 + i - 2) {
               k = 3;
            }

            MutableBlockPos pointer = new MutableBlockPos();

            for(int x = pos.getX() - k; x <= pos.getX() + k && generating; ++x) {
               for(int z = pos.getZ() - k; z <= pos.getZ() + k && generating; ++z) {
                  if (y >= 0 && y < 256) {
                     IBlockState iblockstate = worldIn.getBlockState(pointer.setPos(x, y, z));
                     Block block = iblockstate.getBlock();
                     if (!iblockstate.getBlock().isAir(iblockstate, worldIn, pointer.setPos(x, y, z)) && !iblockstate.getBlock().isLeaves(iblockstate, worldIn, pointer.setPos(x, y, z))) {
                        if (block != Blocks.WATER && block != Blocks.FLOWING_WATER) {
                           generating = false;
                        } else if (y > pos.getY()) {
                           generating = false;
                        }
                     }
                  } else {
                     generating = false;
                  }
               }
            }
         }

         if (!generating) {
            return false;
         } else {
            BlockPos down = pos.down();
            IBlockState state = worldIn.getBlockState(down);
            boolean isSoil = state.getBlock().canSustainPlant(state, worldIn, down, EnumFacing.UP, (BlockSapling)Blocks.SAPLING);
            if (isSoil && pos.getY() < worldIn.getHeight() - i - 1) {
               state.getBlock().onPlantGrow(state, worldIn, pos.down(), pos);

               for(int y = pos.getY() - 3 + i; y <= pos.getY() + i; ++y) {
                  int dy = y - (pos.getY() + i);
                  int partialDy = 2 - dy / 2;

                  for(int x = pos.getX() - partialDy; x <= pos.getX() + partialDy; ++x) {
                     int dx = x - pos.getX();

                     for(int z = pos.getZ() - partialDy; z <= pos.getZ() + partialDy; ++z) {
                        int dz = z - pos.getZ();
                        if (Math.abs(dx) != partialDy || Math.abs(dz) != partialDy || rand.nextInt(2) != 0 && dy != 0) {
                           BlockPos blockpos = new BlockPos(x, y, z);
                           state = worldIn.getBlockState(blockpos);
                           if (state.getBlock().canBeReplacedByLeaves(state, worldIn, blockpos)) {
                              this.setBlockAndNotifyAdequately(worldIn, blockpos, this.leaf);
                           }
                        }
                     }
                  }
               }

               for(int l1 = 0; l1 < i; ++l1) {
                  BlockPos upN = pos.up(l1);
                  IBlockState upState = worldIn.getBlockState(upN);
                  Block upBlock = upState.getBlock();
                  if (upBlock.isAir(upState, worldIn, upN) || upBlock.isLeaves(upState, worldIn, upN) || upBlock == Blocks.FLOWING_WATER || upBlock == Blocks.WATER) {
                     this.setBlockAndNotifyAdequately(worldIn, pos.up(l1), TRUNK);
                  }
               }

               if (this.addVines) {
                  for(int y = pos.getY() - 3 + i; y <= pos.getY() + i; ++y) {
                     int dx = y - (pos.getY() + i);
                     int partialDy = 2 - dx / 2;
                     MutableBlockPos pointer = new MutableBlockPos();

                     for(int x = pos.getX() - partialDy; x <= pos.getX() + partialDy; ++x) {
                        for(int z = pos.getZ() - partialDy; z <= pos.getZ() + partialDy; ++z) {
                           pointer.setPos(x, y, z);
                           if (worldIn.getBlockState(pointer).getMaterial() == Material.LEAVES) {
                              BlockPos west = pointer.west();
                              BlockPos east = pointer.east();
                              BlockPos north = pointer.north();
                              BlockPos south = pointer.south();
                              if (rand.nextInt(4) == 0 && worldIn.isAirBlock(west)) {
                                 this.addVine(worldIn, west, BlockVine.EAST);
                              }

                              if (rand.nextInt(4) == 0 && worldIn.isAirBlock(east)) {
                                 this.addVine(worldIn, east, BlockVine.WEST);
                              }

                              if (rand.nextInt(4) == 0 && worldIn.isAirBlock(north)) {
                                 this.addVine(worldIn, north, BlockVine.SOUTH);
                              }

                              if (rand.nextInt(4) == 0 && worldIn.isAirBlock(south)) {
                                 this.addVine(worldIn, south, BlockVine.NORTH);
                              }
                           }
                        }
                     }
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private void addVine(World worldIn, BlockPos pos, PropertyBool prop) {
      IBlockState iblockstate = Blocks.VINE.getDefaultState().withProperty(prop, Boolean.valueOf(true));
      this.setBlockAndNotifyAdequately(worldIn, pos, iblockstate);
      int i = 4;

      for(BlockPos blockpos = pos.down(); worldIn.isAirBlock(blockpos) && i > 0; --i) {
         this.setBlockAndNotifyAdequately(worldIn, blockpos, iblockstate);
         blockpos = blockpos.down();
      }

   }
}
