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
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.random.WeightedSelector.Item;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.GuiValidityStateIndicator;
import ivorius.reccomplex.gui.editstructure.gentypes.TableDataSourceMazeGeneration;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.world.gen.feature.structure.Placer;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.Structures;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazeComponent;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePath;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazePathConnection;
import ivorius.reccomplex.world.gen.feature.structure.generic.maze.SavedMazeReachability;
import java.lang.reflect.Type;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

public class MazeGeneration extends GenerationType implements Item {
   private static Gson gson = createGson();
   public String mazeID;
   public Double weight;
   public SavedMazeComponent mazeComponent;

   public MazeGeneration() {
      this((String)null, (Double)null, "", new SavedMazeComponent("Wall"));
      this.mazeComponent.rooms.addAll(Selection.zeroSelection(3));
   }

   public MazeGeneration(@Nullable String id, Double weight, String mazeID, SavedMazeComponent mazeComponent) {
      super(id != null ? id : randomID(MazeGeneration.class));
      this.weight = weight;
      this.mazeID = mazeID;
      this.mazeComponent = mazeComponent;
   }

   public static Gson createGson() {
      GsonBuilder builder = new GsonBuilder();
      builder.registerTypeAdapter(MazeGeneration.class, new MazeGeneration.Serializer());
      builder.registerTypeAdapter(SavedMazeComponent.class, new SavedMazeComponent.Serializer());
      builder.registerTypeAdapter(MazeRoom.class, new SavedMazeComponent.RoomSerializer());
      builder.registerTypeAdapter(SavedMazeReachability.class, new SavedMazeReachability.Serializer());
      builder.registerTypeAdapter(SavedMazePath.class, new SavedMazePath.Serializer());
      builder.registerTypeAdapter(SavedMazePathConnection.class, new SavedMazePathConnection.Serializer());
      builder.registerTypeAdapter(SavedMazePathConnection.ConditionalConnector.class, new SavedMazePathConnection.ConditionalConnector.Serializer());
      return builder.create();
   }

   public static Gson getGson() {
      return gson;
   }

   public static boolean exists(String mazeID) {
      return structures(StructureRegistry.INSTANCE, mazeID).findAny().isPresent();
   }

   @SideOnly(Side.CLIENT)
   public static GuiValidityStateIndicator.State idValidity(String mazeID) {
      return !Structures.isSimpleID(mazeID) ? GuiValidityStateIndicator.State.INVALID : (exists(mazeID) ? GuiValidityStateIndicator.State.VALID : GuiValidityStateIndicator.State.SEMI_VALID);
   }

   public static Stream<Pair<Structure<?>, MazeGeneration>> structures(StructureRegistry registry, String mazeID) {
      Predicate<Pair<Structure<?>, MazeGeneration>> predicate = (input) -> {
         MazeGeneration info = (MazeGeneration)input.getRight();
         return mazeID.equals(info.mazeID) && info.mazeComponent.isValid();
      };
      return registry.getGenerationTypes(MazeGeneration.class).stream().filter(predicate);
   }

   public String getMazeID() {
      return this.mazeID;
   }

   public void setMazeID(String mazeID) {
      this.mazeID = mazeID;
   }

   public double getWeight() {
      return this.weight != null ? this.weight.doubleValue() : 1.0D;
   }

   public boolean hasDefaultWeight() {
      return this.weight == null;
   }

   public String displayString() {
      return IvTranslations.format("reccomplex.generationInfo.mazeComponent.title", new Object[]{this.mazeID});
   }

   @Nullable
   public Placer placer() {
      return null;
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(MazeVisualizationContext mazeVisualizationContext, TableNavigator navigator, TableDelegate delegate) {
      return new TableDataSourceMazeGeneration(navigator, mazeVisualizationContext, delegate, this);
   }

   public static class Serializer implements JsonSerializer<MazeGeneration>, JsonDeserializer<MazeGeneration> {
      public Serializer() {
      }

      public MazeGeneration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "MazeGenerationInfo");
         String id = GenerationType.readID(jsonObject);
         String mazeID = JsonUtils.getString(jsonObject, "mazeID");
         JsonObject componentJson = JsonUtils.getJsonObject(jsonObject, "component", new JsonObject());
         Double weight = jsonObject.has("weight") ? JsonUtils.getDouble(jsonObject, "weight") : null;
         if (weight == null) {
            if (componentJson.has("weightD")) {
               weight = JsonUtils.getDouble(componentJson, "weightD");
            } else if (componentJson.has("weight")) {
               weight = (double)JsonUtils.getInt(componentJson, "weight") * 0.01D;
            }
         }

         SavedMazeComponent mazeComponent = (SavedMazeComponent)MazeGeneration.gson.fromJson(componentJson, SavedMazeComponent.class);
         return new MazeGeneration(id, weight, mazeID, mazeComponent);
      }

      public JsonElement serialize(MazeGeneration src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("id", src.id);
         if (src.weight != null) {
            jsonObject.addProperty("weight", src.weight);
         }

         jsonObject.addProperty("mazeID", src.mazeID);
         jsonObject.add("component", MazeGeneration.gson.toJsonTree(src.mazeComponent));
         return jsonObject;
      }
   }
}
