package ivorius.reccomplex.world.gen.feature.structure.generic.placement;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class StructurePlaceContext {
   @Nonnull
   public final EaglercraftRandom random;
   @Nonnull
   public final Environment environment;
   @Nonnull
   public final AxisAlignedTransform2D transform;
   @Nonnull
   public final StructureBoundingBox boundingBox;

   public StructurePlaceContext(@Nonnull EaglercraftRandom random, Environment environment, @Nonnull AxisAlignedTransform2D transform, @Nonnull StructureBoundingBox boundingBox) {
      this.random = random;
      this.environment = environment;
      this.transform = transform;
      this.boundingBox = boundingBox;
   }
}
