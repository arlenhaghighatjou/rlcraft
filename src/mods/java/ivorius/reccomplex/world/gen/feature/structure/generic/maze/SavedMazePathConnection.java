package ivorius.reccomplex.world.gen.feature.structure.generic.maze;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import ivorius.ivtoolkit.maze.components.MazePassage;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.expression.EnvironmentExpression;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.tuple.Pair;

public class SavedMazePathConnection implements NBTCompoundObject {
   public final SavedMazePath path;
   public final SavedConnector connector;
   public final List<SavedMazePathConnection.ConditionalConnector> conditionalConnectors;

   public SavedMazePathConnection() {
      this.path = new SavedMazePath();
      this.connector = new SavedConnector("Path");
      this.conditionalConnectors = new ArrayList();
   }

   public SavedMazePathConnection(SavedMazePath path, SavedConnector connector, List<SavedMazePathConnection.ConditionalConnector> conditionalConnectors) {
      this(path.pathDimension, path.sourceRoom, path.pathGoesUp, connector.id, conditionalConnectors);
   }

   public SavedMazePathConnection(int pathDimension, MazeRoom sourceRoom, boolean pathGoesUp, String connector, List<SavedMazePathConnection.ConditionalConnector> conditionalConnectors) {
      this.path = new SavedMazePath();
      this.connector = new SavedConnector("Path");
      this.conditionalConnectors = new ArrayList();
      this.path.pathDimension = pathDimension;
      this.path.sourceRoom = sourceRoom;
      this.path.pathGoesUp = pathGoesUp;
      this.connector.id = connector;
      this.conditionalConnectors.addAll(conditionalConnectors);
   }

   public SavedMazePath getPath() {
      return this.path;
   }

   public SavedConnector getConnector() {
      return this.connector;
   }

   public Entry<MazePassage, Connector> build(Environment environment, ConnectorFactory factory) {
      SavedConnector connector = this.connector;

      for(SavedMazePathConnection.ConditionalConnector conditionalConnector : this.conditionalConnectors) {
         if (conditionalConnector.expression.test(environment)) {
            connector = conditionalConnector.connector;
            break;
         }
      }

      return Pair.of(this.path.build(), connector.toConnector(factory));
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.path.readFromNBT(compound);
      this.connector.id = compound.hasKey("connector", 8) ? compound.getString("connector") : "Path";
   }

   public void writeToNBT(NBTTagCompound compound) {
      this.path.writeToNBT(compound);
      compound.setString("connector", this.connector.id);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SavedMazePathConnection that = (SavedMazePathConnection)o;
         if (this.path != null) {
            if (!this.path.equals(that.path)) {
               return false;
            }
         } else if (that.path != null) {
            return false;
         }

         if (this.connector != null) {
            if (!this.connector.equals(that.connector)) {
               return false;
            }
         } else if (that.connector != null) {
            return false;
         }

         return this.conditionalConnectors != null ? this.conditionalConnectors.equals(that.conditionalConnectors) : that.conditionalConnectors == null;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.path != null ? this.path.hashCode() : 0;
      result = 31 * result + (this.connector != null ? this.connector.hashCode() : 0);
      result = 31 * result + (this.conditionalConnectors != null ? this.conditionalConnectors.hashCode() : 0);
      return result;
   }

   public SavedMazePathConnection copy() {
      return new SavedMazePathConnection(this.path.copy(), this.connector.copy(), (List)this.conditionalConnectors.stream().map(SavedMazePathConnection.ConditionalConnector::copy).collect(Collectors.toList()));
   }

   public static class ConditionalConnector {
      public EnvironmentExpression expression;
      public final SavedConnector connector = new SavedConnector("Path");

      public ConditionalConnector(String expression, String connector) {
         this.expression = (EnvironmentExpression)ExpressionCache.of(new EnvironmentExpression(), expression);
         this.connector.id = connector;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            SavedMazePathConnection.ConditionalConnector that = (SavedMazePathConnection.ConditionalConnector)o;
            if (this.expression != null) {
               if (!this.expression.equals(that.expression)) {
                  return false;
               }
            } else if (that.expression != null) {
               return false;
            }

            return this.connector != null ? this.connector.equals(that.connector) : that.connector == null;
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.expression != null ? this.expression.hashCode() : 0;
         result = 31 * result + (this.connector != null ? this.connector.hashCode() : 0);
         return result;
      }

      public SavedMazePathConnection.ConditionalConnector copy() {
         return new SavedMazePathConnection.ConditionalConnector(this.expression.getExpression(), this.connector.id);
      }

      public static class Serializer implements JsonSerializer<SavedMazePathConnection.ConditionalConnector>, JsonDeserializer<SavedMazePathConnection.ConditionalConnector> {
         public Serializer() {
         }

         public SavedMazePathConnection.ConditionalConnector deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = JsonUtils.asJsonObject(json, "ConditionalConnector");
            return new SavedMazePathConnection.ConditionalConnector(JsonUtils.getString(jsonObject, "expression", ""), JsonUtils.getString(jsonObject, "connector", "Path"));
         }

         public JsonElement serialize(SavedMazePathConnection.ConditionalConnector src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("expression", src.expression.getExpression());
            jsonObject.addProperty("connector", src.connector.id);
            return jsonObject;
         }
      }
   }

   public static class Serializer implements JsonSerializer<SavedMazePathConnection>, JsonDeserializer<SavedMazePathConnection> {
      public Serializer() {
      }

      public SavedMazePathConnection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "MazeRoom");
         SavedMazePath path = (SavedMazePath)context.deserialize(json, SavedMazePath.class);
         String connector = JsonUtils.getString(jsonObject, "connector", "Path");
         List<SavedMazePathConnection.ConditionalConnector> conditionalConnectors = (List)context.deserialize(JsonUtils.getJsonArray(jsonObject, "conditionalConnectors", new JsonArray()), (new TypeToken<List<SavedMazePathConnection.ConditionalConnector>>() {
         }).getType());
         return new SavedMazePathConnection(path, new SavedConnector(connector), conditionalConnectors);
      }

      public JsonElement serialize(SavedMazePathConnection src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = (JsonObject)context.serialize(src.path);
         jsonObject.addProperty("connector", src.connector.id);
         jsonObject.add("conditionalConnectors", context.serialize(src.conditionalConnectors));
         return jsonObject;
      }
   }
}
