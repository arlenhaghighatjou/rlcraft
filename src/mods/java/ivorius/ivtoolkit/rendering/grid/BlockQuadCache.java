package ivorius.ivtoolkit.rendering.grid;

import com.google.common.base.Function;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class BlockQuadCache {
   public BlockQuadCache() {
   }

   public static GridQuadCache<?> createQuadCache(IvBlockCollection blockCollection, float[] scale) {
      Object handle = new Object();
      int[] size = new int[]{blockCollection.width, blockCollection.height, blockCollection.length};
      return GridQuadCache.createQuadCache(size, scale, (input) -> {
         IBlockState block = blockCollection.getBlockState((BlockPos)input.getLeft());
         return (GridQuadCache<?>)(block.isOpaqueCube() && blockCollection.shouldRenderSide((BlockPos)input.getLeft(), (EnumFacing)input.getRight()) ? handle : null);
      });
   }
}
