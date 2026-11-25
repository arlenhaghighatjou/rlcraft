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
import ivorius.reccomplex.gui.editstructure.transformers.TableDataSourceBTNegativeSpace;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.nbt.NBTNone;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.BlockExpression;
import ivorius.reccomplex.utils.expression.PositionedBlockExpression;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLiveContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import java.lang.reflect.Type;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TransformerNegativeSpace extends Transformer<NBTNone> {
   public BlockExpression sourceMatcher;
   public PositionedBlockExpression destMatcher;

   public TransformerNegativeSpace() {
      this((String)null, BlockExpression.of(RecurrentComplex.specialRegistry, RCBlocks.genericSpace, Integer.valueOf(0)), "");
   }

   public TransformerNegativeSpace(@Nullable String id, String sourceExpression, String destExpression) {
      super(id != null ? id : randomID(TransformerNegativeSpace.class));
      this.sourceMatcher = (BlockExpression)ExpressionCache.of(new BlockExpression(RecurrentComplex.specialRegistry), sourceExpression);
      this.destMatcher = (PositionedBlockExpression)ExpressionCache.of(new PositionedBlockExpression(RecurrentComplex.specialRegistry), destExpression);
   }

   public boolean skipGeneration(NBTNone instanceData, StructureLiveContext context, BlockPos pos, IBlockState state, IvWorldData worldData, BlockPos sourcePos) {
      return this.sourceMatcher.test(state) && ((Boolean)this.destMatcher.evaluate(() -> {
         return PositionedBlockExpression.Argument.at(context.environment.world, pos);
      })).booleanValue();
   }

   @SideOnly(Side.CLIENT)
   public String getDisplayString() {
      return "Space: " + this.sourceMatcher.getDisplayString((Object)null);
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceBTNegativeSpace(this, navigator, delegate);
   }

   public NBTNone prepareInstanceData(StructurePrepareContext context, IvWorldData worldData) {
      return new NBTNone();
   }

   public NBTNone loadInstanceData(StructureLoadContext context, NBTBase nbt) {
      return new NBTNone();
   }

   public static class Serializer implements JsonDeserializer<TransformerNegativeSpace>, JsonSerializer<TransformerNegativeSpace> {
      private MCRegistry registry;

      public Serializer(MCRegistry registry) {
         this.registry = registry;
      }

      public TransformerNegativeSpace deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context) {
         JsonObject jsonObject = JsonUtils.asJsonObject(jsonElement, "transformerNegativeSpace");
         String id = Transformer.readID(jsonObject);
         String expression = TransformerReplace.Serializer.readLegacyMatcher(jsonObject, "source", "sourceMetadata");
         if (expression == null) {
            expression = JsonUtils.getString(jsonObject, "sourceExpression", "");
         }

         String destExpression = JsonUtils.getString(jsonObject, "destExpression", "");
         return new TransformerNegativeSpace(id, expression, destExpression);
      }

      public JsonElement serialize(TransformerNegativeSpace transformer, Type par2Type, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("id", transformer.id());
         jsonObject.addProperty("sourceExpression", transformer.sourceMatcher.getExpression());
         jsonObject.addProperty("destExpression", transformer.destMatcher.getExpression());
         return jsonObject;
      }
   }
}
