package ivorius.reccomplex.world.gen.feature.structure.generic.transformers;

import ivorius.ivtoolkit.blocks.BlockAreas;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.blocks.IvMutableBlockPos;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.world.chunk.gen.StructureBoundingBoxes;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.utils.RCStructureBoundingBoxes;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLiveContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public abstract class TransformerSingleBlock<S extends NBTStorable> extends Transformer<S> {
   public TransformerSingleBlock(@Nonnull String id) {
      super(id);
   }

   public boolean skipGeneration(S instanceData, StructureLiveContext context, BlockPos pos, IBlockState state, IvWorldData worldData, BlockPos sourcePos) {
      return this.matches(context.environment, instanceData, sourcePos, state);
   }

   public void transform(S instanceData, Transformer.Phase phase, StructureSpawnContext context, IvWorldData worldData, RunTransformer transformer) {
      if (this.generatesInPhase(instanceData, phase)) {
         IvBlockCollection blockCollection = worldData.blockCollection;
         StructureBoundingBox relevantSourceArea = context.sourceIntersection(BlockAreas.toBoundingBox(blockCollection.area()));
         if (relevantSourceArea != null) {
            BlockPos lowerCoord = StructureBoundingBoxes.min(context.boundingBox);
            int[] areaSize = new int[]{blockCollection.width, blockCollection.height, blockCollection.length};
            context.freezeHeightMap(context.intersection(context.boundingBox));
            MutableBlockPos worldCoord = new MutableBlockPos();

            for(BlockPos sourceCoord : RCStructureBoundingBoxes.mutablePositions(relevantSourceArea)) {
               IvMutableBlockPos.add(context.transform.applyOn(sourceCoord, worldCoord, areaSize), lowerCoord);
               if (context.includesComplex(worldCoord)) {
                  IBlockState state = blockCollection.getBlockState(sourceCoord);
                  if (this.matches(context.environment, instanceData, sourceCoord, state)) {
                     this.transformBlock(instanceData, Transformer.Phase.BEFORE, context, transformer, areaSize, worldCoord, state);
                  }
               }
            }

            context.meltHeightMap();
         }
      }
   }

   public abstract boolean generatesInPhase(S var1, Transformer.Phase var2);

   public abstract boolean matches(Environment var1, S var2, BlockPos var3, IBlockState var4);

   public abstract void transformBlock(S var1, Transformer.Phase var2, StructureSpawnContext var3, RunTransformer var4, int[] var5, BlockPos var6, IBlockState var7);
}
