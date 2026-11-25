package ivorius.reccomplex.world.gen.feature.structure.context;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import javax.annotation.Nonnull;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class StructureLiveContext extends StructureContext {
   @Nonnull
   public Environment environment;

   public StructureLiveContext(@Nonnull AxisAlignedTransform2D transform, @Nonnull StructureBoundingBox boundingBox, boolean generateAsSource, @Nonnull Environment environment) {
      super(transform, boundingBox, generateAsSource);
      this.environment = environment;
   }
}
