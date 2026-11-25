package ivorius.reccomplex.world.gen.feature.structure.generic.placement.rays;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.ivtoolkit.util.IvStreams;
import ivorius.ivtoolkit.world.WorldCache;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.TableDataSourceExpression;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellBoolean;
import ivorius.reccomplex.gui.table.cell.TableCellFloatSlider;
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
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RayMatcher extends FactorLimit.Ray {
   public final PositionedBlockExpression destMatcher;
   public boolean up;
   public float requiredRatio;

   public RayMatcher() {
      this((Float)null, false, 0.5F, "");
   }

   public RayMatcher(Float weight, boolean up, float requiredRatio, String destExpression) {
      super(weight);
      this.destMatcher = new PositionedBlockExpression(RecurrentComplex.specialRegistry);
      this.up = up;
      this.requiredRatio = requiredRatio;
      this.destMatcher.setExpression(destExpression);
   }

   protected boolean matches(WorldCache cache, Set<BlockPos> surface, int y, float needed) {
      int[] chances = new int[]{surface.size()};
      int[] need = new int[]{(int)((float)chances[0] * needed)};
      chances[0] -= need[0];
      MutableBlockPos pos = new MutableBlockPos();
      IvStreams.visit(surface.stream(), (surfacePos) -> {
         pos.setPos(surfacePos.getX(), surfacePos.getY() + y, surfacePos.getZ());
         if (((Boolean)this.destMatcher.evaluate(() -> {
            return (boolean)PositionedBlockExpression.Argument.at(cache, pos);
         })).booleanValue()) {
            return --need[0] > 0;
         } else {
            return --chances[0] > 0;
         }
      });
      return need[0] <= 0;
   }

   public OptionalInt cast(WorldCache cache, StructurePlaceContext context, IvBlockCollection collection, Set<BlockPos> surface, int y) {
      Set<BlockPos> shiftedSurface = RayAverageMatcher.shifted(context, collection, surface);

      for(int height = cache.world.getHeight(); y >= 0 && y < height; y += this.up ? 1 : -1) {
         if (this.matches(cache, shiftedSurface, y, this.requiredRatio)) {
            return OptionalInt.of(y);
         }
      }

      return OptionalInt.empty();
   }

   public String displayString() {
      return String.format("%s %s", directionArrow(Boolean.valueOf(this.up)), IvTranslations.get("reccomplex.placer.factors.limit.rays.matcher"));
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceSegmented(new TableDataSource[]{this.rayTableDataSource(navigator, delegate), TableDataSourceExpression.constructDefault(IvTranslations.get("reccomplex.placer.factors.limit.rays.matcher.condition"), this.destMatcher, (Object)null), new TableDataSourceSupplied(new Supplier[]{() -> {
         TableCellBoolean cell = new TableCellBoolean((String)null, this.up, IvTranslations.get("reccomplex.direction.up"), IvTranslations.get("reccomplex.direction.down"));
         cell.addListener((v) -> {
            this.up = v.booleanValue();
         });
         return new TitledCell(IvTranslations.get("reccomplex.placer.factors.limit.rays.matcher.direction"), cell);
      }, () -> {
         TableCellFloatSlider cell = new TableCellFloatSlider((String)null, this.requiredRatio, 0.0F, 1.0F);
         cell.addListener((v) -> {
            this.requiredRatio = v.floatValue();
         });
         return (new TitledCell(IvTranslations.get("reccomplex.placer.factors.limit.rays.matcher.ratio"), cell)).withTitleTooltip(IvTranslations.getLines("reccomplex.placer.factors.limit.rays.matcher.ratio.tooltip"));
      }})});
   }

   public static class Serializer implements JsonSerializer<RayMatcher>, JsonDeserializer<RayMatcher> {
      public Serializer() {
      }

      public RayMatcher deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "rayMatcher");
         Float weight = JsonUtils.has(jsonObject, "weight") ? JsonUtils.getFloat(jsonObject, "weight") : null;
         boolean up = JsonUtils.getBoolean(jsonObject, "up", true);
         float allowedDivergence = JsonUtils.getFloat(jsonObject, "requiredRatio", 0.0F);
         String destExpression = JsonUtils.getString(jsonObject, "destExpression", "");
         return new RayMatcher(weight, up, allowedDivergence, destExpression);
      }

      public JsonElement serialize(RayMatcher src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         if (src.weight != null) {
            jsonObject.addProperty("weight", src.weight);
         }

         jsonObject.addProperty("up", Boolean.valueOf(src.up));
         jsonObject.addProperty("requiredRatio", Float.valueOf(src.requiredRatio));
         jsonObject.addProperty("destExpression", src.destMatcher.getExpression());
         return jsonObject;
      }
   }
}
