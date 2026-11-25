package ivorius.reccomplex.world.gen.feature.structure.generic.transformers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockAreas;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.ivtoolkit.world.chunk.gen.StructureBoundingBoxes;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.block.RCBlocks;
import ivorius.reccomplex.gui.editstructure.transformers.TableDataSourceBTNaturalAir;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.utils.RCBlockLogic;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.BlockExpression;
import ivorius.reccomplex.utils.expression.PositionedBlockExpression;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TransformerNaturalAir extends TransformerAbstractCloud<TransformerNaturalAir.InstanceData> {
   public static final double DEFAULT_NATURAL_EXPANSION_DISTANCE = 4.0D;
   public static final double DEFAULT_NATURAL_EXPANSION_RANDOMIZATION = 10.0D;
   public static final int MAX_TREE_SIZE = 300;
   public BlockExpression sourceMatcher;
   public PositionedBlockExpression destMatcher;
   public double naturalExpansionDistance;
   public double naturalExpansionRandomization;

   public TransformerNaturalAir() {
      this((String)null, BlockExpression.of(RecurrentComplex.specialRegistry, RCBlocks.genericSpace, Integer.valueOf(1)), 4.0D, 10.0D);
   }

   public TransformerNaturalAir(@Nullable String id, String sourceMatcherExpression, double naturalExpansionDistance, double naturalExpansionRandomization) {
      super(id != null ? id : randomID(TransformerNaturalAir.class));
      this.sourceMatcher = (BlockExpression)ExpressionCache.of(new BlockExpression(RecurrentComplex.specialRegistry), sourceMatcherExpression);
      this.destMatcher = (PositionedBlockExpression)ExpressionCache.of(new PositionedBlockExpression(RecurrentComplex.specialRegistry), "");
      this.naturalExpansionDistance = naturalExpansionDistance;
      this.naturalExpansionRandomization = naturalExpansionRandomization;
   }

   protected static Stream<BlockPos> neighbors(BlockPos worldPos) {
      return BlockAreas.streamPositions(new BlockArea(worldPos.add(-1, -1, -1), worldPos.add(1, 1, 1)));
   }

   public boolean canPenetrate(Environment environment, IvWorldData worldData, BlockPos pos, double density, TransformerMulti transformer, TransformerMulti.InstanceData transformerID) {
      return ((Boolean)this.destMatcher.evaluate(() -> {
         return PositionedBlockExpression.Argument.at(environment.world, pos);
      })).booleanValue();
   }

   public boolean matches(TransformerNaturalAir.InstanceData instanceData, IBlockState state) {
      return this.sourceMatcher.test(state);
   }

   public void transformBlock(TransformerNaturalAir.InstanceData instanceData, Transformer.Phase phase, StructureSpawnContext context, BlockPos sourcePos, BlockPos worldPos, IBlockState sourceState, double density) {
      context.setBlock(worldPos, Blocks.AIR.getDefaultState(), 2);
   }

   public void transform(TransformerNaturalAir.InstanceData instanceData, Transformer.Phase phase, StructureSpawnContext context, IvWorldData worldData, RunTransformer transformer) {
      super.transform(instanceData, phase, context, worldData, transformer);
      if (phase == Transformer.Phase.AFTER) {
         WorldServer world = context.environment.world;
         IvBlockCollection blockCollection = worldData.blockCollection;
         int[] areaSize = new int[]{blockCollection.width, blockCollection.height, blockCollection.length};
         BlockPos lowerCoord = StructureBoundingBoxes.min(context.boundingBox);
         HashSet<BlockPos> check = (HashSet)instanceData.cloud.keySet().stream().flatMap((pos) -> {
            return (new BlockArea(pos.subtract(new Vec3i(2, 2, 2)), pos.add(new Vec3i(2, 2, 2)))).stream();
         }).filter((pos) -> {
            return !instanceData.cloud.containsKey(pos);
         }).map((pos) -> {
            return context.transform.apply(pos, areaSize).add(lowerCoord);
         }).collect(Collectors.toCollection(HashSet::new));
         Set<BlockPos> remove = new HashSet();
         HashSet<BlockPos> start = new HashSet();
         check.forEach((checking) -> {
            start.add(checking);
            if (visitRecursively(start, (changed, pos) -> {
               IBlockState state = world.getBlockState(pos);
               boolean isFoliage = RCBlockLogic.isFoliage(state, world, pos);
               if (!RCBlockLogic.canStay(state, world, pos)) {
                  context.setBlock(pos, Blocks.AIR.getDefaultState(), 2);
               } else {
                  if (!isFoliage && !state.getBlock().isReplaceable(world, pos)) {
                     return false;
                  }

                  if (isFoliage && remove.size() < 300 && remove.add(pos)) {
                     neighbors(pos).forEach(changed::add);
                  }
               }

               return true;
            })) {
               remove.forEach((pos) -> {
                  context.setBlock(pos, Blocks.AIR.getDefaultState(), 2);
               });
            }

            start.clear();
            remove.clear();
         });
      }

   }

   @SideOnly(Side.CLIENT)
   public String getDisplayString() {
      return "Natural Air: " + this.sourceMatcher.getDisplayString((Object)null);
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceBTNaturalAir(this, navigator, delegate);
   }

   public TransformerNaturalAir.InstanceData prepareInstanceData(StructurePrepareContext context, IvWorldData worldData) {
      return new TransformerNaturalAir.InstanceData();
   }

   public TransformerNaturalAir.InstanceData loadInstanceData(StructureLoadContext context, NBTBase nbt) {
      TransformerNaturalAir.InstanceData instanceData = new TransformerNaturalAir.InstanceData();
      instanceData.readFromNBT(nbt);
      return instanceData;
   }

   public double cloudExpansionDistance() {
      return this.naturalExpansionDistance;
   }

   public double cloudExpansionRandomization() {
      return this.naturalExpansionRandomization;
   }

   public boolean generatesInPhase(TransformerNaturalAir.InstanceData instanceData, Transformer.Phase phase) {
      return phase == Transformer.Phase.BEFORE;
   }

   public static class InstanceData extends TransformerAbstractCloud.InstanceData {
      public InstanceData() {
      }
   }

   public static class Serializer implements JsonDeserializer<TransformerNaturalAir>, JsonSerializer<TransformerNaturalAir> {
      private MCRegistry registry;

      public Serializer(MCRegistry registry) {
         this.registry = registry;
      }

      public TransformerNaturalAir deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context) {
         JsonObject jsonObject = JsonUtils.asJsonObject(jsonElement, "transformerNatural");
         String id = Transformer.readID(jsonObject);
         String expression = TransformerReplace.Serializer.readLegacyMatcher(jsonObject, "source", "sourceMetadata");
         if (expression == null) {
            expression = JsonUtils.getString(jsonObject, "sourceExpression", "");
         }

         double naturalExpansionDistance = JsonUtils.getDouble(jsonObject, "naturalExpansionDistance", 4.0D);
         double naturalExpansionRandomization = JsonUtils.getDouble(jsonObject, "naturalExpansionRandomization", 10.0D);
         TransformerNaturalAir transformer = new TransformerNaturalAir(id, expression, naturalExpansionDistance, naturalExpansionRandomization);
         transformer.destMatcher.setExpression(JsonUtils.getString(jsonObject, "destExpression", ""));
         return transformer;
      }

      public JsonElement serialize(TransformerNaturalAir transformer, Type par2Type, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("id", transformer.id());
         jsonObject.addProperty("sourceExpression", transformer.sourceMatcher.getExpression());
         jsonObject.addProperty("destExpression", transformer.destMatcher.getExpression());
         jsonObject.addProperty("naturalExpansionDistance", Double.valueOf(transformer.naturalExpansionDistance));
         jsonObject.addProperty("naturalExpansionRandomization", Double.valueOf(transformer.naturalExpansionRandomization));
         return jsonObject;
      }
   }
}
