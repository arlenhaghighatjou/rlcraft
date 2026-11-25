package ivorius.reccomplex.world.gen.feature.structure.context;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import javax.annotation.Nonnull;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class StructureLoadContext extends StructureContext {
   public StructureLoadContext(@Nonnull AxisAlignedTransform2D transform, @Nonnull StructureBoundingBox boundingBox, boolean generateAsSource) {
      super(transform, boundingBox, generateAsSource);
   }
}
