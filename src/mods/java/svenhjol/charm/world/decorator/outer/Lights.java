package svenhjol.charm.world.decorator.outer;

import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import svenhjol.charm.Charm;
import svenhjol.charm.crafting.feature.Lantern;
import svenhjol.charm.world.compat.FutureMcBlocks;
import svenhjol.meson.decorator.MesonOuterDecorator;

public class Lights extends MesonOuterDecorator {
   public Lights(World world, BlockPos pos, Random rand, List<ChunkPos> chunks) {
      super(world, pos, rand, chunks);
   }

   public void generate() {
      int max = 1 + this.rand.nextInt(4);

      for(int i = 0; i < max; ++i) {
         int xx = this.rand.nextInt(16) + 8;
         int zz = this.rand.nextInt(16) + 8;
         BlockPos posForLight = this.world.getHeight(this.pos.add(xx, 0, zz));
         boolean airAbove = this.world.getBlockState(posForLight) == Blocks.AIR.getDefaultState();
         boolean solidBelow = this.world.getBlockState(posForLight.offset(EnumFacing.DOWN)).isNormalCube();
         if (airAbove && solidBelow) {
            float f = this.rand.nextFloat();
            IBlockState light;
            if (f > 0.93F && (Charm.hasFeature(Lantern.class) || FutureMcBlocks.lanternHangingProperty != null)) {
               if (Lantern.ironLantern == null && FutureMcBlocks.lanternHangingProperty != null) {
                  light = FutureMcBlocks.lantern.getDefaultState().withProperty(FutureMcBlocks.lanternHangingProperty, Boolean.valueOf(false));
               } else {
                  light = Lantern.getDefaultLantern().getDefaultState();
               }
            } else if (f > 0.84F) {
               light = Blocks.REDSTONE_TORCH.getDefaultState();
            } else {
               light = Blocks.TORCH.getDefaultState();
            }

            this.world.setBlockState(posForLight, light);
         }
      }

   }
}
