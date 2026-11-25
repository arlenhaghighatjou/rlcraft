package reborncore.client.multiblock;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.BlockFluidRenderer;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class RebornFluidRenderer extends BlockFluidRenderer {
   public RebornFluidRenderer() {
      super(BlockColors.init());
   }

   public float getFluidHeight(IBlockAccess blockAccess, BlockPos blockPosIn, Material blockMaterial) {
      return 1.0F;
   }
}
