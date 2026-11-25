package ivorius.reccomplex.world.gen.feature.structure.generic.placement.rays;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.blocks.IvMutableBlockPos;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.ivtoolkit.world.WorldCache;
import ivorius.ivtoolkit.world.chunk.gen.StructureBoundingBoxes;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellBoolean;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSupplied;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.utils.expression.PositionedBlockExpression;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.FactorLimit;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.StructurePlaceContext;
import java.lang.reflect.Type;
import java.util.OptionalInt;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RayAverageMatcher extends FactorLimit.Ray {
   public final PositionedBlockExpression destMatcher;
   public boolean up;

   public RayAverageMatcher() {
      this((Float)null, false, "blocks:movement & !is:foliage");
   }

   public RayAverageMatcher(Float weight, boolean up, String destExpression) {
      super(weight);
      this.destMatcher = new PositionedBlockExpression(RecurrentComplex.specialRegistry);
      this.up = up;
      this.destMatcher.setExpression(destExpression);
   }

   public static BlockPos findFirstBlock(MutableBlockPos pos, Predicate<BlockPos> predicate, boolean up, int wHeight) {
      while(pos.getY() >= 0 && pos.getY() < wHeight) {
         if (predicate.test(pos)) {
            return pos;
         }

         IvMutableBlockPos.offset(pos, pos, up ? EnumFacing.UP : EnumFacing.DOWN);
      }

      return null;
   }

   public static int getAverageGroundLevel(boolean up, int y, Set<BlockPos> surface, Predicate<BlockPos> predicate, int wHeight, double samples, EaglercraftRandom random) {
      TIntList list = new TIntArrayList(surface.size());
      MutableBlockPos pos = new MutableBlockPos();

      for(BlockPos surfacePos : surface) {
         if (samples >= 1.0D || random.nextDouble() < samples) {
            pos.setPos(surfacePos.getX(), surfacePos.getY() + y, surfacePos.getZ());
            BlockPos found = findFirstBlock(pos, predicate, up, wHeight);
            if (found != null) {
               list.add(found.getY() - surfacePos.getY());
            }
         }
      }

      if (list.isEmpty()) {
         return -1;
      } else {
         return robustAverage(list.toArray());
      }
   }

   protected static int robustAverage(int... values) {
      int average = 0;

      for(int val : values) {
         average += val;
      }

      average = average / values.length;
      int averageDist = 0;

      for(int val : values) {
         averageDist += dist(val, average);
      }

      averageDist = averageDist / values.length;
      int newAverage = 0;
      int ignored = 0;

      for(int val : values) {
         if (dist(val, average) <= averageDist * 2) {
            newAverage += val;
         } else {
            ++ignored;
         }
      }

      return newAverage / (values.length - ignored);
   }

   protected static int dist(int val1, int val2) {
      return val1 > val2 ? val1 - val2 : val2 - val1;
   }

   public static Set<BlockPos> shifted(StructurePlaceContext context, IvBlockCollection collection, Set<BlockPos> surface) {
      int[] size = collection.area().areaSize();
      BlockPos lower = StructureBoundingBoxes.min(context.boundingBox);
      return (Set)surface.stream().map((p) -> {
         return context.transform.apply(p, size).add(lower);
      }).collect(Collectors.toSet());
   }

   public OptionalInt cast(WorldCache cache, StructurePlaceContext context, IvBlockCollection collection, Set<BlockPos> surface, int y) {
      int floorBlocks = surface.size();
      double samples = floorBlocks < 256 ? 1.0D : Math.pow((double)(256.0F / (float)floorBlocks), 0.699999988079071D);
      Set<BlockPos> shiftedSurface = shifted(context, collection, surface);
      int averageGroundLevel = getAverageGroundLevel(this.up, y, shiftedSurface, (blockPos) -> {
         return ((Boolean)this.destMatcher.evaluate(() -> {
            return PositionedBlockExpression.Argument.at(cache, blockPos);
         })).booleanValue();
      }, cache.world.getHeight(), samples, context.random);
      return averageGroundLevel >= 0 ? OptionalInt.of(averageGroundLevel) : OptionalInt.empty();
   }

   public String displayString() {
      return String.format("%s %s", directionArrow(Boolean.valueOf(this.up)), IvTranslations.get("reccomplex.placer.factors.limit.rays.average"));
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceSegmented(new TableDataSource[]{this.rayTableDataSource(navigator, delegate), TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.placer.factors.limit.rays.matcher.condition"), this.destMatcher, (Object)null), new TableDataSourceSupplied(new Supplier[]{() -> {
         TableCellBoolean cell = new TableCellBoolean((String)null, this.up, IvTranslations.get("reccomplex.direction.up"), IvTranslations.get("reccomplex.direction.down"));
         cell.addListener((v) -> {
            this.up = v.booleanValue();
         });
         return new TitledCell(IvTranslations.get("reccomplex.placer.factors.limit.rays.matcher.direction"), cell);
      }})});
   }

   public static class Serializer implements JsonSerializer<RayAverageMatcher>, JsonDeserializer<RayAverageMatcher> {
      public Serializer() {
      }

      public RayAverageMatcher deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "rayAverageMatcher");
         Float weight = JsonUtils.has(jsonObject, "weight") ? JsonUtils.getFloat(jsonObject, "weight") : null;
         boolean up = JsonUtils.getBoolean(jsonObject, "up", true);
         String destExpression = JsonUtils.getString(jsonObject, "destExpression", "");
         return new RayAverageMatcher(weight, up, destExpression);
      }

      public JsonElement serialize(RayAverageMatcher src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         if (src.weight != null) {
            jsonObject.addProperty("weight", src.weight);
         }

         jsonObject.addProperty("up", Boolean.valueOf(src.up));
         jsonObject.addProperty("destExpression", src.destMatcher.getExpression());
         return jsonObject;
      }
   }
}
