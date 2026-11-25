package ivorius.reccomplex.world.gen.feature.structure.generic.placement;

import com.google.common.math.DoubleMath;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.blocks.BlockAreas;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.gui.IntegerRange;
import ivorius.ivtoolkit.util.LineSelection;
import ivorius.ivtoolkit.world.WorldCache;
import ivorius.ivtoolkit.world.chunk.gen.StructureBoundingBoxes;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.editstructure.placer.TableDataSourceFactorMatch;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.utils.IntegerRanges;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.BlockExpression;
import ivorius.reccomplex.utils.expression.PositionedBlockExpression;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

public class FactorMatch extends GenericPlacer.Factor {
   public BlockExpression sourceMatcher;
   public PositionedBlockExpression destMatcher;
   public float requiredConformity;

   public FactorMatch() {
      this(1.0F, "", "", 0.5F);
   }

   public FactorMatch(float priority, String sourceExpression, String destExpression, float requiredConformity) {
      super(priority);
      this.sourceMatcher = (BlockExpression)ExpressionCache.of(new BlockExpression(RecurrentComplex.specialRegistry), sourceExpression);
      this.destMatcher = (PositionedBlockExpression)ExpressionCache.of(new PositionedBlockExpression(RecurrentComplex.specialRegistry), destExpression);
      this.requiredConformity = requiredConformity;
   }

   protected float weight(WorldCache cache, Set<? extends BlockPos> sources, float needed) {
      int failChances = (int)((float)sources.size() * (1.0F - needed));
      int matched = 0;

      for(BlockPos pos : sources) {
         if (((Boolean)this.destMatcher.evaluate(() -> {
            return (float)PositionedBlockExpression.Argument.at(cache, pos);
         })).booleanValue()) {
            ++matched;
         } else {
            --failChances;
            if (failChances < 0) {
               break;
            }
         }
      }

      return failChances >= 0 ? (float)matched / (float)sources.size() : 0.0F;
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceFactorMatch(this, delegate, navigator);
   }

   public List<Pair<LineSelection, Float>> consider(WorldCache cache, LineSelection considerable, @Nullable IvBlockCollection blockCollection, Set<BlockPos> surface, StructurePlaceContext context) {
      if (blockCollection == null) {
         throw new IllegalArgumentException("Missing a block collection!");
      } else {
         List<Pair<LineSelection, Float>> consideration = new ArrayList();
         int[] size = StructureBoundingBoxes.size(context.boundingBox);
         BlockPos lowerCoord = StructureBoundingBoxes.min(context.boundingBox);
         Set<MutableBlockPos> sources = (Set)BlockAreas.streamMutablePositions(blockCollection.area()).filter((p) -> {
            return ((Boolean)this.sourceMatcher.evaluate(() -> {
               return blockCollection.getBlockState(p);
            })).booleanValue();
         }).map((p) -> {
            return new MutableBlockPos(context.transform.apply(p, size).add(lowerCoord.getX(), 0, lowerCoord.getZ()));
         }).collect(Collectors.toSet());
         Stream var10000 = considerable.streamSections((IntegerRange)null, true);
         var10000.getClass();

         for(IntegerRange range : var10000::iterator) {
            Float curConformity = null;
            int lastY = range.getMax();
            int end = range.getMin();

            for(int y = lastY; y >= end; --y) {
               sources.forEach((p) -> {
                  p.move(EnumFacing.UP, finalY);
               });
               float conformity = this.weight(cache, sources, this.requiredConformity);
               sources.forEach((p) -> {
                  p.move(EnumFacing.DOWN, finalY);
               });
               if (curConformity == null) {
                  curConformity = conformity;
                  lastY = y;
               } else if (!DoubleMath.fuzzyEquals((double)conformity, (double)curConformity.floatValue(), 0.01D)) {
                  consideration.add(Pair.of(LineSelection.fromRange(IntegerRanges.from(lastY, y + 1), true), Float.valueOf(this.weight(curConformity.floatValue()))));
                  curConformity = conformity;
                  lastY = y;
               }
            }

            if (curConformity != null) {
               consideration.add(Pair.of(LineSelection.fromRange(IntegerRanges.from(lastY, end), true), Float.valueOf(this.weight(curConformity.floatValue()))));
            }
         }

         return consideration;
      }
   }

   public static class Serializer implements JsonSerializer<FactorMatch>, JsonDeserializer<FactorMatch> {
      public Serializer() {
      }

      public FactorMatch deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "factorMatch");
         float priority = JsonUtils.getFloat(jsonObject, "priority", 1.0F);
         String sourceExpression = JsonUtils.getString(jsonObject, "sourceExpression", "");
         String destExpression = JsonUtils.getString(jsonObject, "destExpression", "");
         float requiredConformity = JsonUtils.getFloat(jsonObject, "requiredConformity", 0.0F);
         return new FactorMatch(priority, sourceExpression, destExpression, requiredConformity);
      }

      public JsonElement serialize(FactorMatch src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("priority", Float.valueOf(src.priority));
         jsonObject.addProperty("sourceExpression", src.sourceMatcher.getExpression());
         jsonObject.addProperty("destExpression", src.destMatcher.getExpression());
         jsonObject.addProperty("requiredConformity", Float.valueOf(src.requiredConformity));
         return jsonObject;
      }
   }
}
