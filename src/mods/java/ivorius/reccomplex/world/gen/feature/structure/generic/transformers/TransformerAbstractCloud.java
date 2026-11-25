package ivorius.reccomplex.world.gen.feature.structure.generic.transformers;

import com.google.common.collect.Sets;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import gnu.trove.procedure.TObjectDoubleProcedure;
import ivorius.ivtoolkit.blocks.BlockAreas;
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.blocks.IvMutableBlockPos;
import ivorius.ivtoolkit.random.BlurredValueField;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.tools.NBTTagLists;
import ivorius.ivtoolkit.world.chunk.gen.StructureBoundingBoxes;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLiveContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

public abstract class TransformerAbstractCloud<S extends TransformerAbstractCloud.InstanceData> extends Transformer<S> {
   public TransformerAbstractCloud(@Nonnull String id) {
      super(id);
   }

   public static <T> boolean visitRecursively(HashSet<T> start, BiPredicate<Set<T>, T> changedConsumer) {
      Set<T> changedL = start;
      Set<T> changedR = new HashSet();

      while(changedL.size() > 0 || changedR.size() > 0) {
         Set<T> current = changedL.size() > 0 ? changedL : changedR;
         Set<T> next = current == changedL ? changedR : changedL;

         for(T t : current) {
            if (!changedConsumer.test(next, t)) {
               return false;
            }
         }

         current.clear();
      }

      return true;
   }

   public abstract double cloudExpansionDistance();

   public abstract double cloudExpansionRandomization();

   public boolean skipGeneration(S instanceData, StructureLiveContext context, BlockPos pos, IBlockState state, IvWorldData worldData, BlockPos sourcePos) {
      return this.matches(instanceData, state);
   }

   public TObjectDoubleMap<BlockPos> buildCloud(S instanceData, IvWorldData worldData, StructurePrepareContext context, TransformerMulti transformer, TransformerMulti.InstanceData transformerInstanceData) {
      Random random = context.random;
      Environment environment = context.environment;
      BlockPos lowerCoord = StructureBoundingBoxes.min(context.boundingBox);
      int[] strucSize = new int[]{worldData.blockCollection.width, worldData.blockCollection.height, worldData.blockCollection.length};
      TObjectDoubleMap<BlockPos> cloud = new TObjectDoubleHashMap();
      BlurredValueField blurredValueField = new BlurredValueField(strucSize);
      int gridCoords = 1;

      for(int d : strucSize) {
         gridCoords *= d;
      }

      int values = MathHelper.floor((float)gridCoords * 0.04F + 0.5F);

      for(int i = 0; i < values; ++i) {
         blurredValueField.addValue((double)(1.0F + (random.nextFloat() - random.nextFloat()) * (float)this.cloudExpansionRandomization() / 100.0F), random);
      }

      BlockAreas.mutablePositions(worldData.blockCollection.area()).forEach((pos) -> {
         IBlockState state = worldData.blockCollection.getBlockState(pos);
         BlockPos worldCoord = context.transform.apply(pos, strucSize).add(lowerCoord);
         if (this.matches(instanceData, state) && this.canPenetrate(environment, worldData, worldCoord, 1.0D, transformer, transformerInstanceData)) {
            cloud.put(pos.toImmutable(), 1.0D);
         }

      });
      double expansionDistance = this.cloudExpansionDistance();
      MutableBlockPos sidePos = new MutableBlockPos();
      MutableBlockPos sideWorldCoord = new MutableBlockPos();
      if (expansionDistance > 1.0E-6D) {
         double[] sideFalloffs = new double[6];
         List<EnumFacing> checkSides = new ArrayList();

         for(EnumFacing side : EnumFacing.values()) {
            double sideExpansionDistance = this.cloudExpansionDistance(side);
            double sideFalloff = sideExpansionDistance > 1.0E-6D ? 1.0D / sideExpansionDistance / expansionDistance : 0.0D;
            if (sideFalloff > 0.0D) {
               checkSides.add(side);
               sideFalloffs[side.getIndex()] = sideFalloff;
            }
         }

         visitRecursively(Sets.newHashSet(cloud.keySet()), (changed, pos) -> {
            double density = cloud.get(pos);

            for(EnumFacing side : checkSides) {
               double sideFalloff = sideFalloffs[side.getIndex()];
               IvMutableBlockPos.offset(pos, sidePos, side);
               double sideDensity = density - sideFalloff * blurredValueField.getValue(new int[]{sidePos.getX(), sidePos.getY(), sidePos.getZ()});
               if (sideDensity > 0.0D && cloud.get(sidePos) < sideDensity - 1.0E-5D) {
                  IvMutableBlockPos.add(context.transform.applyOn(sidePos, sideWorldCoord, strucSize), lowerCoord);
                  if (this.canPenetrate(environment, worldData, sideWorldCoord, sideDensity, transformer, transformerInstanceData)) {
                     BlockPos immutableSidePos = sidePos.toImmutable();
                     cloud.put(immutableSidePos, sideDensity);
                     changed.add(immutableSidePos);
                  }
               }
            }

            return true;
         });
      }

      return cloud;
   }

