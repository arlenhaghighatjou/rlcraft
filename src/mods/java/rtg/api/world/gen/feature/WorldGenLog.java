package rtg.api.world.gen.feature;

import java.util.ArrayList;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import rtg.api.util.BlockUtil;

public class WorldGenLog extends WorldGenerator {
   private IBlockState logBlock;
   private IBlockState leavesBlock;
   private int logLength;
   private boolean generateLeaves;

   public WorldGenLog(IBlockState logBlock, IBlockState leavesBlock, int logLength) {
      this.logBlock = logBlock;
      this.leavesBlock = leavesBlock;
      this.logLength = logLength;
      this.generateLeaves = false;
   }

   public boolean generate(@Nonnull World world, @Nonnull EaglercraftRandom rand, @Nonnull BlockPos pos) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      IBlockState g = world.getBlockState(new BlockPos(x, y - 1, z));
      if (g.getMaterial() != Material.GROUND && g.getMaterial() != Material.GRASS && g.getMaterial() != Material.SAND && g.getMaterial() != Material.ROCK) {
         return false;
      } else {
         int dir = rand.nextInt(2);
         int air = 0;
         ArrayList<Integer> aX = new ArrayList();
         ArrayList<Integer> aY = new ArrayList();
         ArrayList<Integer> aZ = new ArrayList();
         ArrayList<IBlockState> aBlock = new ArrayList();
         MutableBlockPos mpos = new MutableBlockPos(pos);

         for(int i = 0; i < this.logLength; ++i) {
            IBlockState b = world.getBlockState(mpos.setPos(x - (dir == 0 ? 1 : 0), y, z - (dir == 1 ? 1 : 0)));
            if (b.getMaterial() != Material.AIR && b.getMaterial() != Material.VINE && b.getMaterial() != Material.PLANTS) {
               break;
            }

            x -= dir == 0 ? 1 : 0;
            z -= dir == 1 ? 1 : 0;
            if (this.airCheck(world, x, y, z) > 0) {
               return false;
            }
         }

         for(int i = 0; i < this.logLength * 2; ++i) {
            IBlockState b = world.getBlockState(mpos.setPos(x + (dir == 0 ? 1 : 0), y, z + (dir == 1 ? 1 : 0)));
            if (b.getMaterial() != Material.AIR && b.getMaterial() != Material.VINE && b.getMaterial() != Material.PLANTS) {
               break;
            }

            air += this.airCheck(world, x, y, z);
            if (air > 2) {
               return false;
            }

            if (!BlockUtil.checkVerticalBlocks(BlockUtil.MatchType.ALL, world, pos, 1, Blocks.AIR)) {
               return false;
            }

            aX.add(Integer.valueOf(x));
            aY.add(Integer.valueOf(y));
            aZ.add(Integer.valueOf(z));

            try {
               aBlock.add(this.logBlock.withProperty(BlockLog.LOG_AXIS, dir == 0 ? EnumAxis.X : EnumAxis.Z));
            } catch (Exception var18) {
               return false;
            }

            if (this.generateLeaves) {
               this.addLeaves(world, rand, dir, x, y, z);
            }

            x += dir == 0 ? 1 : 0;
            z += dir == 1 ? 1 : 0;
         }

         for(int i = 0; i < aBlock.size(); ++i) {
            world.setBlockState(mpos.setPos(((Integer)aX.get(i)).intValue(), ((Integer)aY.get(i)).intValue(), ((Integer)aZ.get(i)).intValue()), (IBlockState)aBlock.get(i), 2);
         }

         return true;
      }
   }

   private int airCheck(World world, int x, int y, int z) {
      IBlockState b = world.getBlockState(new BlockPos(x, y - 1, z));
      if (b.getMaterial() != Material.AIR && b.getMaterial() != Material.VINE && b.getMaterial() != Material.WATER && b.getMaterial() != Material.PLANTS) {
         return 0;
      } else {
         b = world.getBlockState(new BlockPos(x, y - 2, z));
         return b.getMaterial() != Material.AIR && b.getMaterial() != Material.VINE && b.getMaterial() != Material.WATER && b.getMaterial() != Material.PLANTS ? 1 : 99;
      }
   }

   private void addLeaves(World world, EaglercraftRandom rand, int dir, int x, int y, int z) {
      if (dir == 0) {
         IBlockState b = world.getBlockState(new BlockPos(x, y, z - 1));
         if ((b.getMaterial() == Material.AIR || b.getMaterial() == Material.VINE || b.getMaterial() == Material.PLANTS) && rand.nextInt(3) == 0) {
            world.setBlockState(new BlockPos(x, y, z - 1), this.leavesBlock, 2);
         }

         b = world.getBlockState(new BlockPos(x, y, z + 1));
         if ((b.getMaterial() == Material.AIR || b.getMaterial() == Material.VINE || b.getMaterial() == Material.PLANTS) && rand.nextInt(3) == 0) {
            world.setBlockState(new BlockPos(x, y, z + 1), this.leavesBlock, 2);
         }
      } else {
         IBlockState b = world.getBlockState(new BlockPos(x - 1, y, z));
         if ((b.getMaterial() == Material.AIR || b.getMaterial() == Material.VINE || b.getMaterial() == Material.PLANTS) && rand.nextInt(3) == 0) {
            world.setBlockState(new BlockPos(x - 1, y, z), this.leavesBlock, 2);
         }

         b = world.getBlockState(new BlockPos(x + 1, y, z));
         if ((b.getMaterial() == Material.AIR || b.getMaterial() == Material.VINE || b.getMaterial() == Material.PLANTS) && rand.nextInt(3) == 0) {
            world.setBlockState(new BlockPos(x + 1, y, z), this.leavesBlock, 2);
         }
      }

      IBlockState var11 = world.getBlockState(new BlockPos(x, y + 1, z));
      if ((var11.getMaterial() == Material.AIR || var11.getMaterial() == Material.VINE || var11.getMaterial() == Material.PLANTS) && rand.nextInt(3) == 0) {
         world.setBlockState(new BlockPos(x, y + 1, z), this.leavesBlock, 2);
      }

   }

   public IBlockState getLogBlock() {
      return this.logBlock;
   }

   public WorldGenLog setLogBlock(IBlockState logBlock) {
      this.logBlock = logBlock;
      return this;
   }

   public IBlockState getLeavesBlock() {
      return this.leavesBlock;
   }

   public WorldGenLog setLeavesBlock(IBlockState leavesBlock) {
      this.leavesBlock = leavesBlock;
      return this;
   }
}
