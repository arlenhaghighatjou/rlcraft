package ivorius.ivtoolkit.maze.classic;

import com.google.common.base.Function;
import net.minecraft.nbt.NBTTagCompound;

public class MazePaths {
   public MazePaths() {
   }

   public static Function<MazePath, NBTTagCompound> toNBT() {
      return (input) -> {
         return input.storeInNBT();
      };
   }

   public static Function<NBTTagCompound, MazePath> fromNBT() {
      return (input) -> {
         return new MazePath(input);
      };
   }
}
