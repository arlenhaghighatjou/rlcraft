package ivorius.reccomplex.world.gen.feature.structure.generic.generation;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.blocks.Directions;
import ivorius.ivtoolkit.random.WeightedSelector.Item;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.editstructure.gentypes.TableDataSourceStructureListGeneration;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.world.gen.feature.structure.Placer;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import java.lang.reflect.Type;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

public class ListGeneration extends GenerationType implements Item {
   public String listID;
   public Double weight;
   public BlockPos shift;
   public EnumFacing front;

   public ListGeneration() {
      this((String)null, "", (Double)null, BlockPos.ORIGIN, EnumFacing.NORTH);
   }

   public ListGeneration(@Nullable String id, String listID, Double weight, BlockPos shift, EnumFacing front) {
      super(id != null ? id : randomID(ListGeneration.class));
      this.listID = listID;
      this.weight = weight;
      this.shift = shift;
      this.front = front;
   }

   public static Stream<Pair<Structure<?>, ListGeneration>> structures(StructureRegistry registry, String listID, @Nullable EnumFacing front) {
      Predicate<Pair<Structure<?>, ListGeneration>> predicate = (input) -> {
         return listID.equals(((ListGeneration)input.getRight()).listID) && (front == null || ((Structure)input.getLeft()).isRotatable() || ((ListGeneration)input.getRight()).front == front);
      };
      return registry.getGenerationTypes(ListGeneration.class).stream().filter(predicate);
   }

   @Nonnull
   public String id() {
      return this.id;
   }

   public void setID(@Nonnull String id) {
      this.id = id;
   }

   public BlockPos getShift() {
      return this.shift;
   }

   public void setShift(BlockPos shift) {
      this.shift = shift;
   }

   public String displayString() {
      return IvTranslations.format("reccomplex.generationInfo.structureList.title", new Object[]{this.listID});
   }

   @Nullable
   public Placer placer() {
      return null;
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(MazeVisualizationContext mazeVisualizationContext, TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceStructureListGeneration(navigator, delegate, this);
   }

   public double getWeight() {
      return this.weight != null ? this.weight.doubleValue() : 1.0D;
   }

   public static class Serializer implements JsonSerializer<ListGeneration>, JsonDeserializer<ListGeneration> {
      public Serializer() {
      }

      public ListGeneration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "vanillaStructureSpawnInfo");
         String id = GenerationType.readID(jsonObject);
         String listID = JsonUtils.getString(jsonObject, "listID", "");
         Double weight = jsonObject.has("weight") ? JsonUtils.getDouble(jsonObject, "weight") : null;
         int positionX = JsonUtils.getInt(jsonObject, "positionX", 0);
         int positionY = JsonUtils.getInt(jsonObject, "positionY", 0);
         int positionZ = JsonUtils.getInt(jsonObject, "positionZ", 0);
         EnumFacing front = Directions.deserialize(JsonUtils.getString(jsonObject, "front", "NORTH"));
         return new ListGeneration(id, listID, weight, new BlockPos(positionX, positionY, positionZ), front);
      }

      public JsonElement serialize(ListGeneration src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("id", src.id);
         jsonObject.addProperty("listID", src.listID);
         if (src.weight != null) {
            jsonObject.addProperty("weight", src.weight);
         }

         jsonObject.addProperty("positionX", Integer.valueOf(src.shift.getX()));
         jsonObject.addProperty("positionY", Integer.valueOf(src.shift.getY()));
         jsonObject.addProperty("positionZ", Integer.valueOf(src.shift.getZ()));
         jsonObject.addProperty("front", Directions.serialize(src.front));
         return jsonObject;
      }
   }
}
