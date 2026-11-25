package svenhjol.charm.world.decorator.outer;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import svenhjol.charm.world.feature.VillageDecorations;
import svenhjol.meson.decorator.MesonOuterDecorator;

public class Erosion extends MesonOuterDecorator {
   public Erosion(World world, BlockPos pos, Random rand, List<ChunkPos> chunks) {
      super(world, pos, rand, chunks);
   }

   public void generate() {
      int max = VillageDecorations.erosionDamage + this.rand.nextInt(60);

      for(int i = 0; i < max; ++i) {
         int xx = this.rand.nextInt(16) + 8;
         int zz = this.rand.nextInt(16) + 8;
         BlockPos posToErode = this.world.getHeight(this.pos.add(xx, 0, zz)).down(1 + this.rand.nextInt(12));
         IBlockState state = this.world.getBlockState(posToErode);
         IBlockState newState = null;
         if (state.getBlock() instanceof BlockPlanks || state.getBlock() instanceof BlockStairs || state.getBlock() instanceof BlockFence) {
            if (this.rand.nextFloat() < 0.92F) {
               newState = Blocks.AIR.getDefaultState();
            } else {
               newState = Blocks.WEB.getDefaultState();
            }
         }

         if (state.getBlock() instanceof BlockSlab) {
            newState = Blocks.AIR.getDefaultState();
         }

         if (state.getBlock() == Blocks.COBBLESTONE) {
            if (this.rand.nextFloat() < 0.5F) {
               newState = Blocks.MOSSY_COBBLESTONE.getDefaultState();
            } else {
               newState = Blocks.GRAVEL.getDefaultState();
            }
         }

         if (newState != null) {
            this.world.setBlockState(posToErode, newState);
         }
      }

   }
}
