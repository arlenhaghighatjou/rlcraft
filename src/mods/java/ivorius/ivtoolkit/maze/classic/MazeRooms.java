package ivorius.ivtoolkit.maze.classic;

import com.google.common.base.Function;
import net.minecraft.nbt.NBTTagIntArray;

public class MazeRooms {
   public MazeRooms() {
   }

   public static Function<MazeRoom, NBTTagIntArray> toNBT() {
      return (input) -> {
         return input.storeInNBT();
      };
   }

   public static Function<NBTTagIntArray, MazeRoom> fromNBT() {
      return (input) -> {
         return new MazeRoom(input);
      };
   }
}
