package ivorius.reccomplex.world.gen.feature.structure.generic.maze;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.maze.components.MazePassage;
import ivorius.ivtoolkit.maze.components.MazeRoom;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.reccomplex.json.JsonUtils;
import java.lang.reflect.Type;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class SavedMazePath implements NBTCompoundObject, Comparable<SavedMazePath> {
   public int pathDimension;
   public MazeRoom sourceRoom;
   public boolean pathGoesUp;

   public SavedMazePath() {
   }

   public SavedMazePath(int pathDimension, MazeRoom sourceRoom, boolean pathGoesUp) {
      this.pathDimension = pathDimension;
      this.sourceRoom = sourceRoom;
      this.pathGoesUp = pathGoesUp;
   }

   public void set(SavedMazePath path) {
      this.pathDimension = path.pathDimension;
      this.sourceRoom = path.sourceRoom;
      this.pathGoesUp = path.pathGoesUp;
   }

   public MazePassage build() {
      return new MazePassage(this.getSourceRoom(), this.getDestRoom());
   }

   public MazeRoom getSourceRoom() {
      return this.sourceRoom;
   }

   public MazeRoom getDestRoom() {
      return this.sourceRoom.addInDimension(this.pathDimension, this.pathGoesUp ? 1 : -1);
   }

   public SavedMazePath inverse() {
      return new SavedMazePath(this.pathDimension, this.sourceRoom.addInDimension(this.pathDimension, this.pathGoesUp ? 1 : -1), !this.pathGoesUp);
   }

   public SavedMazePath copy() {
      return new SavedMazePath(this.pathDimension, this.sourceRoom, this.pathGoesUp);
   }

   public void readFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("source", 10)) {
         this.sourceRoom = new MazeRoom(compound.getCompoundTag("source").getIntArray("coordinates"));
      } else {
         this.sourceRoom = new MazeRoom(compound.getIntArray("source"));
      }

      this.pathDimension = compound.getInteger("pathDimension");
      this.pathGoesUp = compound.getBoolean("pathGoesUp");
   }

   public void writeToNBT(NBTTagCompound compound) {
      compound.setTag("source", this.sourceRoom.storeInNBT());
      compound.setInteger("pathDimension", this.pathDimension);
      compound.setBoolean("pathGoesUp", this.pathGoesUp);
   }

   public String toString() {
      return String.format("%s %s", this.sourceRoom, this.getEnumFacing());
   }

   public EnumFacing getEnumFacing() {
      switch(this.pathDimension) {
      case 0:
         return this.pathGoesUp ? EnumFacing.EAST : EnumFacing.WEST;
      case 1:
         return this.pathGoesUp ? EnumFacing.UP : EnumFacing.DOWN;
      case 2:
         return this.pathGoesUp ? EnumFacing.SOUTH : EnumFacing.NORTH;
      default:
         throw new InternalError();
      }
   }

   public int compareTo(@Nonnull SavedMazePath o) {
      return this.toString().compareTo(o.toString());
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SavedMazePath that = (SavedMazePath)o;
         if (this.pathDimension != that.pathDimension) {
            return false;
         } else if (this.pathGoesUp != that.pathGoesUp) {
            return false;
         } else {
            return this.sourceRoom != null ? this.sourceRoom.equals(that.sourceRoom) : that.sourceRoom == null;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.pathDimension;
      result = 31 * result + (this.sourceRoom != null ? this.sourceRoom.hashCode() : 0);
      result = 31 * result + (this.pathGoesUp ? 1 : 0);
      return result;
   }

   public static class Serializer implements JsonSerializer<SavedMazePath>, JsonDeserializer<SavedMazePath> {
      public Serializer() {
      }

      public SavedMazePath deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject jsonObject = JsonUtils.asJsonObject(json, "MazeRoom");
         MazeRoom src = (MazeRoom)context.deserialize(jsonObject.get("source"), MazeRoom.class);
         int pathDimension = JsonUtils.getInt(jsonObject, "pathDimension");
         boolean pathGoesUp = JsonUtils.getBoolean(jsonObject, "pathGoesUp");
         return new SavedMazePath(pathDimension, src, pathGoesUp);
      }

      public JsonElement serialize(SavedMazePath src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.add("source", context.serialize(src.sourceRoom));
         jsonObject.addProperty("pathDimension", Integer.valueOf(src.pathDimension));
         jsonObject.addProperty("pathGoesUp", Boolean.valueOf(src.pathGoesUp));
         return jsonObject;
      }
   }
}
