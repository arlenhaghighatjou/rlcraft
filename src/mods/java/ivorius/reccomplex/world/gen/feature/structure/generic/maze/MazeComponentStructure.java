package ivorius.reccomplex.world.gen.feature.structure.generic.maze;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.UnmodifiableIterator;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.math.IvVecMathHelper;
import ivorius.ivtoolkit.maze.components.MazePassage;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.maze.components.WeightedMazeComponent;
import ivorius.reccomplex.world.gen.feature.structure.VariableDomain;
import java.util.Map;
import java.util.Set;

public class MazeComponentStructure<C> implements WeightedMazeComponent<C> {
   public final ImmutableSet<MazeRoom> rooms;
   public final ImmutableMap<MazePassage, C> exits;
   public final ImmutableMultimap<MazePassage, MazePassage> reachability;
   public double weight;
   public String structureID;
   public VariableDomain variableDomain;
   public AxisAlignedTransform2D transform;

   public MazeComponentStructure(double weight, String structureID, VariableDomain variableDomain, AxisAlignedTransform2D transform, ImmutableSet<MazeRoom> rooms, ImmutableMap<MazePassage, C> exits, ImmutableMultimap<MazePassage, MazePassage> reachability) {
      this.weight = weight;
      this.variableDomain = variableDomain;
      this.structureID = structureID;
      this.transform = transform;
      this.rooms = rooms;
      this.exits = exits;
      this.reachability = reachability;
   }

   public double getWeight() {
      return this.weight;
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

   public int[] getSize() {
      int[] lowest = null;
      int[] highest = null;
      UnmodifiableIterator size = this.rooms.iterator();

      while(size.hasNext()) {
         MazeRoom room = (MazeRoom)size.next();

         for(int i = 0; i < room.getDimensions(); ++i) {
            if (lowest == null) {
               lowest = room.getCoordinates();
               highest = room.getCoordinates();
            } else if (room.getCoordinate(i) < lowest[i]) {
               lowest[i] = room.getCoordinate(i);
            } else if (room.getCoordinate(i) > highest[i]) {
               highest[i] = room.getCoordinate(i);
            }
         }
      }

      if (lowest == null) {
         throw new UnsupportedOperationException();
      } else {
         int[] size = IvVecMathHelper.sub(highest, new int[][]{lowest});

         for(int i = 0; i < size.length; ++i) {
            ++size[i];
         }

         return size;
      }
   }

   public String toString() {
      return "MazeComponentStructure{structureID='" + this.structureID + '\'' + ", variableDomain=" + this.variableDomain + ", transform=" + this.transform + '}';
   }
}
