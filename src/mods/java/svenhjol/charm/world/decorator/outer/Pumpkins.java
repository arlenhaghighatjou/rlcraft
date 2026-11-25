package svenhjol.charm.world.decorator.outer;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import svenhjol.charm.world.feature.VillageDecorations;
import svenhjol.meson.decorator.MesonOuterDecorator;

public class Pumpkins extends MesonOuterDecorator {
   public Pumpkins(World world, BlockPos pos, Random rand, List<ChunkPos> chunks) {
      super(world, pos, rand, chunks);
   }

   public void generate() {
      int max = 3;

      for(int i = 0; i < max; ++i) {
         if ((double)this.rand.nextFloat() >= VillageDecorations.pumpkinsWeight) {
            int xx = this.rand.nextInt(16) + 8;
            int zz = this.rand.nextInt(16) + 8;
            BlockPos current = this.world.getHeight(this.pos.add(xx, 0, zz));
            boolean airAbove = this.world.getBlockState(current) == Blocks.AIR.getDefaultState();
            boolean grassBelow = this.world.getBlockState(current.offset(EnumFacing.DOWN)) == Blocks.GRASS.getDefaultState();
            if (airAbove && grassBelow) {
               float r = this.rand.nextFloat();
               IBlockState state;
               if ((double)r <= 0.05D) {
                  state = Blocks.MELON_BLOCK.getDefaultState();
               } else if ((double)r <= 0.3D) {
                  state = Blocks.LIT_PUMPKIN.getDefaultState().withProperty(BlockPumpkin.FACING, EnumFacing.getHorizontal(this.rand.nextInt(4)));
               } else {
                  state = Blocks.PUMPKIN.getDefaultState().withProperty(BlockPumpkin.FACING, EnumFacing.getHorizontal(this.rand.nextInt(4)));
               }

               this.world.setBlockState(current, state);
            }
         }
      }

   }
}
