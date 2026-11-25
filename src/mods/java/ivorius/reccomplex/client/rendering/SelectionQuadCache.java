package ivorius.reccomplex.client.rendering;

import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.rendering.grid.GridQuadCache;
import ivorius.reccomplex.gui.GuiHider;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class SelectionQuadCache {
   public SelectionQuadCache() {
   }

   public static class Visualizer implements GuiHider.Visualizer {
      public static final String NULL_PLACEHOLDER = "_null_";
      protected GridQuadCache<?> quadCache;
      protected BlockPos lowerCoord;

      public Visualizer(Selection selection, MazeVisualizationContext context) {
         Selection realWorldSelection = context.mapSelection(selection);
         this.lowerCoord = BlockPositions.fromIntArray(realWorldSelection.boundsLower());
         Map<MazeRoom, String> compiled = realWorldSelection.compile(true);
         Map<BlockPos, String> coords = (Map)compiled.entrySet().stream().collect(Collectors.toMap((e) -> {
            return BlockPositions.fromIntArray(((MazeRoom)e.getKey()).getCoordinates()).subtract(this.lowerCoord);
         }, (e) -> {
            return e.getValue() != null ? (String)e.getValue() : "_null_";
         }));
         this.quadCache = GridQuadCache.createQuadCache(realWorldSelection.boundsSize(), new float[]{1.0F, 1.0F, 1.0F}, (input) -> {
            BlockPos coord = (BlockPos)input.getLeft();
            EnumFacing direction = (EnumFacing)input.getRight();
            String cur = (String)coords.get(coord);
            return cur != null && !Objects.equals(coords.get(coord.offset(direction)), cur) ? cur : null;
         });
      }

      public void draw(Entity renderEntity, float partialTicks) {
         GlStateManager.color(0.8F, 0.75F, 0.5F);
         OperationRenderer.renderGridQuadCache(this.quadCache, AxisAlignedTransform2D.ORIGINAL, this.lowerCoord, renderEntity.ticksExisted, partialTicks);
      }
   }
}
