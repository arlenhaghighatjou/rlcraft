package ivorius.reccomplex.world.gen.feature.structure.generic.transformers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.block.RCBlocks;
import ivorius.reccomplex.gui.editstructure.transformers.TableDataSourceBTNatural;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.BlockExpression;
import ivorius.reccomplex.utils.expression.PositionedBlockExpression;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TransformerNatural extends TransformerAbstractCloud<TransformerNatural.InstanceData> {
   public static final double DEFAULT_NATURAL_EXPANSION_DISTANCE = 4.0D;
   public static final double DEFAULT_NATURAL_EXPANSION_RANDOMIZATION = 6.0D;
   public BlockExpression sourceMatcher;
   public PositionedBlockExpression destMatcher;
   public double naturalExpansionDistance;
   public double naturalExpansionRandomization;

   public TransformerNatural() {
      this((String)null, BlockExpression.of(RecurrentComplex.specialRegistry, RCBlocks.genericSolid, Integer.valueOf(0)), 4.0D, 6.0D);
      this.destMatcher.setExpression("is:replaceable | is:foliage");
   }

   public TransformerNatural(@Nullable String id, String sourceMatcherExpression, double naturalExpansionDistance, double naturalExpansionRandomization) {
      super(id != null ? id : randomID(TransformerNatural.class));
      this.sourceMatcher = (BlockExpression)ExpressionCache.of(new BlockExpression(RecurrentComplex.specialRegistry), sourceMatcherExpression);
      this.destMatcher = (PositionedBlockExpression)ExpressionCache.of(new PositionedBlockExpression(RecurrentComplex.specialRegistry), "");
      this.naturalExpansionDistance = naturalExpansionDistance;
      this.naturalExpansionRandomization = naturalExpansionRandomization;
   }

   public static void addIfNew(List<int[]> list, int... object) {
      if (!list.contains(object)) {
         list.add(object);
      }

   }

   public boolean matches(TransformerNatural.InstanceData instanceData, IBlockState state) {
      return this.sourceMatcher.test(state);
   }

   protected double cloudExpansionDistance(EnumFacing side) {
      return side == EnumFacing.UP ? 0.0D : (side.getHorizontalIndex() >= 0 ? 0.5D : super.cloudExpansionDistance(side));
   }

   public boolean canPenetrate(Environment environment, IvWorldData worldData, BlockPos pos, double density, TransformerMulti transformer, TransformerMulti.InstanceData transformerID) {
      return density >= 1.0D || ((Boolean)this.destMatcher.evaluate(() -> {
         return PositionedBlockExpression.Argument.at(environment.world, pos);
      })).booleanValue();
   }

   public void transformBlock(TransformerNatural.InstanceData instanceData, Transformer.Phase phase, StructureSpawnContext context, BlockPos sourcePos, BlockPos pos, IBlockState sourceState, double density) {
      World world = context.environment.world;
      Biome biome = context.environment.biome;
      IBlockState topBlock = biome.topBlock != null ? biome.topBlock : Blocks.AIR.getDefaultState();
      IBlockState fillerBlock = biome.fillerBlock != null ? biome.fillerBlock : Blocks.AIR.getDefaultState();
      IBlockState mainBlock = Blocks.STONE.getDefaultState();
      boolean useStoneBlock = pos.getY() < world.getSeaLevel() - 3;
      IBlockState setBlock = useStoneBlock ? mainBlock : (instanceData.cloud.containsKey(sourcePos.up()) ? fillerBlock : topBlock);
      if (world.provider.getDimension() == -1) {
         setBlock = Blocks.NETHERRACK.getDefaultState();
      } else if (world.provider.getDimension() == 1) {
         setBlock = Blocks.END_STONE.getDefaultState();
      }

      context.setBlock(pos, setBlock, 2);
   }

   public double cloudExpansionDistance() {
      return this.naturalExpansionDistance;
   }

   public double cloudExpansionRandomization() {
      return this.naturalExpansionRandomization;
   }

   public boolean generatesInPhase(TransformerNatural.InstanceData instanceData, Transformer.Phase phase) {
      return phase == Transformer.Phase.BEFORE;
   }

   public TransformerNatural.InstanceData prepareInstanceData(StructurePrepareContext context, IvWorldData worldData) {
      return new TransformerNatural.InstanceData();
   }

   public TransformerNatural.InstanceData loadInstanceData(StructureLoadContext context, NBTBase nbt) {
      TransformerNatural.InstanceData instanceData = new TransformerNatural.InstanceData();
      instanceData.readFromNBT(nbt);
      return instanceData;
   }

   @SideOnly(Side.CLIENT)
   public String getDisplayString() {
      return "Natural: " + this.sourceMatcher.getDisplayString((Object)null);
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceBTNatural(this, navigator, delegate);
   }

   public static class InstanceData extends TransformerAbstractCloud.InstanceData {
      public InstanceData() {
      }
   }

   public static class Serializer implements JsonDeserializer<TransformerNatural>, JsonSerializer<TransformerNatural> {
      private MCRegistry registry;

      public Serializer(MCRegistry registry) {
         this.registry = registry;
      }

      public TransformerNatural deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context) {
         JsonObject jsonObject = JsonUtils.asJsonObject(jsonElement, "transformerNatural");
         String id = Transformer.readID(jsonObject);
         String expression = TransformerReplace.Serializer.readLegacyMatcher(jsonObject, "source", "sourceMetadata");
         if (expression == null) {
            expression = JsonUtils.getString(jsonObject, "sourceExpression", "");
         }

         double naturalExpansionDistance = JsonUtils.getDouble(jsonObject, "naturalExpansionDistance", 4.0D);
         double naturalExpansionRandomization = JsonUtils.getDouble(jsonObject, "naturalExpansionRandomization", 6.0D);
         TransformerNatural transformer = new TransformerNatural(id, expression, naturalExpansionDistance, naturalExpansionRandomization);
         transformer.destMatcher.setExpression(JsonUtils.getString(jsonObject, "destExpression", "is:replaceable | is:foliage"));
         return transformer;
      }

      public JsonElement serialize(TransformerNatural transformer, Type par2Type, JsonSerializationContext context) {
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
