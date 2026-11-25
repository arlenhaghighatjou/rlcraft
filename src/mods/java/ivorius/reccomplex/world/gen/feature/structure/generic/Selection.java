package ivorius.reccomplex.world.gen.feature.structure.generic;

import com.google.gson.annotations.SerializedName;
import ivorius.ivtoolkit.gui.IntegerRange;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.tools.IvNBTHelper;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.ivtoolkit.tools.NBTTagLists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;

public class Selection extends ArrayList<Selection.Area> implements NBTCompoundObject {
   public final int dimensions;

   public Selection(int dimensions) {
      this.dimensions = dimensions;
   }

   public Selection(int dimensions, Collection<? extends Selection.Area> c) {
      super(c);
      this.dimensions = dimensions;
   }

   private static void mergeRooms(boolean additive, int dimIndex, int[] min, int[] max, int[] position, String id, Map<MazeRoom, String> rooms) {
      for(int i = min[dimIndex]; i <= max[dimIndex]; ++i) {
         position[dimIndex] = i;
         if (dimIndex == position.length - 1) {
            if (additive) {
               rooms.put(new MazeRoom(position), id);
            } else {
               rooms.remove(new MazeRoom(position));
            }
         } else {
            mergeRooms(additive, dimIndex + 1, min, max, position, id, rooms);
         }
      }

   }

   public static Selection zeroSelection(int dimensions) {
      Selection selection = new Selection(dimensions);
      selection.add(new Selection.Area(true, new int[dimensions], new int[dimensions]));
      return selection;
   }

   @Nullable
   public static List<IntegerRange> toRanges(int[] lower, int[] higher) {
      return lower != null ? (List)IntStream.range(0, lower.length).mapToObj((i) -> {
         return new IntegerRange(lower[i], higher[i]);
      }).collect(Collectors.toList()) : null;
   }

   public Map<MazeRoom, String> compile(boolean additive) {
      if (!additive) {
         Map<MazeRoom, String> spaces = new HashMap();
         int[] min = this.boundsLower();
         int[] max = this.boundsHigher();
         mergeRooms(true, 0, min, max, (int[])min.clone(), (String)null, spaces);

         for(Selection.Area area : this) {
            mergeRooms(!area.additive, 0, area.minCoord, area.maxCoord, (int[])area.minCoord.clone(), area.identifier, spaces);
         }

         return spaces;
      } else {
         Map<MazeRoom, String> rooms = new HashMap();

         for(Selection.Area area : this) {
            mergeRooms(area.additive, 0, area.minCoord, area.maxCoord, (int[])area.minCoord.clone(), area.identifier, rooms);
         }

         return rooms;
      }
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.clear();
      NBTTagLists.compoundsFrom(compound, "areas").forEach((cmp) -> {
         this.add(new Selection.Area(cmp, this.dimensions));
      });
   }

   public void writeToNBT(NBTTagCompound compound) {
      NBTTagLists.writeTo(compound, "areas", (List)this.stream().map(Selection.Area::writeToNBT).collect(Collectors.toList()));
   }

   @Nullable
   public List<IntegerRange> bounds() {
      return toRanges(this.boundsLower(), this.boundsHigher());
   }

   @Nullable
   public int[] boundsLower() {
      int[] min = null;

      for(Selection.Area area : this) {
         if (min == null) {
            min = (int[])area.minCoord.clone();
         } else {
            for(int d = 0; d < area.minCoord.length; ++d) {
               min[d] = Math.min(min[d], area.minCoord[d]);
            }
         }
      }

      return min;
   }

   @Nullable
   public int[] boundsHigher() {
      int[] max = null;

      for(Selection.Area area : this) {
         if (max == null) {
            max = (int[])area.maxCoord.clone();
         } else {
            for(int d = 0; d < area.maxCoord.length; ++d) {
               max[d] = Math.max(max[d], area.maxCoord[d]);
            }
         }
      }

      return max;
   }

   public int[] boundsSize() {
      int[] min = this.boundsLower();
      int[] max = this.boundsHigher();

      for(int i = 0; i < max.length; ++i) {
         max[i] += 1 - min[i];
      }

      return max;
   }

   public Selection copy() {
      Selection selection = new Selection(this.dimensions);
      selection.addAll((Collection)this.stream().map(Selection.Area::copy).collect(Collectors.toList()));
      return selection;
   }

   public void transform(AxisAlignedTransform2D transform, int[] size, int centerCorrection) {
      this.forEach((area) -> {
         area.transform(transform, size, centerCorrection);
      });
   }

