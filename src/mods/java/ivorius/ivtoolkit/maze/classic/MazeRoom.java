package ivorius.ivtoolkit.maze.classic;

import ivorius.ivtoolkit.math.IvVecMathHelper;
import java.util.Arrays;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagIntArray;

public class MazeRoom implements MazeCoordinate {
   @Nonnull
   private final int[] coordinates;

   public MazeRoom(@Nonnull int... coordinates) {
      this.coordinates = (int[])coordinates.clone();
   }

   public MazeRoom(NBTTagIntArray intArray) {
      this.coordinates = (int[])intArray.getIntArray().clone();
   }

   public int getDimensions() {
      return this.coordinates.length;
   }

   public int[] getCoordinates() {
      return (int[])this.coordinates.clone();
   }

   public int getCoordinate(int index) {
      return this.coordinates[index];
   }

   public MazeRoom add(MazeRoom room) {
      return new MazeRoom(IvVecMathHelper.add(this.coordinates, room.coordinates));
   }

   public MazeRoom addInDimension(int dimension, int count) {
      this.coordinates[dimension] += count;
      MazeRoom room = new MazeRoom(this.coordinates);
      this.coordinates[dimension] -= count;
      return room;
   }

   public MazeRoom sub(MazeRoom room) {
      return new MazeRoom(IvVecMathHelper.sub(this.coordinates, room.coordinates));
   }

   public MazeRoom subInDimension(int dimension, int count) {
      this.coordinates[dimension] -= count;
      MazeRoom room = new MazeRoom(this.coordinates);
      this.coordinates[dimension] += count;
      return room;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MazeRoom mazeRoom = (MazeRoom)o;
         return Arrays.equals(this.coordinates, mazeRoom.coordinates);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Arrays.hashCode(this.coordinates);
   }

   public String toString() {
      return Arrays.toString(this.coordinates);
   }

   public int[] getMazeCoordinates() {
      int[] coords = new int[this.coordinates.length];

      for(int i = 0; i < coords.length; ++i) {
         coords[i] = this.coordinates[i] * 2 + 1;
      }

      return coords;
   }

   public NBTTagIntArray storeInNBT() {
      return new NBTTagIntArray(this.getMazeCoordinates());
   }
}
