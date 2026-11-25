package ivorius.ivtoolkit.tools;

import java.util.function.Predicate;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTWalker {
   public NBTWalker() {
   }

   public static boolean walk(NBTBase tag, Visitor<NBTBase> consumer) {
      if (tag instanceof NBTTagCompound) {
         return walk((NBTTagCompound)tag, consumer);
      } else {
         return tag instanceof NBTTagList ? walk((NBTTagList)tag, consumer) : consumer.visit(tag);
      }
   }

   public static boolean walk(NBTTagCompound compound, Visitor<NBTBase> consumer) {
      return consumer.visit(compound) && compound.getKeySet().stream().allMatch((key) -> {
         return walk(compound.getTag(key), consumer);
      });
   }

   public static boolean walk(NBTTagList list, Visitor<NBTBase> consumer) {
      return consumer.visit(list) && NBTTagLists.nbtBases(list).stream().allMatch((nbt) -> {
         return walk(nbt, consumer);
      });
   }

   public static boolean walkCompounds(NBTBase tag, Visitor<NBTTagCompound> consumer) {
      if (tag instanceof NBTTagCompound) {
         return walkCompounds((NBTTagCompound)tag, consumer);
      } else {
         return tag instanceof NBTTagList ? walkCompounds((NBTTagList)tag, consumer) : true;
      }
   }

   public static boolean walkCompounds(NBTTagCompound compound, Visitor<NBTTagCompound> consumer) {
      return consumer.visit(compound) && compound.getKeySet().stream().allMatch((key) -> {
         return walkCompounds(compound.getTag(key), consumer);
      });
   }

   public static boolean walkCompounds(NBTTagList list, Visitor<NBTTagCompound> consumer) {
      return list.getTagType() != 10 || NBTTagLists.nbtBases(list).stream().allMatch((nbt) -> {
         return walkCompounds(nbt, consumer);
      });
   }
}