   public static class Area {
      @SerializedName("additive")
      private boolean additive;
      @SerializedName("minCoord")
      private int[] minCoord;
      @SerializedName("maxCoord")
      private int[] maxCoord;
      @Nullable
      @SerializedName("identifier")
      private String identifier;

      public Area(boolean additive, int[] minCoord, int[] maxCoord) {
         this.additive = additive;
         this.minCoord = minCoord;
         this.maxCoord = maxCoord;
         if (minCoord.length != maxCoord.length) {
            throw new IllegalArgumentException();
         }
      }

      public Area(boolean additive, int[] minCoord, int[] maxCoord, @Nullable String identifier) {
         this.additive = additive;
         this.minCoord = minCoord;
         this.maxCoord = maxCoord;
         this.identifier = identifier;
         if (minCoord.length != maxCoord.length) {
            throw new IllegalArgumentException();
         }
      }

      public Area(NBTTagCompound tagCompound, int dimensions) {
         this.additive = tagCompound.getBoolean("additive");
         this.minCoord = IvNBTHelper.readIntArrayFixedSize("min", dimensions, tagCompound);
         this.maxCoord = IvNBTHelper.readIntArrayFixedSize("max", dimensions, tagCompound);
         this.identifier = tagCompound.hasKey("identifier", 8) ? tagCompound.getString("identifier") : null;
      }

      public static Selection.Area from(boolean additive, int[] left, int[] right, @Nullable String identifier) {
         int[] min = new int[left.length];
         int[] max = new int[right.length];

         for(int i = 0; i < left.length; ++i) {
            min[i] = Math.min(left[i], right[i]);
            max[i] = Math.max(left[i], right[i]);
         }

         return new Selection.Area(additive, min, max, identifier);
      }

      public NBTTagCompound writeToNBT() {
         NBTTagCompound compound = new NBTTagCompound();
         compound.setBoolean("additive", this.additive);
         compound.setIntArray("min", this.minCoord);
         compound.setIntArray("max", this.maxCoord);
         if (this.identifier != null) {
            compound.setString("identifier", this.identifier);
         }

         return compound;
      }

      public int[] getMinCoord() {
         return (int[])this.minCoord.clone();
      }

      public void setMinCoord(int[] minCoord) {
         this.minCoord = (int[])minCoord.clone();
      }

      public int[] getMaxCoord() {
         return (int[])this.maxCoord.clone();
      }

      public void setMaxCoord(int[] maxCoord) {
         this.maxCoord = (int[])maxCoord.clone();
      }

      public void setCoord(int dim, int min, int max) {
         this.minCoord[dim] = min;
         this.maxCoord[dim] = max;
      }

      public boolean isAdditive() {
         return this.additive;
      }

      public void setAdditive(boolean additive) {
         this.additive = additive;
      }

      @Nullable
      public String getIdentifier() {
         return this.identifier;
      }

      public void setIdentifier(@Nullable String identifier) {
         this.identifier = identifier;
      }

      public Selection.Area copy() {
         return new Selection.Area(this.additive, (int[])this.minCoord.clone(), (int[])this.maxCoord.clone(), this.identifier);
      }

      public void transform(AxisAlignedTransform2D transform, int[] size, int centerCorrection) {
         transform.applyOn(this.minCoord, size, centerCorrection);
         transform.applyOn(this.maxCoord, size, centerCorrection);

         for(int i = 0; i < this.minCoord.length; ++i) {
            int min = Math.min(this.minCoord[i], this.maxCoord[i]);
            int max = Math.max(this.minCoord[i], this.maxCoord[i]);
            this.minCoord[i] = min;
            this.maxCoord[i] = max;
         }

      }

      public boolean includes(Selection.Area area) {
         for(int i = 0; i < this.minCoord.length; ++i) {
            if (this.minCoord[i] > area.minCoord[i] || this.maxCoord[i] < area.maxCoord[i]) {
               return false;
            }
         }

         return true;
      }

      @Nullable
      public Selection.Area intersection(Selection.Area area) {
         if (area.minCoord.length != this.minCoord.length) {
            throw new IllegalArgumentException();
         } else {
            int[] min = new int[this.minCoord.length];
            int[] max = new int[this.minCoord.length];

            for(int i = 0; i < min.length; ++i) {
               min[i] = Math.max(min[i], area.minCoord[i]);
               max[i] = Math.min(max[i], area.maxCoord[i]);
               if (min[i] > max[i]) {
                  return null;
               }
            }

            return new Selection.Area(this.additive, min, max);
         }
      }
   }
}
