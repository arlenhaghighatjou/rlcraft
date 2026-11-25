package ivorius.ivtoolkit.tools;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;

public class NBTTagLists {
   public NBTTagLists() {
   }

   public static List<NBTBase> nbtBases(NBTTagList nbt) {
      IntStream var10000 = IntStream.range(0, nbt.tagCount());
      nbt.getClass();
      return (List)var10000.mapToObj(nbt::get).collect(Collectors.toList());
   }

   public static void writeTo(NBTTagCompound compound, String key, List<? extends NBTBase> lists) {
      compound.setTag(key, write(lists));
   }

   public static NBTTagList write(List<? extends NBTBase> lists) {
      NBTTagList list = new NBTTagList();
      lists.forEach(list::appendTag);
      return list;
   }

   public static List<NBTTagCompound> compoundsFrom(NBTTagCompound compound, String key) {
      return compounds(compound.getTagList(key, 10));
   }

   public static List<NBTTagCompound> compounds(NBTTagList nbt) {
      IntStream var10000 = IntStream.range(0, nbt.tagCount());
      nbt.getClass();
      return (List)var10000.mapToObj(nbt::getCompoundTagAt).collect(Collectors.toList());
   }

   /** @deprecated */
   @Deprecated
   public static void writeCompoundsTo(NBTTagCompound compound, String key, List<NBTTagCompound> list) {
      compound.setTag(key, writeCompounds(list));
   }

   /** @deprecated */
   @Deprecated
   public static NBTTagList writeCompounds(List<NBTTagCompound> list) {
      return write(list);
   }

   public static List<int[]> intArraysFrom(NBTTagCompound compound, String key) {
      return intArrays(compound.getTagList(key, 11));
   }

   public static List<int[]> intArrays(NBTTagList nbt) {
      IntStream var10000 = IntStream.range(0, nbt.tagCount());
      nbt.getClass();
      return (List)var10000.mapToObj(nbt::getIntArrayAt).collect(Collectors.toList());
   }

   public static void writeIntArraysTo(NBTTagCompound compound, String key, List<int[]> list) {
      compound.setTag(key, writeIntArrays(list));
   }

   public static NBTTagList writeIntArrays(List<int[]> list) {
      NBTTagList tagList = new NBTTagList();
      list.forEach((array) -> {
         tagList.appendTag(new NBTTagIntArray(array));
      });
      return tagList;
   }

   public static List<NBTTagList> listsFrom(NBTTagCompound compound, String key) {
      return lists(compound.getTagList(key, 9));
   }

   public static List<NBTTagList> lists(NBTTagList nbt) {
      return (List)IntStream.range(0, nbt.tagCount()).mapToObj((i) -> {
         return (List<NBTTagList>)(nbt.get(i).getId() == 9 ? nbt.get(i) : new NBTTagList());
      }).collect(Collectors.toList());
   }
}
