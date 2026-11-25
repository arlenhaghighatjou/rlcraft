package ivorius.reccomplex.world.gen.feature.structure.generic.maze;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.maze.components.MazePassage;
import ivorius.ivtoolkit.maze.components.MazePassages;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import ivorius.ivtoolkit.tools.NBTTagLists;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.world.gen.feature.structure.generic.Selection;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class SavedMazeReachability implements NBTCompoundObject {
   private static final Gson gson = (new GsonBuilder()).enableComplexMapKeySerialization().create();
   public final List<Set<SavedMazePath>> groups = new ArrayList();
   public final List<ImmutablePair<SavedMazePath, SavedMazePath>> crossConnections = new ArrayList();
   public boolean groupByDefault = true;

   public <T extends Entry<SavedMazePath, SavedMazePath>> SavedMazeReachability(List<Set<SavedMazePath>> groups, List<T> crossConnections, boolean groupByDefault) {
      this.set(groups, crossConnections, groupByDefault);
   }

   public SavedMazeReachability() {
   }

   public static Predicate<MazePassage> notBlocked(Collection<Connector> blockedConnections, Map<MazePassage, Connector> connections) {
      return (input) -> {
         return !blockedConnections.contains(connections.get(input));
      };
   }

   public static Set<SavedMazePath> buildExpected(SavedMazeComponent savedMazeComponent) {
      Set<SavedMazePath> complete = Sets.newHashSet((Iterable)savedMazeComponent.exitPaths.stream().map((input) -> {
         return input.path;
      }).collect(Collectors.toList()));
      completeExitPaths(complete, savedMazeComponent.rooms);
      return complete;
   }

   public static void completeExitPaths(Set<SavedMazePath> exits, Selection rooms) {
      Set<MazeRoom> roomSet = rooms.compile(true).keySet();

      for(MazeRoom room : roomSet) {
         SavedMazePaths.neighborPaths(room).filter((connection) -> {
            return (void)(!exits.contains(connection) && (!roomSet.contains(connection.getSourceRoom()) || !roomSet.contains(connection.getDestRoom())));
         }).forEach(exits::add);
      }

   }

   public void set(SavedMazeReachability reachability) {
      this.set(reachability.groups, reachability.crossConnections, reachability.groupByDefault);
   }

   public <T extends Entry<SavedMazePath, SavedMazePath>> void set(List<Set<SavedMazePath>> groups, List<T> crossConnections, boolean groupByDefault) {
      this.groups.clear();

      for(Set<SavedMazePath> group : groups) {
         this.groups.add(Sets.newHashSet((Iterable)group.stream().map(SavedMazePath::copy).collect(Collectors.toList())));
      }

      this.crossConnections.clear();

      for(Entry<SavedMazePath, SavedMazePath> entry : crossConnections) {
         this.crossConnections.add(ImmutablePair.of(((SavedMazePath)entry.getKey()).copy(), ((SavedMazePath)entry.getValue()).copy()));
      }

      this.groupByDefault = groupByDefault;
   }

   public Builder<MazePassage, MazePassage> build(Builder<MazePassage, MazePassage> builder, AxisAlignedTransform2D transform, int[] size, Predicate<MazePassage> filter, Set<MazePassage> connections) {
      filter = (connections::contains).and(filter);
      Set<MazePassage> defaultGroup = Sets.newHashSet(connections);

      for(Set<SavedMazePath> group : this.groups) {
         List<MazePassage> mazePassages = (List)group.stream().map((savedMazePath) -> {
            return MazePassages.rotated(savedMazePath.build(), transform, size);
         }).filter(filter).collect(Collectors.toList());
         defaultGroup.removeAll(mazePassages);
         this.addInterconnections(builder, mazePassages.stream());
      }

      if (this.groupByDefault) {
         this.addInterconnections(builder, defaultGroup.stream().filter(filter));
      }

      for(Entry<SavedMazePath, SavedMazePath> entry : this.crossConnections) {
         MazePassage key = MazePassages.rotated(((SavedMazePath)entry.getKey()).build(), transform, size);
         MazePassage val = MazePassages.rotated(((SavedMazePath)entry.getValue()).build(), transform, size);
         if (filter.test(key) && filter.test(val)) {
            builder.put(key, val);
         }
      }

      return builder;
   }

   protected void addInterconnections(Builder<MazePassage, MazePassage> builder, Stream<MazePassage> existing) {
      existing.reduce((last, current) -> {
         if (last != null) {
            builder.put(last, current);
            builder.put(current, last);
         }

         return (void)current;
      });
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.groups.clear();
      this.groups.addAll(Lists.transform(NBTTagLists.listsFrom(compound, "groups"), (input) -> {
         return Sets.newHashSet(NBTCompoundObjects.readList(input, SavedMazePath::new));
      }));
      this.crossConnections.clear();
      this.crossConnections.addAll(Lists.transform(NBTTagLists.compoundsFrom(compound, "crossConnections"), (input) -> {
         return ImmutablePair.of(NBTCompoundObjects.readFrom(input, "key", SavedMazePath::new), NBTCompoundObjects.readFrom(input, "val", SavedMazePath::new));
      }));
      this.groupByDefault = compound.getBoolean("groupByDefault");
   }

   public void writeToNBT(NBTTagCompound compound) {
      NBTTagLists.writeTo(compound, "groups", Lists.transform(this.groups, NBTCompoundObjects::writeList));
      NBTTagLists.writeTo(compound, "crossConnections", Lists.transform(this.crossConnections, (input) -> {
         NBTTagCompound compound1 = new NBTTagCompound();
         NBTCompoundObjects.writeTo(compound1, "key", (NBTCompoundObject)input.getKey());
         NBTCompoundObjects.writeTo(compound1, "val", (NBTCompoundObject)input.getValue());
         return compound1;
      }));
      compound.setBoolean("groupByDefault", this.groupByDefault);
   }

   public static class Serializer implements JsonSerializer<SavedMazeReachability>, JsonDeserializer<SavedMazeReachability> {
      public Serializer() {
      }

      public SavedMazeReachability deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "MazeReachability");
         List<Set<SavedMazePath>> groups = (List)context.deserialize(jsonObject.get("groups"), (new TypeToken<List<Set<SavedMazePath>>>() {
         }).getType());
         if (groups == null) {
            groups = Collections.emptyList();
         }

         List<ImmutablePair<SavedMazePath, SavedMazePath>> crossConnections = (List)SavedMazeReachability.gson.fromJson(JsonUtils.getJsonArray(jsonObject, "crossConnections", new JsonArray()), (new TypeToken<List<ImmutablePair<SavedMazePath, SavedMazePath>>>() {
         }).getType());
         if (crossConnections == null) {
            crossConnections = Collections.emptyList();
         }

         boolean groupByDefault = JsonUtils.getBoolean(jsonObject, "groupByDefault", true);
         return new SavedMazeReachability(groups, crossConnections, groupByDefault);
      }

      public JsonElement serialize(SavedMazeReachability src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.add("groups", context.serialize(src.groups));
         jsonObject.add("crossConnections", SavedMazeReachability.gson.toJsonTree(src.crossConnections));
         jsonObject.addProperty("groupByDefault", Boolean.valueOf(src.groupByDefault));
         return jsonObject;
      }
   }
}
