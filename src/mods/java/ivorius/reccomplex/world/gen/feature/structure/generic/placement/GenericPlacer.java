package ivorius.reccomplex.world.gen.feature.structure.generic.placement;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.gui.IntegerRange;
import ivorius.ivtoolkit.random.WeightedSelector;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.ivtoolkit.util.LineSelection;
import ivorius.ivtoolkit.util.LineSelections;
import ivorius.ivtoolkit.world.WorldCache;
import ivorius.ivtoolkit.world.chunk.gen.StructureBoundingBoxes;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

public class GenericPlacer {
   public final List<GenericPlacer.Factor> factors = new ArrayList();

   public GenericPlacer() {
   }

   public GenericPlacer(List<GenericPlacer.Factor> factors) {
      this.factors.addAll(factors);
   }

   public int place(StructurePlaceContext context, @Nullable IvBlockCollection blockCollection, Set<BlockPos> surface) {
      if (this.factors.isEmpty()) {
         return -1;
      } else {
         WorldServer world = context.environment.world;
         WorldCache cache = new WorldCache(world, StructureBoundingBoxes.wholeHeightBoundingBox(world, context.boundingBox));
         LineSelection considerable = LineSelection.fromRange(new IntegerRange(0, world.getHeight() - context.boundingBox.getYSize()), true);
         List<Pair<LineSelection, Float>> considerations = new ArrayList();
         this.factors.forEach((factor) -> {
            List<Pair<LineSelection, Float>> consideration = factor.consider(cache, considerable, blockCollection, surface, context);
            consideration.stream().filter((p) -> {
               return ((Float)p.getRight()).floatValue() <= 0.0F;
            }).forEach((p) -> {
               considerable.set((LineSelection)p.getLeft(), true, false);
            });
            consideration = (List)consideration.stream().filter((p) -> {
               return ((Float)p.getRight()).floatValue() > 0.0F;
            }).collect(Collectors.toList());
            considerable.set(LineSelections.combine(consideration.stream().map(Pair::getLeft), true), false, false);
            considerations.addAll(consideration);
         });
         Set<Pair<Integer, Double>> applicable = (Set)considerable.streamElements((IntegerRange)null, true).mapToObj((y) -> {
            return (int)Pair.of(Integer.valueOf(y), Double.valueOf(considerations.stream().mapToDouble((pair) -> {
               return (int)(((LineSelection)pair.getLeft()).isSectionAdditive(((LineSelection)pair.getLeft()).sectionForIndex(y)) ? (double)((Float)pair.getRight()).floatValue() : 1.0D);
            }).reduce(1.0D, (left, right) -> {
               return (int)(left * right);
            })));
         }).filter((p) -> {
            return ((Double)p.getRight()).doubleValue() > 0.0D;
         }).collect(Collectors.toSet());
         return applicable.size() > 0 ? ((Integer)((Pair)WeightedSelector.select(context.random, applicable, Pair::getRight)).getLeft()).intValue() : -1;
      }
   }

   public abstract static class Factor {
      public float priority;

      public Factor(float priority) {
         this.priority = priority;
      }

      public float weight(float weight) {
         return (float)Math.pow((double)weight, (double)this.priority);
      }

      public String displayString() {
         return IvTranslations.get("reccomplex.placer.factors." + FactorRegistry.INSTANCE.getTypeRegistry().iDForType(this.getClass()));
      }

      @SideOnly(Side.CLIENT)
      public abstract TableDataSource tableDataSource(TableNavigator var1, TableDelegate var2);

      public abstract List<Pair<LineSelection, Float>> consider(WorldCache var1, LineSelection var2, @Nullable IvBlockCollection var3, Set<BlockPos> var4, StructurePlaceContext var5);
   }

   public static class Serializer implements JsonSerializer<GenericPlacer>, JsonDeserializer<GenericPlacer> {
      public Serializer() {
      }

      public GenericPlacer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "placer");
         List<GenericPlacer.Factor> factors = (List)FactorRegistry.INSTANCE.gson.fromJson(jsonObject.get("factors"), (new TypeToken<List<GenericPlacer.Factor>>() {
         }).getType());
         if (factors == null) {
            factors = Collections.emptyList();
         }

         return new GenericPlacer(factors);
      }

      public JsonElement serialize(GenericPlacer src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.add("factors", FactorRegistry.INSTANCE.gson.toJsonTree(src.factors));
         return jsonObject;
      }

      public static enum SelectionMode {
         @SerializedName("bedrock")
         BEDROCK,
         @SerializedName("surface")
         SURFACE,
         @SerializedName("sealevel")
         SEALEVEL,
         @SerializedName("underwater")
         UNDERWATER,
         @SerializedName("top")
         TOP,
         @SerializedName("lowestedge")
         LOWEST_EDGE;

         private SelectionMode() {
         }
      }
   }
}
