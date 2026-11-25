package ivorius.ivtoolkit.maze.classic;

import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;

public class MazePath implements MazeCoordinate {
   @Nonnull
   private final MazeRoom sourceRoom;
   private final int pathDimension;
   private MazeRoom destRoomCache;

   public MazePath(int pathDimension, @Nonnull MazeRoom sourceRoom) {
      this.sourceRoom = sourceRoom;
      this.pathDimension = pathDimension;
   }

   public MazePath(int pathDimension, @Nonnull int... roomCoordinates) {
      this(pathDimension, new MazeRoom((int[])roomCoordinates.clone()));
   }

   public MazePath(NBTTagCompound compound) {
      this.sourceRoom = new MazeRoom(compound.getIntArray("source"));
      this.pathDimension = compound.getInteger("pathDimension");
   }

   public static MazePath fromRoom(int pathDimension, MazeRoom sourceRoom, boolean pathGoesUp) {
      return new MazePath(pathDimension, pathGoesUp ? sourceRoom : sourceRoom.subInDimension(pathDimension, 1));
   }

   public static MazePath fromConnection(MazeRoom source, MazeRoom dest) {
      return fromConnection(source.getCoordinates(), dest.getCoordinates());
   }

   public static MazePath fromConnection(int[] source, int[] dest) {
      for(int i = 0; i < source.length; ++i) {
         if (source[i] != dest[i]) {
            return new MazePath(i, source[i] < dest[i] ? source : dest);
         }
      }

      return null;
   }

   public int getPathDimension() {
      return this.pathDimension;
   }

   public int[] getPathDirection() {
      int[] direction = new int[this.sourceRoom.getDimensions()];
      direction[this.pathDimension] = 1;
      return direction;
   }

   public MazeRoom getSourceRoom() {
      return this.sourceRoom;
   }

   public MazeRoom getDestinationRoom() {
      return this.destRoomCache != null ? this.destRoomCache : (this.destRoomCache = this.sourceRoom.addInDimension(this.pathDimension, 1));
   }

   public MazePath add(MazeRoom room) {
      return new MazePath(this.pathDimension, this.sourceRoom.add(room));
   }

   public MazePath sub(MazeRoom room) {
      return new MazePath(this.pathDimension, this.sourceRoom.sub(room));
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MazePath mazePath = (MazePath)o;
         return this.pathDimension == mazePath.pathDimension && this.sourceRoom.equals(mazePath.sourceRoom);
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.sourceRoom.hashCode();
      result = 31 * result + this.pathDimension;
      return result;
   }

   public String toString() {
      return String.format("%s <-> %s", this.getSourceRoom(), this.getDestinationRoom());
   }

   public int[] getMazeCoordinates() {
      int[] coords = this.sourceRoom.getMazeCoordinates();
      ++coords[this.pathDimension];
      return coords;
   }

   public NBTTagCompound storeInNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setIntArray("source", this.sourceRoom.getCoordinates());
      compound.setInteger("pathDimension", this.pathDimension);
      return compound;
   }
}
