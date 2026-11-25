package rtg.api.world.gen.feature;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import rtg.api.util.BlockUtil;

public class WorldGenBlob extends WorldGenerator {
   private final IBlockState blobBlock;
   private final int blobSize;
   private boolean allowInWater;
   private Collection<Block> validGroundBlocks;
   private boolean enabled = true;

   public WorldGenBlob(IBlockState block, int size, Collection<Block> validGroundBlocks, boolean allowInWater) {
      super(false);
      this.blobBlock = block;
      this.blobSize = size;
      this.validGroundBlocks = Collections.unmodifiableCollection(validGroundBlocks);
      this.allowInWater = allowInWater;
   }

   public boolean generate(World world, Random rand, BlockPos pos) {
      if (!this.enabled) {
         return false;
      } else {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         MutableBlockPos mpos = new MutableBlockPos(pos);

         while(true) {
            label82: {
               mpos.move(EnumFacing.DOWN);
               if (mpos.getY() > 3) {
                  if (world.isAirBlock(mpos)) {
                     break label82;
                  }

                  IBlockState blockstate = world.getBlockState(mpos);
                  if (!this.allowInWater && (blockstate.getMaterial() == Material.WATER || BlockUtil.checkAreaMaterials(BlockUtil.MatchType.ANY, world, mpos, 1, Material.WATER))) {
                     return false;
                  }

                  if (!this.validGroundBlocks.contains(blockstate.getBlock())) {
                     break label82;
                  }
               }

               if (mpos.getY() <= 3) {
                  return false;
               }

               int k2 = this.blobSize;

               for(int l = 0; this.blobSize >= 0 && l < 3; ++l) {
                  int sizeX = this.blobSize + rand.nextInt(2);
                  int sizeY = this.blobSize + rand.nextInt(2);
                  int sizeZ = this.blobSize + rand.nextInt(2);
                  float f = (float)(sizeX + sizeY + sizeZ) * 0.333F + 0.5F;

                  for(int bx = x - sizeX; bx <= x + sizeX; ++bx) {
                     for(int bz = z - sizeZ; bz <= z + sizeZ; ++bz) {
                        for(int by = y - sizeY; by <= y + sizeY; ++by) {
                           float f1 = (float)(bx - x);
                           float f2 = (float)(bz - z);
                           float f3 = (float)(by - y);
                           if (f1 * f1 + f2 * f2 + f3 * f3 <= f * f) {
                              this.placeBoulderBlock(world, new BlockPos(bx, by, bz), this.blobBlock);
                           }
                        }
                     }
                  }

                  x += -(this.blobSize + 1) + rand.nextInt(2 + k2 * 2);
                  z += -(this.blobSize + 1) + rand.nextInt(2 + k2 * 2);
                  y += 0 - rand.nextInt(2);
               }

               return true;
            }

            --y;
         }
      }
   }

   private void placeBoulderBlock(World world, BlockPos targetPos, IBlockState boulderBlock) {
      MutableBlockPos mpos = new MutableBlockPos(targetPos);
      Block targetBlock = world.getBlockState(targetPos).getBlock();
      if (targetBlock.isReplaceable(world, targetPos)) {
         world.setBlockState(targetPos, boulderBlock, 2);
         if (world.getBlockState(mpos.move(EnumFacing.UP)).getBlock() == Blocks.DOUBLE_PLANT) {
            world.setBlockState(mpos, Blocks.AIR.getDefaultState(), 2);
         }
      }

   }
}
