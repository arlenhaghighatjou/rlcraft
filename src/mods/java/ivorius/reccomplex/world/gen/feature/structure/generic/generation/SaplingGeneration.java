package ivorius.reccomplex.world.gen.feature.structure.generic.generation;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.editstructure.gentypes.TableDataSourceSaplingGeneration;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.EnvironmentExpression;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import ivorius.reccomplex.world.gen.feature.structure.Placer;
import ivorius.reccomplex.world.gen.feature.structure.generic.BlockPattern;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.SelectivePlacer;
import java.lang.reflect.Type;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SaplingGeneration extends GenerationType {
   public Double generationWeight;
   public BlockPos spawnShift;
   public EnvironmentExpression environmentExpression;
   @Nonnull
   public BlockPattern pattern;

   public SaplingGeneration() {
      this((String)null, (Double)null, BlockPos.ORIGIN, "", new BlockPattern());
   }

   public SaplingGeneration(@Nullable String id, Double generationWeight, BlockPos spawnShift, String environmentExpression, BlockPattern pattern) {
      super(id != null ? id : randomID(SaplingGeneration.class));
      this.generationWeight = generationWeight;
      this.spawnShift = spawnShift;
      this.environmentExpression = (EnvironmentExpression)ExpressionCache.of(new EnvironmentExpression(), environmentExpression);
      this.pattern = pattern;
   }

   @Nonnull
   public String id() {
      return this.id;
   }

   public void setID(@Nonnull String id) {
      this.id = id;
   }

   public void setSpawnShift(BlockPos spawnShift) {
      this.spawnShift = spawnShift;
   }

   public String displayString() {
      return IvTranslations.get("reccomplex.generationInfo.sapling.title");
   }

   @Nullable
   public Placer placer() {
      return SelectivePlacer.surfacePlacer(0);
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(MazeVisualizationContext mazeVisualizationContext, TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceSaplingGeneration(this, mazeVisualizationContext, navigator, delegate);
   }

   public boolean generatesIn(Environment environment) {
      return this.environmentExpression.test(environment);
   }

   @Nonnull
   public BlockPattern getPattern() {
      return this.pattern;
   }

   public double getActiveWeight() {
      return this.generationWeight != null ? this.generationWeight.doubleValue() : 1.0D;
   }

   public static class Serializer implements JsonSerializer<SaplingGeneration>, JsonDeserializer<SaplingGeneration> {
      public Serializer() {
      }

      public SaplingGeneration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "vanillaStructureSpawnInfo");
         String id = GenerationType.readID(jsonObject);
         Double spawnWeight = jsonObject.has("generationWeight") ? JsonUtils.getDouble(jsonObject, "generationWeight") : null;
         int spawnX = JsonUtils.getInt(jsonObject, "spawnShiftX", 0);
         int spawnY = JsonUtils.getInt(jsonObject, "spawnShiftY", 0);
         int spawnZ = JsonUtils.getInt(jsonObject, "spawnShiftZ", 0);
         String environmentExpression = JsonUtils.getString(jsonObject, "environmentExpression", "");
         BlockPattern pattern = (BlockPattern)BlockPattern.gson.fromJson(JsonUtils.getJsonObject(jsonObject, "pattern", new JsonObject()), BlockPattern.class);
         return new SaplingGeneration(id, spawnWeight, new BlockPos(spawnX, spawnY, spawnZ), environmentExpression, pattern);
      }

      public JsonElement serialize(SaplingGeneration src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("id", src.id);
         if (src.generationWeight != null) {
            jsonObject.addProperty("generationWeight", src.generationWeight);
         }

         jsonObject.addProperty("spawnShiftX", Integer.valueOf(src.spawnShift.getX()));
         jsonObject.addProperty("spawnShiftY", Integer.valueOf(src.spawnShift.getY()));
         jsonObject.addProperty("spawnShiftZ", Integer.valueOf(src.spawnShift.getZ()));
         jsonObject.addProperty("environmentExpression", src.environmentExpression.getExpression());
         jsonObject.add("pattern", BlockPattern.gson.toJsonTree(src.pattern));
         return jsonObject;
      }
   }
}
