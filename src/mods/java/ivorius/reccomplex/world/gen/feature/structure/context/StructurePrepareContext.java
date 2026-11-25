package ivorius.reccomplex.world.gen.feature.structure.context;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class StructurePrepareContext extends StructureLiveContext {
   @Nonnull
   public final Random random;
   @Nonnull
   public final StructureSpawnContext.GenerateMaturity generateMaturity;

   public StructurePrepareContext(@Nonnull AxisAlignedTransform2D transform, @Nonnull StructureBoundingBox boundingBox, boolean generateAsSource, @Nonnull Environment environment, @Nonnull Random random, @Nonnull StructureSpawnContext.GenerateMaturity generateMaturity) {
      super(transform, boundingBox, generateAsSource, environment);
      this.random = random;
      this.generateMaturity = generateMaturity;
   }
}
