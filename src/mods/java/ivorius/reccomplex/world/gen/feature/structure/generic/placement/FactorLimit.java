package ivorius.reccomplex.world.gen.feature.structure.generic.placement;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.ivtoolkit.util.LineSelection;
import ivorius.ivtoolkit.world.WorldCache;
import ivorius.reccomplex.gui.editstructure.placer.TableDataSourceFactorLimit;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellFloatNullable;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSupplied;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.json.SerializableStringTypeRegistry;
import ivorius.reccomplex.utils.IntegerRanges;
import ivorius.reccomplex.utils.scale.Scales;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

public class FactorLimit extends GenericPlacer.Factor {
   private static SerializableStringTypeRegistry<FactorLimit.Ray> rayRegistry = new SerializableStringTypeRegistry<FactorLimit.Ray>("ray", "type", FactorLimit.Ray.class);
   public static final Gson gson = createGson();
   public final List<FactorLimit.Ray> rays;

   public FactorLimit() {
      this(1.0F, Collections.emptyList());
   }

   public FactorLimit(float priority, Collection<FactorLimit.Ray> rays) {
      super(priority);
      this.rays = new ArrayList();
      this.rays.addAll(rays);
   }

   private static Gson createGson() {
      GsonBuilder builder = new GsonBuilder();
      rayRegistry.constructGson(builder);
      return builder.create();
   }

   public static SerializableStringTypeRegistry<FactorLimit.Ray> getRayRegistry() {
      return rayRegistry;
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceFactorLimit(this, delegate, navigator);
   }

   public List<Pair<LineSelection, Float>> consider(WorldCache cache, LineSelection considerable, @Nullable IvBlockCollection blockCollection, Set<BlockPos> surface, StructurePlaceContext context) {
      List<Pair<LineSelection, Float>> consideration = new ArrayList();
      int height = cache.world.getHeight();
      int pos = height - 1;

      for(FactorLimit.Ray ray : this.rays) {
         int before = pos;
         OptionalInt cast = ray.cast(cache, context, blockCollection, surface, pos);
         if (!cast.isPresent()) {
            break;
         }

         pos = cast.getAsInt();
         LineSelection selection = LineSelection.fromRange(IntegerRanges.from(pos, before), true);
         selection.set(considerable, false, false);
         if (ray.weight != null && !selection.isUniform()) {
            consideration.add(Pair.of(selection, Float.valueOf(this.weight(ray.weight.floatValue()))));
         }
      }

      return consideration;
   }

   public abstract static class Ray {
      @Nullable
      public Float weight;

      public Ray(Float weight) {
         this.weight = weight;
      }

      @Nonnull
      public static String directionArrow(Boolean up) {
         if (up == null) {
            return "\u25b6";
         } else {
            return up.booleanValue() ? "\u25b2" : "\u25bc";
         }
      }

      public abstract OptionalInt cast(WorldCache var1, StructurePlaceContext var2, IvBlockCollection var3, Set<BlockPos> var4, int var5);

      @SideOnly(Side.CLIENT)
      public TableDataSource rayTableDataSource(TableNavigator navigator, TableDelegate delegate) {
         return new TableDataSourceSupplied(new Supplier[]{() -> {
            TableCellFloatNullable cell = new TableCellFloatNullable("value", this.weight, 0.0F, 0.0F, 1000.0F, "I", "A");
            cell.setScale(Scales.pow(5.0F));
            cell.addListener((v) -> {
               this.weight = v;
            });
            return (new TitledCell(IvTranslations.get("reccomplex.placer.factors.limit.ray.weight"), cell)).withTitleTooltip(IvTranslations.formatLines("reccomplex.placer.factors.limit.ray.weight.tooltip", new Object[0]));
         }}) {
            @Nonnull
            public String title() {
               return Ray.this.displayString();
            }
         };
      }

      @SideOnly(Side.CLIENT)
      public abstract TableDataSource tableDataSource(TableNavigator var1, TableDelegate var2);

      public String displayString() {
         return IvTranslations.get("reccomplex.placer.factors.limit.rays." + FactorLimit.rayRegistry.iDForType(this.getClass()));
      }
   }

   public static class Serializer implements JsonSerializer<FactorLimit>, JsonDeserializer<FactorLimit> {
      public Serializer() {
      }

      public FactorLimit deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "factorLimit");
         float priority = JsonUtils.getFloat(jsonObject, "priority", 1.0F);
         List<FactorLimit.Ray> rays = (List)FactorLimit.gson.fromJson(jsonObject.get("rays"), (new TypeToken<List<FactorLimit.Ray>>() {
         }).getType());
         return new FactorLimit(priority, rays);
      }

      public JsonElement serialize(FactorLimit src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("priority", Float.valueOf(src.priority));
         jsonObject.add("rays", FactorLimit.gson.toJsonTree(src.rays));
         return jsonObject;
      }
   }
}
