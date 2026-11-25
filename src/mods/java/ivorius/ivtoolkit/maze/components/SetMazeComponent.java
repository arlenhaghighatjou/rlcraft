package ivorius.ivtoolkit.maze.components;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SetMazeComponent<C> implements MorphingMazeComponent<C> {
   public final Set<MazeRoom> rooms;
   public final Map<MazePassage, C> exits;
   public final Multimap<MazePassage, MazePassage> reachability;

   public SetMazeComponent() {
      this.rooms = new HashSet();
      this.exits = new HashMap();
      this.reachability = HashMultimap.create();
   }

   /** @deprecated */
   @Deprecated
   public SetMazeComponent(Set<MazeRoom> rooms, Map<MazePassage, C> exits) {
      this(rooms, exits, HashMultimap.create());
      connectAll(exits.keySet(), this.reachability);
   }

   public static void connectAll(Set<MazePassage> exits, Multimap<MazePassage, MazePassage> reachability) {
      List<MazePassage> connections = Lists.newArrayList(exits);

      for(int i = 1; i < connections.size(); ++i) {
         MazePassage left = (MazePassage)connections.get(i - 1);
         MazePassage right = (MazePassage)connections.get(i);
         reachability.put(left, right);
         reachability.put(right, left);
      }

   }

   public static void connectAll(Set<MazePassage> exits, Builder<MazePassage, MazePassage> reachability) {
      List<MazePassage> connections = Lists.newArrayList(exits);

      for(int i = 1; i < connections.size(); ++i) {
         MazePassage left = (MazePassage)connections.get(i - 1);
         MazePassage right = (MazePassage)connections.get(i);
         reachability.put(left, right);
         reachability.put(right, left);
      }

   }

   public SetMazeComponent(Set<MazeRoom> rooms, Map<MazePassage, C> exits, Multimap<MazePassage, MazePassage> reachability) {
      this.rooms = new HashSet();
      this.exits = new HashMap();
      this.reachability = HashMultimap.create();
      this.rooms.addAll(rooms);
      this.exits.putAll(exits);
      this.reachability.putAll(reachability);
   }

   public Set<MazeRoom> rooms() {
      return this.rooms;
   }

   public Map<MazePassage, C> exits() {
      return this.exits;
   }

   public Multimap<MazePassage, MazePassage> reachability() {
      return this.reachability;
   }

   public void add(MazeComponent<C> component) {
      this.rooms.addAll(component.rooms());
      component.exits().entrySet().stream().filter((entry) -> {
         return (void)(this.exits.remove(entry.getKey()) == null);
      }).forEach((entry) -> {
         this.exits.put(entry.getKey(), entry.getValue());
      });
      this.reachability.putAll(component.reachability());
   }

   public void set(MazeComponent<C> component) {
      this.rooms.clear();
      this.rooms.addAll(component.rooms());
      this.exits.clear();
      this.exits.putAll(component.exits());
      this.reachability.clear();
      this.reachability.putAll(component.reachability());
   }

   public MorphingMazeComponent<C> copy() {
      return new SetMazeComponent<C>(this.rooms, this.exits, this.reachability);
   }
}
