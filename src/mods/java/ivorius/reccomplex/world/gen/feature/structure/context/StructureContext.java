package ivorius.reccomplex.world.gen.feature.structure.context;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.world.chunk.gen.StructureBoundingBoxes;
import javax.annotation.Nonnull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class StructureContext {
   @Nonnull
   public final AxisAlignedTransform2D transform;
   @Nonnull
   public final StructureBoundingBox boundingBox;
   public final boolean generateAsSource;

   public StructureContext(@Nonnull AxisAlignedTransform2D transform, @Nonnull StructureBoundingBox boundingBox, boolean generateAsSource) {
      this.transform = transform;
      this.boundingBox = boundingBox;
      this.generateAsSource = generateAsSource;
   }

   public BlockPos lowerCoord() {
      return StructureBoundingBoxes.min(this.boundingBox);
   }

   public int[] areaSize() {
      return StructureBoundingBoxes.size(this.boundingBox);
   }
}
