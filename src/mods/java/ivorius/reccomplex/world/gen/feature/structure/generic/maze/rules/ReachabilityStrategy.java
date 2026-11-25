package ivorius.reccomplex.world.gen.feature.structure.generic.maze.rules;

import com.google.common.collect.Sets;
import gnu.trove.function.TIntFunction;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import gnu.trove.procedure.TObjectIntProcedure;
import ivorius.ivtoolkit.maze.components.ConnectionStrategy;
import ivorius.ivtoolkit.maze.components.MazeComponent;
import ivorius.ivtoolkit.maze.components.MazePassage;
import ivorius.ivtoolkit.maze.components.MazePredicate;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.maze.components.MorphingMazeComponent;
import ivorius.ivtoolkit.maze.components.ShiftedMazeComponent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReachabilityStrategy<C> implements MazePredicate<C> {
   private final Collection<ReachabilityStrategy.Ability<C>> traversalAbilities = new ArrayList();
   private ReachabilityStrategy<C>.ConnectionPoint mainConnectionPoint;
   private final List<ReachabilityStrategy<C>.ConnectionPoint> connectionPoints = new ArrayList();
   private final TObjectIntMap<ReachabilityStrategy<C>.ConnectionPoint> stepsReached = new TObjectIntHashMap();
   private final Predicate<MazeRoom> confiner;
   private final ConnectionStrategy<C> connectionStrategy;
   private boolean preventConnection;

   public ReachabilityStrategy(Predicate<MazeRoom> confiner, ConnectionStrategy<C> connectionStrategy, boolean preventConnection) {
      this.confiner = confiner;
      this.connectionStrategy = connectionStrategy;
      this.preventConnection = preventConnection;
   }

   public static <C> ReachabilityStrategy<C> connect(Collection<Collection<MazePassage>> points, Predicate<MazeRoom> confiner, Collection<ReachabilityStrategy.Ability<C>> traversalAbilities, ConnectionStrategy<C> connectionStrategy) {
      ReachabilityStrategy<C> strategy = new ReachabilityStrategy<C>(confiner, connectionStrategy, false);
      strategy.setConnection(points);
      strategy.traversalAbilities.addAll(traversalAbilities);
      return strategy;
   }

   public static <C> ReachabilityStrategy<C> preventConnection(Collection<Collection<MazePassage>> points, Predicate<MazeRoom> confiner, ConnectionStrategy<C> connectionStrategy) {
      ReachabilityStrategy<C> strategy = new ReachabilityStrategy<C>(confiner, connectionStrategy, true);
      strategy.setConnection(points);
      return strategy;
   }

   public static <C> Collection<ReachabilityStrategy.Ability<C>> compileAbilities(Collection<? extends MazeComponent<C>> components) {
      Collection<ReachabilityStrategy.Ability<C>> abilities = new HashSet();

      for(MazeComponent<C> component : components) {
         for(MazePassage source : component.reachability().keySet()) {
            if (component.rooms().contains(source.getSource())) {
               for(MazePassage exit : traverse(Collections.singleton(component), new HashSet(), Collections.singleton(source), (Consumer)null)) {
                  if (!component.rooms().contains(exit.getSource()) && !source.equals(exit.inverse())) {
                     ReachabilityStrategy.Ability<C> ability = ReachabilityStrategy.Ability.<C>from(source, exit);
                     Optional<ReachabilityStrategy.Ability<C>> existing = abilities.stream().filter((a) -> {
                        return a.same(ability);
                     }).findFirst();
                     if (existing.isPresent()) {
                        ((ReachabilityStrategy.Ability)existing.get()).add(component);
                     } else {
                        ability.add(component);
                        abilities.add(ability);
                     }
                  }
               }
            }
         }
      }

      abilities.forEach((abilityx) -> {
         for(int i = 0; i < ability.masks.size(); ++i) {
            ReachabilityStrategy.Ability<C>.Mask mask = (ReachabilityStrategy.Ability.Mask)ability.masks.get(i);
            ability.masks.remove(i);
            if (approximateCanReach(mask.rooms, (c, p) -> {
               return compatible(mask.exits.get(p), c);
            }, abilities, Collections.singleton(ability.start()), Collections.singleton(ability.destination()), (Predicate)null) != null) {
               --i;
            } else {
               ability.masks.add(i, mask);
            }
         }

      });
      abilities.removeIf((a) -> {
         return a.masks.isEmpty();
      });
      return abilities;
   }

   protected static <C> boolean compatible(C existing, C add) {
      return existing == null || add == null || existing.equals(add);
   }

   public static <C> Predicate<C> connectorTraverser(Set<C> blockingConnections) {
      return (input) -> {
         return !blockingConnections.contains(input);
      };
   }

   protected static <C> Set<MazePassage> traverse(Collection<MazeComponent<C>> mazes, @Nonnull Collection<MazePassage> traversed, Collection<MazePassage> connections, @Nullable Consumer<MazePassage> visitor) {
      if (connections.size() <= 0) {
         return Collections.emptySet();
      } else {
         Deque<MazePassage> dirty = new ArrayDeque(connections);
         Set<MazePassage> added = new HashSet();

         MazePassage src;
         while((src = (MazePassage)dirty.pollFirst()) != null) {
            for(MazeComponent<C> maze : mazes) {
               maze.reachability().get(src).forEach((dest) -> {
                  if (traversed.add(dest)) {
                     if (visitor != null) {
                        visitor.accept(dest);
                     }

                     added.add(dest);
                     dirty.addLast(dest);
                  }

               });
            }
         }

         return added;
      }
   }

   private static <C> Set<MazeRoom> approximateCanReach(Set<MazeRoom> rooms, BiPredicate<C, MazePassage> connector, Collection<ReachabilityStrategy.Ability<C>> abilities, Set<MazePassage> left, Set<MazePassage> right, Predicate<MazeRoom> confiner) {
      return approximateCanReach(rooms, abilities, Collections.emptyList(), left, right, Collections.emptyList(), confiner, connector);
   }

   private static <C> Set<MazeRoom> approximateCanReach(Set<MazeRoom> rooms, Collection<ReachabilityStrategy.Ability<C>> abilities, Collection<MazeComponent<C>> mazes, Set<MazePassage> left, Set<MazePassage> right, Collection<MazePassage> pTraversed, Predicate<MazeRoom> confiner, BiPredicate<C, MazePassage> connector) {
      if (left.size() > 0 && right.size() > 0) {
         Stream var10000 = left.stream();
         right.getClass();
         if (var10000.anyMatch(right::contains)) {
            return null;
         } else {
            Collection<MazePassage> traversed = Sets.newHashSet(pTraversed);
            Predicate<MazeRoom> roomPlaceable = confiner != null ? (o) -> {
               return confiner.test(o) && !rooms.contains(o);
            } : rooms::contains;
            Predicate<MazePassage> passagePlaceable = (o) -> {
               return roomPlaceable.test(o.getSource());
            };
            Set<MazePassage> visited = Sets.newHashSet(left);
            TreeSet<MazePassage> dirty = Sets.newTreeSet((o1, o2) -> {
               int compare;
               if ((compare = Double.compare(minDistanceSQ(o1, right), minDistanceSQ(o2, right))) != 0) {
                  return compare;
               } else if ((compare = compare(o1.getSource().getCoordinates(), o2.getSource().getCoordinates())) != 0) {
                  return compare;
               } else {
                  return (compare = compare(o1.getDest().getCoordinates(), o2.getDest().getCoordinates())) != 0 ? compare : 0;
               }
            });
            dirty.addAll(left);
            visited.addAll(left);

            MazePassage curPre;
            while((curPre = (MazePassage)dirty.pollFirst()) != null) {
               MazePassage cur = curPre;
               MazePassage curNormal = curPre.normalize();
               var10000 = abilities.stream().filter((abilityx) -> {
                  return !visited.contains(ability.destination().add(cur.getSource()));
               }).filter((abilityx) -> {
                  return ability.start.getDest().equals(curNormal.getDest());
               }).filter((abilityx) -> {
                  return ability.connect(cur.getSource(), roomPlaceable, connector);
               });
               var10000.getClass();

               for(ReachabilityStrategy.Ability ability : var10000::iterator) {
                  MazePassage dest = ability.destination().add(cur.getSource());
                  if (right.contains(dest)) {
                     return compileRoute(visited);
                  }

                  if (passagePlaceable.test(dest) && visited.add(dest)) {
                     dirty.add(dest);
                  }

                  var10000 = traverse(mazes, traversed, Collections.singleton(dest), (Consumer)null).stream().distinct();
                  var10000.getClass();

                  for(MazePassage p : var10000::iterator) {
                     if (right.contains(p)) {
                        return compileRoute(visited);
                     }

                     if (passagePlaceable.test(p) && visited.add(p)) {
                        dirty.add(p);
                     }
                  }
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   protected static Set<MazeRoom> compileRoute(Set<MazePassage> passages) {
      return (Set)passages.stream().map(MazePassage::getSource).collect(Collectors.toSet());
   }

   private static int compare(int[] left, int[] right) {
      for(int i = 0; i < left.length; ++i) {
         int cmp = Integer.compare(left[i], right[i]);
         if (cmp != 0) {
            return cmp;
         }
      }

      return 0;
   }

   private static double minDistanceSQ(MazePassage passage, Collection<MazePassage> rooms) {
      return rooms.stream().map(MazePassage::getDest).mapToDouble((o) -> {
         return o.distanceSQ(passage.getSource());
      }).min().orElseThrow(InternalError::new);
   }

   protected static <C> C exitFromEither(MazeComponent<C> left, MazeComponent<C> right, MazePassage p) {
      C c = (C)left.exits().get(p);
      return (C)(c != null ? c : right.exits().get(p));
   }

   protected void setConnection(Collection<Collection<MazePassage>> points) {
      this.connectionPoints.addAll((Collection)points.stream().map((p) -> {
         return (void)(new ReachabilityStrategy.ConnectionPoint(new Collection[]{p, (Collection)p.stream().map(MazePassage::inverse).collect(Collectors.toList())}));
      }).collect(Collectors.toList()));
      this.mainConnectionPoint = this.connectionPoints.size() > 0 ? (ReachabilityStrategy.ConnectionPoint)this.connectionPoints.remove(0) : null;
   }

   public boolean canPlace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component) {
      if (this.preventConnection && !this.stepsReached.isEmpty()) {
         return true;
      } else if (this.stepsReached.size() == this.connectionPoints.size()) {
         return true;
      } else {
         Predicate<MazePassage> isDirtyPre = this.dirtyPassages(maze.exits().keySet());
         boolean[] unconnectable = new boolean[this.connectionPoints.size()];

         for(int i = 0; i < this.connectionPoints.size(); ++i) {
            ReachabilityStrategy<C>.ConnectionPoint point = (ReachabilityStrategy.ConnectionPoint)this.connectionPoints.get(i);
            if (point.traversed.stream().noneMatch(isDirtyPre)) {
               unconnectable[i] = true;
            }
         }

         this.place(maze, component, true);
         Set<MazeRoom> roomsFromBoth = Sets.union(maze.rooms(), component.rooms());
         Set<MazePassage> exitsFromBoth = Sets.union(maze.exits().keySet(), component.exits().keySet());
         Predicate<MazePassage> isDirty = this.dirtyPassages(exitsFromBoth);
         boolean canPlace;
         if (this.preventConnection) {
            canPlace = this.stepsReached.isEmpty();
         } else {
            canPlace = true;

            for(int i = 0; i < this.connectionPoints.size(); ++i) {
               ReachabilityStrategy<C>.ConnectionPoint point = (ReachabilityStrategy.ConnectionPoint)this.connectionPoints.get(i);
               if (point.intersectsRoute(component)) {
                  canPlace = this.stepsReached.containsKey(point) || unconnectable[i] || (point.route = approximateCanReach(roomsFromBoth, this.traversalAbilities, Arrays.asList(maze, component), (Set)point.traversed.stream().filter(isDirty).collect(Collectors.toSet()), (Set)this.mainConnectionPoint.traversed.stream().filter(isDirty).map(MazePassage::inverse).collect(Collectors.toSet()), point.traversed, this.confiner, (c, p) -> {
                     return this.connectionStrategy.connect(p, exitFromEither(maze, component, p.inverse()), c) > 0.0F;
                  })) != null;
                  if (!canPlace) {
                     break;
                  }
               }
            }
         }

         this.unplace(maze, component, true);
         return canPlace;
      }
   }

   @Nonnull
   protected Predicate<MazePassage> dirtyPassages(Set<MazePassage> r) {
      return (input) -> {
         return this.confiner.test(input.getSource()) && !r.contains(input);
      };
   }

   public void willPlace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component) {
      this.place(maze, component, false);
      this.connectionPoints.stream().filter((p) -> {
         return (void)p.intersectsRoute(component);
      }).forEach((point) -> {
         point.route = null;
      });
   }

   public void didPlace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component) {
   }

   public void willUnplace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component) {
   }

   protected void place(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component, boolean simulate) {
      if (this.stepsReached.size() == this.connectionPoints.size()) {
         this.stepsReached.transformValues((i) -> {
            return (void)(i + 1);
         });
      } else {
         for(ReachabilityStrategy<C>.ConnectionPoint point : this.connectionPoints) {
            if (this.stepsReached.containsKey(point)) {
               this.stepsReached.adjustValue(point, 1);
            } else {
               point.order.add(this.traverse(maze, component, point.traversed, this.mainConnectionPoint.traversed, (p) -> {
                  this.stepsReached.put(point, 0);
               }));
            }
         }

         this.mainConnectionPoint.order.add(this.traverse(maze, component, this.mainConnectionPoint.traversed, (Collection)this.connectionPoints.stream().filter((pointx) -> {
            return (void)(!this.stepsReached.containsKey(point));
         }).flatMap((pointx) -> {
            return (void)point.traversed.stream();
         }).collect(Collectors.toList()), (p) -> {
            this.connectionPoints.stream().filter((point) -> {
               return (void)point.traversed.contains(p);
            }).forEach((point) -> {
               this.stepsReached.put(point, 0);
            });
         }));
      }

   }

   protected Set<MazePassage> traverse(MazeComponent<C> maze, MazeComponent<C> component, Set<MazePassage> traversed, Collection<MazePassage> goal, Consumer<MazePassage> goalConsumer) {
      return traverse(Arrays.asList(maze, component), traversed, Sets.intersection(component.exits().keySet(), traversed), (connection) -> {
         if (goal.contains(connection)) {
            goalConsumer.accept(connection);
         }

      });
   }

   public void didUnplace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component) {
      this.unplace(maze, component, false);
   }

   protected void unplace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<?, C> component, boolean simulate) {
      this.stepsReached.transformValues((i) -> {
         return (void)(i - 1);
      });
      this.stepsReached.retainEntries((a, i) -> {
         return (void)(i >= 0);
      });
      if (this.stepsReached.size() < this.connectionPoints.size()) {
         this.mainConnectionPoint.reverseStep();
         this.connectionPoints.stream().filter((point) -> {
            return (void)(point.order.size() > this.mainConnectionPoint.order.size());
         }).forEach(ReachabilityStrategy.ConnectionPoint::reverseStep);
      }

   }

   public boolean isDirtyConnection(MazeRoom dest, MazeRoom source, C c) {
      return true;
   }

   protected Function<MazeRoom, String> dirtyMarker(MazeComponent component, @Nullable MazeComponent place) {
      return (r) -> {
         if (this.isDirty(r, this.mainConnectionPoint, component)) {
            return "0";
         } else {
            Stream var10000 = this.connectionPoints.stream().filter((point) -> {
               return this.isDirty(r, point, component);
            });
            List var10001 = this.connectionPoints;
            this.connectionPoints.getClass();
            int p = var10000.mapToInt(var10001::indexOf).findFirst().orElse(-1);
            if (p >= 0) {
               return "" + (p + 1);
            } else {
               return place != null && place.rooms().contains(r) ? "O" : null;
            }
         }
      };
   }

   private boolean isDirty(MazeRoom r, ReachabilityStrategy<C>.ConnectionPoint point, MazeComponent<?> component) {
      boolean var4;
      if (!this.stepsReached.containsKey(point)) {
         Stream var10000 = point.traversed.stream().filter(this.dirtyPassages(component.exits().keySet()));
         r.getClass();
         if (var10000.anyMatch(r::equals)) {
            var4 = true;
            return var4;
         }
      }

      var4 = false;
      return var4;
   }

   private static class Ability<C> {
      @Nonnull
      protected final MazePassage start;
      @Nonnull
      protected final MazePassage destination;
      protected List<ReachabilityStrategy.Ability<C>.Mask> masks = new ArrayList();

      public Ability(@Nonnull MazePassage start, @Nonnull MazePassage destination) {
         this.start = start;
         this.destination = destination;
      }

      public static <C> ReachabilityStrategy.Ability<C> from(@Nonnull MazePassage start, @Nonnull MazePassage destination) {
         return new ReachabilityStrategy.Ability<C>(start.normalize(), destination.sub(start.getSource()));
      }

      public boolean same(ReachabilityStrategy.Ability<C> ability) {
         return this.start.equals(ability.start) && this.destination.equals(ability.destination);
      }

      public void add(MazeComponent<C> component) {
         List var10000 = this.masks;
         Set var10004 = (Set)component.rooms().stream().map((r) -> {
            return (void)r.sub(this.start.getSource());
         }).collect(Collectors.toSet());
         Stream var10005 = component.exits().keySet().stream();
         Function var10006 = (r) -> {
            return (void)r.sub(this.start.getSource());
         };
         Map var10007 = component.exits();
         var10007.getClass();
         var10000.add(new ReachabilityStrategy.Ability.Mask(var10004, (Map)var10005.collect(Collectors.toMap(var10006, var10007::get))));
      }

      public boolean connect(MazeRoom reference, Predicate<MazeRoom> roomPlaceable, BiPredicate<C, MazePassage> connector) {
         return this.masks.stream().anyMatch((mask) -> {
            return mask.rooms.stream().map((r) -> {
               return (boolean)r.add(reference);
            }).allMatch(roomPlaceable) && mask.exits.keySet().stream().allMatch((p) -> {
               return connector.test(mask.exits.get(p), p.add(reference));
            });
         });
      }

      public MazePassage start() {
         return this.start;
      }

      public MazePassage destination() {
         return this.destination;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            ReachabilityStrategy.Ability<?> ability = (ReachabilityStrategy.Ability)o;
            if (!this.start.equals(ability.start)) {
               return false;
            } else if (!this.destination.equals(ability.destination)) {
               return false;
            } else {
               return this.masks != null ? this.masks.equals(ability.masks) : ability.masks == null;
            }
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.start.hashCode();
         result = 31 * result + this.destination.hashCode();
         result = 31 * result + (this.masks != null ? this.masks.hashCode() : 0);
         return result;
      }

      public String toString() {
         return "Ability{start=" + this.start + ", destination=" + this.destination + ", masks=" + this.masks + '}';
      }

      public class Mask {
         @Nonnull
         public final Set<MazeRoom> rooms;
         @Nonnull
         public final Map<MazePassage, C> exits;

         public Mask(@Nonnull Set<MazeRoom> rooms, @Nonnull Map<MazePassage, C> exits) {
            this.rooms = rooms;
            this.exits = exits;
         }

         public boolean equals(Object o) {
            if (this == o) {
               return true;
            } else if (o != null && this.getClass() == o.getClass()) {
               ReachabilityStrategy.Ability<C>.Mask mask = (ReachabilityStrategy.Ability.Mask)o;
               return !this.rooms.equals(mask.rooms) ? false : this.exits.equals(mask.exits);
            } else {
               return false;
            }
         }

         public int hashCode() {
            int result = this.rooms.hashCode();
            result = 31 * result + this.exits.hashCode();
            return result;
         }

         public String toString() {
            return "Mask{rooms=" + this.rooms + ", exits=" + this.exits + '}';
         }
      }
   }

   private class ConnectionPoint {
      public final Set<MazePassage> traversed = new HashSet();
      public final List<Set<MazePassage>> order = new ArrayList();
      public Set<MazeRoom> route = null;

      @SafeVarargs
      public ConnectionPoint(Collection<MazePassage>[] points) {
         Stream var10000 = Arrays.stream(points);
         Set var10001 = this.traversed;
         this.traversed.getClass();
         var10000.forEach(var10001::addAll);
      }

      public void reverseStep() {
         this.traversed.removeAll((Collection)this.order.remove(this.order.size() - 1));
      }

      public boolean intersectsRoute(ShiftedMazeComponent<?, C> component) {
         return this.route == null || component.rooms().stream().anyMatch((r) -> {
            return this.route.contains(r);
         });
      }
   }
}
