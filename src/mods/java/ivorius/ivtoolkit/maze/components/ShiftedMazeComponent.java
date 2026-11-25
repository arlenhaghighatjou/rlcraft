package ivorius.ivtoolkit.maze.components;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableSetMultimap.Builder;
import java.util.Map;
import java.util.Set;

public class ShiftedMazeComponent<M extends MazeComponent<C>, C> implements MazeComponent<C> {
   private final M component;
   private final MazeRoom shift;
   private final ImmutableSet<MazeRoom> rooms;
   private final ImmutableMap<MazePassage, C> exits;
   private final ImmutableMultimap<MazePassage, MazePassage> reachability;

   /** @deprecated */
   @Deprecated
   public ShiftedMazeComponent(M component, MazeRoom shift, ImmutableSet<MazeRoom> rooms, ImmutableMap<MazePassage, C> exits) {
      this.component = component;
      this.shift = shift;
      this.rooms = rooms;
      this.exits = exits;
      Builder<MazePassage, MazePassage> builder = ImmutableSetMultimap.builder();
      SetMazeComponent.connectAll(exits.keySet(), builder);
      this.reachability = builder.build();
   }

   public ShiftedMazeComponent(M component, MazeRoom shift, ImmutableSet<MazeRoom> rooms, ImmutableMap<MazePassage, C> exits, ImmutableMultimap<MazePassage, MazePassage> reachability) {
      this.component = component;
      this.shift = shift;
      this.rooms = rooms;
      this.exits = exits;
      this.reachability = reachability;
   }

   public M getComponent() {
      return this.component;
   }

   public MazeRoom getShift() {
      return this.shift;
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
}
