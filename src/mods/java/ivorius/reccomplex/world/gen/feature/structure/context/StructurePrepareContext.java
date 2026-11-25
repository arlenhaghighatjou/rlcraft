package ivorius.reccomplex.world.gen.feature.structure.context;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class StructurePrepareContext extends StructureLiveContext {
   @Nonnull
   public final EaglercraftRandom random;
   @Nonnull
   public final StructureSpawnContext.GenerateMaturity generateMaturity;

   public StructurePrepareContext(@Nonnull AxisAlignedTransform2D transform, @Nonnull StructureBoundingBox boundingBox, boolean generateAsSource, @Nonnull Environment environment, @Nonnull EaglercraftRandom random, @Nonnull StructureSpawnContext.GenerateMaturity generateMaturity) {
      super(transform, boundingBox, generateAsSource, environment);
      this.random = random;
      this.generateMaturity = generateMaturity;
   }
}
