package ivorius.reccomplex.client.rendering;

import com.google.common.base.Function;
import ivorius.ivtoolkit.rendering.grid.GridQuadCache;
import ivorius.reccomplex.world.gen.feature.structure.schematics.SchematicFile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class SchematicQuadCache {
   public SchematicQuadCache() {
   }

   public static GridQuadCache<?> createQuadCache(SchematicFile schematic, float[] scale) {
      Object handle = new Object();
      return GridQuadCache.createQuadCache(new int[]{schematic.width, schematic.height, schematic.length}, scale, (input) -> {
         BlockPos coord = (BlockPos)input.getLeft();
         EnumFacing direction = (EnumFacing)input.getRight();
         IBlockState blockState = schematic.getBlockState(coord);
         return (GridQuadCache<?>)(blockState.isOpaqueCube() && schematic.shouldRenderSide(coord, direction) ? handle : null);
      });
   }
}
