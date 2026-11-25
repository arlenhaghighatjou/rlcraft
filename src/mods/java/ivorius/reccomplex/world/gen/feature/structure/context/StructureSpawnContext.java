package ivorius.reccomplex.world.gen.feature.structure.context;

import ivorius.ivtoolkit.blocks.BlockAreas;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.world.chunk.gen.StructureBoundingBoxes;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.utils.RCAxisAlignedTransform;
import ivorius.reccomplex.utils.RCBlockAreas;
import ivorius.reccomplex.utils.RCStructureBoundingBoxes;
import ivorius.reccomplex.world.gen.feature.HeightMapFreezer;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class StructureSpawnContext extends StructureLiveContext {
   @Nonnull
   public final Random random;
   @Nullable
   public final StructureBoundingBox generationBB;
   @Nullable
   public final Predicate<Vec3i> generationPredicate;
   public final int generationLayer;
   public final StructureSpawnContext.GenerateMaturity generateMaturity;
   private HeightMapFreezer heightMapFreezer;

   public StructureSpawnContext(@Nonnull Environment environment, @Nonnull Random random, @Nonnull AxisAlignedTransform2D transform, @Nonnull StructureBoundingBox boundingBox, @Nullable StructureBoundingBox generationBB, Predicate<Vec3i> generationPredicate, int generationLayer, boolean generateAsSource, StructureSpawnContext.GenerateMaturity generateMaturity) {
      super(transform, boundingBox, generateAsSource, environment);
      this.random = random;
      this.generationBB = generationBB;
      this.generationPredicate = generationPredicate;
      this.generationLayer = generationLayer;
      this.generateMaturity = generateMaturity;
   }

   @Nullable
   public StructureBoundingBox sourceIntersection(StructureBoundingBox area) {
      return this.generationBB != null ? RCStructureBoundingBoxes.intersection(area, BlockAreas.toBoundingBox(RCAxisAlignedTransform.apply(RCAxisAlignedTransform.invert(this.transform), RCBlockAreas.sub(RCBlockAreas.from(this.generationBB), StructureBoundingBoxes.min(this.boundingBox)), StructureBoundingBoxes.size(this.boundingBox), 1))) : area;
   }

   public StructureBoundingBox intersection(StructureBoundingBox area) {
      return this.generationBB != null ? RCStructureBoundingBoxes.intersection(area, this.generationBB) : area;
   }

   public boolean includes(Vec3i coord) {
      return (this.generationBB == null || this.generationBB.isVecInside(coord)) && (this.generationPredicate == null || this.generationPredicate.test(coord));
   }

   public boolean includesComplex(Vec3i coord) {
      return this.generationPredicate == null || this.generationPredicate.test(coord);
   }

   public boolean setBlock(BlockPos pos, IBlockState state, int flag) {
      if (!RecurrentComplex.specialRegistry.isSafe(state.getBlock())) {
         this.environment.world.setBlockState(pos.toImmutable(), Blocks.AIR.getDefaultState(), 2);
         return false;
      } else if (this.includes(pos)) {
         this.environment.world.setBlockState(pos.toImmutable(), state, flag);
         if (this.heightMapFreezer != null) {
            this.heightMapFreezer.markBlock(pos, state);
         }

         return true;
      } else {
         return false;
      }
   }

   public void freezeHeightMap(StructureBoundingBox boundingBox) {
      this.heightMapFreezer = HeightMapFreezer.freeze(boundingBox, this.environment.world);
   }

   public void meltHeightMap() {
      this.heightMapFreezer.melt();
      this.heightMapFreezer = null;
   }

   public static enum GenerateMaturity {
      PLAN,
      SUGGEST,
      FIRST,
      COMPLEMENT;

      private GenerateMaturity() {
      }

      public boolean isSuggest() {
         return this == PLAN || this == SUGGEST;
      }

      public boolean isFirstTime() {
         return this != COMPLEMENT;
      }

      public boolean generates() {
         return this != PLAN;
      }
   }
}
