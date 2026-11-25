package ivorius.reccomplex.world.gen.feature.structure.generic.generation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import ivorius.ivtoolkit.blocks.BlockSurfacePos;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.ivtoolkit.world.chunk.Chunks;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.editstructure.gentypes.TableDataSourceStaticGeneration;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.DimensionExpression;
import ivorius.reccomplex.world.gen.feature.structure.Placer;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.GenericPlacer;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.SelectivePlacer;
import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

public class StaticGeneration extends GenerationType {
   private static Gson gson = createGson();
   public SelectivePlacer placer;
   public DimensionExpression dimensionExpression;
   public boolean relativeToSpawn;
   public BlockSurfacePos position;
   @Nullable
   public StaticGeneration.Pattern pattern;

   public StaticGeneration() {
      this((String)null, (DimensionExpression)ExpressionCache.of(new DimensionExpression(), "0"), true, BlockSurfacePos.ORIGIN, (StaticGeneration.Pattern)null);
   }

   public StaticGeneration(@Nullable String id, DimensionExpression dimensionExpression, boolean relativeToSpawn, BlockSurfacePos position, StaticGeneration.Pattern pattern) {
      super(id != null ? id : randomID(StaticGeneration.class));
      this.dimensionExpression = dimensionExpression;
      this.relativeToSpawn = relativeToSpawn;
      this.position = position;
      this.pattern = pattern;
      this.placer = new SelectivePlacer();
   }

   public static Gson createGson() {
      GsonBuilder builder = new GsonBuilder();
      builder.registerTypeAdapter(StaticGeneration.class, new StaticGeneration.Serializer());
      builder.registerTypeAdapter(GenericPlacer.class, new GenericPlacer.Serializer());
      return builder.create();
   }

   public static Gson getGson() {
      return gson;
   }

   public static Stream<Triple<Structure<?>, StaticGeneration, BlockSurfacePos>> structuresAt(StructureRegistry registry, ChunkPos chunkPos, World world, BlockPos spawnPos) {
      Predicate<Pair<Structure<?>, StaticGeneration>> predicate = (input) -> {
         StaticGeneration info = (StaticGeneration)input.getRight();
         return info.dimensionExpression.test(world.provider) && (info.pattern != null || Chunks.contains(chunkPos, info.getPos(spawnPos)));
      };
      Stream<Pair<Structure<?>, StaticGeneration>> statics = registry.getGenerationTypes(StaticGeneration.class).stream().filter(predicate);
      return statics.flatMap((pair) -> {
         StaticGeneration info = (StaticGeneration)pair.getRight();
         return info.hasPattern() ? Chunks.repeatIntersections(chunkPos, info.getPos(spawnPos), info.pattern.repeatX, info.pattern.repeatZ).map((pos) -> {
            return Triple.of(pair.getLeft(), info, pos);
         }) : Stream.of(Triple.of(pair.getLeft(), info, info.getPos(spawnPos)));
      });
   }

   public BlockSurfacePos getPosition() {
      return this.position;
   }

   public void setPosition(BlockSurfacePos position) {
      this.position = position;
   }

   @Nonnull
   public String id() {
      return this.id;
   }

   public void setID(@Nonnull String id) {
      this.id = id;
   }

   public String displayString() {
      if (this.hasPattern()) {
         return IvTranslations.format("reccomplex.generationInfo.static.summary.pattern", new Object[]{String.valueOf(this.pattern.repeatX), String.valueOf(this.pattern.repeatZ)});
      } else {
         return this.relativeToSpawn ? IvTranslations.format("reccomplex.generationInfo.static.summary.spawn", new Object[]{String.valueOf(this.position.x), String.valueOf(this.position.z)}) : IvTranslations.format("reccomplex.generationInfo.static.summary.nospawn", new Object[]{String.valueOf(this.position.x), String.valueOf(this.position.z)});
      }
   }

   @Nullable
   public Placer placer() {
      return this.placer;
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(MazeVisualizationContext mazeVisualizationContext, TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceStaticGeneration(navigator, delegate, this);
   }

   public BlockSurfacePos getPos(BlockPos spawnPos) {
      return new BlockSurfacePos(this.relativeToSpawn ? spawnPos.getX() + this.position.x : this.position.x, this.relativeToSpawn ? spawnPos.getZ() + this.position.z : this.position.z);
   }

   public boolean hasPattern() {
      return this.pattern != null;
   }

   public static class Pattern {
      @SerializedName("repeatX")
      public int repeatX = 16;
      @SerializedName("repeatZ")
      public int repeatZ = 16;
      @SerializedName("randomShiftX")
      public int randomShiftX = 0;
      @SerializedName("randomShiftZ")
      public int randomShiftZ = 0;

      public Pattern() {
      }
   }

   public static class Serializer implements JsonSerializer<StaticGeneration>, JsonDeserializer<StaticGeneration> {
      public Serializer() {
      }

      public StaticGeneration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "vanillaStructureSpawnInfo");
         String id = GenerationType.readID(jsonObject);
         String dimension = JsonUtils.getString(jsonObject, "dimensions", "");
         boolean relativeToSpawn = JsonUtils.getBoolean(jsonObject, "relativeToSpawn", false);
         int positionX = JsonUtils.getInt(jsonObject, "positionX", 0);
         int positionZ = JsonUtils.getInt(jsonObject, "positionZ", 0);
         StaticGeneration.Pattern pattern = jsonObject.has("pattern") ? (StaticGeneration.Pattern)StaticGeneration.gson.fromJson(jsonObject.get("pattern"), StaticGeneration.Pattern.class) : null;
         StaticGeneration staticGenInfo = new StaticGeneration(id, (DimensionExpression)ExpressionCache.of(new DimensionExpression(), dimension), relativeToSpawn, new BlockSurfacePos(positionX, positionZ), pattern);
         staticGenInfo.placer = (SelectivePlacer)SelectivePlacer.gson.fromJson(json, SelectivePlacer.class);
         return staticGenInfo;
      }

      public JsonElement serialize(StaticGeneration src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = (JsonObject)SelectivePlacer.gson.toJsonTree(src.placer);
         jsonObject.addProperty("id", src.id);
         jsonObject.addProperty("dimensions", src.dimensionExpression.getExpression());
         jsonObject.addProperty("relativeToSpawn", Boolean.valueOf(src.relativeToSpawn));
         jsonObject.addProperty("positionX", Integer.valueOf(src.position.x));
         jsonObject.addProperty("positionZ", Integer.valueOf(src.position.z));
         if (src.pattern != null) {
            jsonObject.add("pattern", StaticGeneration.gson.toJsonTree(src.pattern));
         }

         return jsonObject;
      }
   }
}
