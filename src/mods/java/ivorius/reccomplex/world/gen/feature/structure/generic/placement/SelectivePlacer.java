package ivorius.reccomplex.world.gen.feature.structure.generic.placement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import ivorius.ivtoolkit.blocks.BlockAreas;
import ivorius.ivtoolkit.blocks.BlockSurfacePos;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.BlockExpression;
import ivorius.reccomplex.utils.presets.PresettedObject;
import ivorius.reccomplex.utils.presets.PresettedObjects;
import ivorius.reccomplex.world.gen.feature.structure.Placer;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.rays.RayDynamicPosition;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.rays.RayMatcher;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.rays.RayMove;
import ivorius.reccomplex.world.gen.feature.structure.generic.presets.GenericPlacerPresets;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class SelectivePlacer implements Placer {
   public static Gson gson = createGson();
   public final PresettedObject<GenericPlacer> placer = new PresettedObject<GenericPlacer>(GenericPlacerPresets.instance(), (String)null);
   public int baseline;
   public BlockExpression sourceMatcher;

   public SelectivePlacer() {
      this.placer.setPreset("surface");
      this.baseline = 0;
      this.sourceMatcher = (BlockExpression)ExpressionCache.of(new BlockExpression(RecurrentComplex.specialRegistry), "!(air | reccomplex:generic_space)");
   }

   public SelectivePlacer(GenericPlacer placer, int baseline) {
      this.placer.setContents(placer);
      this.baseline = baseline;
      this.sourceMatcher = (BlockExpression)ExpressionCache.of(new BlockExpression(RecurrentComplex.specialRegistry), "");
   }

   public static Gson createGson() {
      GsonBuilder builder = new GsonBuilder();
      builder.registerTypeAdapter(SelectivePlacer.class, new SelectivePlacer.Serializer());
      builder.registerTypeAdapter(GenericPlacer.class, new GenericPlacer.Serializer());
      return builder.create();
   }

   @Nonnull
   public static SelectivePlacer surfacePlacer(int baseline) {
      SelectivePlacer surface = new SelectivePlacer();
      surface.placer.setPreset("surface");
      surface.baseline = baseline;
      return surface;
   }

   @Nonnull
   public static SelectivePlacer underwaterPlacer(int baseline) {
      SelectivePlacer surface = new SelectivePlacer();
      surface.placer.setPreset("underwater");
      surface.baseline = baseline;
      return surface;
   }

   public int place(StructurePlaceContext context, IvBlockCollection blockCollection) {
      Set<BlockPos> surface = (Set)BlockAreas.side(blockCollection.area(), EnumFacing.DOWN).stream().map((p) -> {
         return BlockSurfacePos.from(p).blockPos(this.baseline);
      }).filter((p) -> {
         return ((Boolean)this.sourceMatcher.evaluate(blockCollection.getBlockState(p))).booleanValue();
      }).collect(Collectors.toSet());
      return (this.placer.getContents()).place(context, blockCollection, surface);
   }

   public static class Serializer implements JsonSerializer<SelectivePlacer>, JsonDeserializer<SelectivePlacer> {
      public Serializer() {
      }

      @Nonnull
      public static SelectivePlacer readLegacyPlacer(JsonDeserializationContext context, JsonObject jsonObject) {
         GenericPlacer.Serializer.SelectionMode selectionMode = jsonObject.has("selectionMode") ? (GenericPlacer.Serializer.SelectionMode)context.deserialize(jsonObject.get("selectionMode"), GenericPlacer.Serializer.SelectionMode.class) : GenericPlacer.Serializer.SelectionMode.SURFACE;
         int minYShift = JsonUtils.getInt(jsonObject, "minY", 0);
         int maxYShift = JsonUtils.getInt(jsonObject, "maxY", 0);
         RayDynamicPosition.Type dynType = null;
         SelectivePlacer placer;
         switch(selectionMode) {
         case SURFACE:
         default:
            placer = SelectivePlacer.surfacePlacer(-(minYShift + maxYShift) / 2);
            break;
         case UNDERWATER:
            placer = SelectivePlacer.underwaterPlacer(-(minYShift + maxYShift) / 2);
            break;
         case LOWEST_EDGE:
            placer = new SelectivePlacer(new GenericPlacer(Collections.singletonList(new FactorLimit(1.0F, Arrays.asList(new RayDynamicPosition((Float)null, RayDynamicPosition.Type.WORLD_HEIGHT), new RayMatcher((Float)null, false, 0.9F, "!(is:air | is:foliage | is:replaceable) & !is:liquid"), new RayMove((Float)null, minYShift), new RayMove(1.0F, maxYShift - minYShift))))), 0);
            break;
         case BEDROCK:
            dynType = RayDynamicPosition.Type.BEDROCK;
         case SEALEVEL:
            if (dynType == null) {
               dynType = RayDynamicPosition.Type.SEALEVEL;
            }
         case TOP:
            if (dynType == null) {
               dynType = RayDynamicPosition.Type.WORLD_HEIGHT;
            }

            placer = new SelectivePlacer(new GenericPlacer(Collections.singletonList(new FactorLimit(1.0F, Arrays.asList(new RayDynamicPosition((Float)null, dynType), new RayMove((Float)null, minYShift), new RayMove(1.0F, maxYShift - minYShift))))), 0);
         }

         return placer;
      }

      public SelectivePlacer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         SelectivePlacer selectivePlacer = new SelectivePlacer();
         JsonObject jsonObject = json.getAsJsonObject();
         if (!PresettedObjects.read(jsonObject, SelectivePlacer.gson, selectivePlacer.placer, "placerPreset", "placer", (new TypeToken<GenericPlacer>() {
         }).getType()) && jsonObject.has("generationY")) {
            return readLegacyPlacer(context, JsonUtils.getJsonObject(jsonObject, "generationY", new JsonObject()));
         } else {
            selectivePlacer.baseline = JsonUtils.getInt(jsonObject, "baseline", 0);
            selectivePlacer.sourceMatcher.setExpression(JsonUtils.getString(jsonObject, "sourceExpression", ""));
            return selectivePlacer;
         }
      }

      public JsonElement serialize(SelectivePlacer src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         PresettedObjects.write(jsonObject, SelectivePlacer.gson, src.placer, "placerPreset", "placer");
         jsonObject.addProperty("baseline", Integer.valueOf(src.baseline));
         jsonObject.addProperty("sourceExpression", src.sourceMatcher.getExpression());
         return jsonObject;
      }
   }
}
