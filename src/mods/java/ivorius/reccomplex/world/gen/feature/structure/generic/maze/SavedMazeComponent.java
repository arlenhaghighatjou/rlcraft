package ivorius.reccomplex.world.gen.feature.structure.generic.maze;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.tools.IvNBTHelper;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import ivorius.ivtoolkit.tools.NBTTagLists;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.nbt.NBTTagCompound;

public class SavedMazeComponent implements NBTCompoundObject {
   public final Selection rooms = new Selection(3);
   public final List<SavedMazePathConnection> exitPaths = new ArrayList();
   public final SavedConnector defaultConnector = new SavedConnector("Wall");
   public final SavedMazeReachability reachability = new SavedMazeReachability();

   public SavedMazeComponent() {
      this.rooms.add(new Selection.Area(true, new int[3], new int[3]));
   }

   public SavedMazeComponent(String defaultConnector) {
      this.defaultConnector.id = defaultConnector;
   }

   public boolean isValid() {
      return !this.rooms.isEmpty();
   }

   public Collection<MazeRoom> getRooms() {
      return this.rooms.compile(true).keySet();
   }

   public List<SavedMazePathConnection> getExitPaths() {
      return Collections.unmodifiableList(this.exitPaths);
   }

   public void setExitPaths(List<SavedMazePathConnection> exitPaths) {
      this.exitPaths.clear();
      this.exitPaths.addAll(exitPaths);
   }

   public int[] boundsSize() {
      return this.rooms.boundsSize();
   }

   public void readFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("roomArea", 10)) {
         this.rooms.readFromNBT(compound.getCompoundTag("roomArea"));
      } else if (compound.hasKey("rooms", 9)) {
         this.rooms.clear();
         this.rooms.addAll(Lists.transform(NBTTagLists.compoundsFrom(compound, "rooms"), (input) -> {
            MazeRoom room = new MazeRoom(IvNBTHelper.readIntArrayFixedSize("coordinates", 3, compound));
            int[] coordinates = room.getCoordinates();
            return new Selection.Area(true, coordinates, (int[])coordinates.clone());
         }));
      }

      this.exitPaths.clear();
      this.exitPaths.addAll(NBTCompoundObjects.readListFrom(compound, "exits", SavedMazePathConnection::new));
      this.defaultConnector.id = compound.hasKey("defaultConnector", 8) ? compound.getString("defaultConnector") : "Path";
      if (!compound.hasKey("reachability")) {
         this.reachability.groupByDefault = false;
      } else {
         this.reachability.readFromNBT(compound.getCompoundTag("reachability"));
      }

   }

   public void writeToNBT(NBTTagCompound compound) {
      compound.setTag("roomArea", NBTCompoundObjects.write(this.rooms));
      NBTCompoundObjects.writeListTo(compound, "exits", this.exitPaths);
      compound.setString("defaultConnector", this.defaultConnector.id);
      NBTCompoundObjects.writeTo(compound, "reachability", this.reachability);
   }

   public static class RoomSerializer implements JsonSerializer<MazeRoom>, JsonDeserializer<MazeRoom> {
      public RoomSerializer() {
      }

      public MazeRoom deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "MazeRoom");
         return new MazeRoom((int[])context.deserialize(jsonObject.get("coordinates"), int[].class));
      }

      public JsonElement serialize(MazeRoom src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.add("coordinates", context.serialize(src.getCoordinates()));
         return jsonObject;
      }
   }

   public static class Serializer implements JsonSerializer<SavedMazeComponent>, JsonDeserializer<SavedMazeComponent> {
      public Serializer() {
      }

      public SavedMazeComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "MazeComponent");
         String defaultConnector = JsonUtils.getString(jsonObject, "defaultConnector", "Wall");
         SavedMazeComponent mazeComponent = new SavedMazeComponent(defaultConnector);
         if (jsonObject.has("roomArea")) {
            mazeComponent.rooms.addAll((Collection)context.deserialize(jsonObject.get("roomArea"), (new TypeToken<List<Selection.Area>>() {
            }).getType()));
         }

         if (jsonObject.has("rooms")) {
            MazeRoom[] rooms = (MazeRoom[])context.deserialize(jsonObject.get("rooms"), MazeRoom[].class);

            for(MazeRoom room : rooms) {
               mazeComponent.rooms.add(new Selection.Area(true, room.getCoordinates(), room.getCoordinates()));
            }
         }

         SavedMazePathConnection[] exits = (SavedMazePathConnection[])context.deserialize(jsonObject.get("exits"), SavedMazePathConnection[].class);
         mazeComponent.setExitPaths(Arrays.asList(exits));
         if (jsonObject.has("reachability")) {
            mazeComponent.reachability.set((SavedMazeReachability)context.deserialize(jsonObject.get("reachability"), SavedMazeReachability.class));
         }

         return mazeComponent;
      }

      public JsonElement serialize(SavedMazeComponent src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.add("roomArea", context.serialize(src.rooms));
         jsonObject.add("exits", context.serialize(src.exitPaths));
         jsonObject.addProperty("defaultConnector", src.defaultConnector.id);
         jsonObject.add("reachability", context.serialize(src.reachability));
         return jsonObject;
      }
   }
}