   public boolean canPenetrate(Environment environment, IvWorldData worldData, BlockPos pos, double density, TransformerMulti transformer, TransformerMulti.InstanceData transformerID) {
      return true;
   }

   protected double cloudExpansionDistance(EnumFacing side) {
      return 1.0D;
   }

   public void configureInstanceData(S s, StructurePrepareContext context, IvWorldData worldData, RunTransformer transformer) {
      s.cloud = this.buildCloud(s, worldData, context, transformer.transformer, transformer.instanceData);
   }

   public void transform(S instanceData, Transformer.Phase phase, StructureSpawnContext context, IvWorldData worldData, RunTransformer transformer) {
      if (this.generatesInPhase(instanceData, phase)) {
         IvBlockCollection blockCollection = worldData.blockCollection;
         int[] areaSize = new int[]{blockCollection.width, blockCollection.height, blockCollection.length};
         BlockPos lowerCoord = StructureBoundingBoxes.min(context.boundingBox);
         MutableBlockPos worldCoord = new MutableBlockPos();
         instanceData.cloud.forEachEntry((sourcePos, density) -> {
            IvMutableBlockPos.add(context.transform.applyOn(sourcePos, worldCoord, areaSize), lowerCoord);
            this.transformBlock(instanceData, phase, context, sourcePos, worldCoord, worldData.blockCollection.getBlockState(sourcePos), density);
            return (void)true;
         });
      }

   }

   public abstract boolean generatesInPhase(S var1, Transformer.Phase var2);

   public abstract boolean matches(S var1, IBlockState var2);

   public abstract void transformBlock(S var1, Transformer.Phase var2, StructureSpawnContext var3, BlockPos var4, BlockPos var5, IBlockState var6, double var7);

   public static class InstanceData implements NBTStorable {
      public TObjectDoubleMap<BlockPos> cloud = new TObjectDoubleHashMap();

      public InstanceData() {
      }

      public void readFromNBT(NBTBase base) {
         NBTTagCompound compound = base instanceof NBTTagCompound ? (NBTTagCompound)base : new NBTTagCompound();
         NBTTagLists.compoundsFrom(compound, "cloud").forEach((cloudCompound) -> {
            BlockPos pos = BlockPositions.readFromNBT("particle", cloudCompound);
            if (pos != null) {
               this.cloud.put(pos, cloudCompound.getDouble("density"));
            }

         });
      }

      public NBTBase writeToNBT() {
         NBTTagCompound compound = new NBTTagCompound();
         List<NBTTagCompound> cloudCompounds = new ArrayList();
         this.cloud.forEachEntry((pos, density) -> {
            NBTTagCompound cloudCompound = new NBTTagCompound();
            BlockPositions.writeToNBT("particle", pos, cloudCompound);
            cloudCompound.setDouble("density", density);
            cloudCompounds.add(cloudCompound);
            return true;
         });
         NBTTagLists.writeTo(compound, "cloud", cloudCompounds);
         return compound;
      }
   }
}
