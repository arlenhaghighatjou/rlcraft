package ivorius.ivtoolkit.tools;

import com.google.common.collect.ImmutableMap.Builder;
import ivorius.ivtoolkit.lang.IvClasses;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTCompoundObjects {
   public NBTCompoundObjects() {
   }

   /** @deprecated */
   @Deprecated
   public static <T extends NBTCompoundObject> Function<NBTTagCompound, T> readFunction(Class<T> tClass) {
      return (input) -> {
         return read(input, tClass);
      };
   }

   /** @deprecated */
   @Deprecated
   public static Function<? extends NBTCompoundObject, NBTTagCompound> writeFunction() {
      return NBTCompoundObjects::write;
   }

   public static void writeTo(NBTTagCompound compound, String key, NBTCompoundObject compoundObject) {
      compound.setTag(key, write(compoundObject));
   }

   public static NBTTagCompound write(NBTCompoundObject compoundObject) {
      NBTTagCompound compound = new NBTTagCompound();
      compoundObject.writeToNBT(compound);
      return compound;
   }

   /** @deprecated */
   @Deprecated
   public static <T extends NBTCompoundObject> T readFrom(NBTTagCompound compound, String key, Class<? extends T> keyClass) {
      return (T)readFrom(compound, key, classSupplier(keyClass));
   }

   public static <T extends NBTCompoundObject> T readFrom(NBTTagCompound compound, String key, Supplier<? extends T> instantiator) {
      return (T)read(compound.getCompoundTag(key), instantiator);
   }

   /** @deprecated */
   @Deprecated
   public static <T extends NBTCompoundObject> T read(NBTTagCompound compound, Class<T> tClass) {
      return (T)read(compound, classSupplier(tClass));
   }

   public static <T extends NBTCompoundObject> T read(NBTTagCompound compound, Supplier<? extends T> instantiator) {
      T t = instantiator.get();
      if (t != null) {
         t.readFromNBT(compound);
      }

      return t;
   }

   public static void writeListTo(NBTTagCompound compound, String key, Iterable<? extends NBTCompoundObject> objects) {
      compound.setTag(key, writeList(objects));
   }

   public static NBTTagList writeList(Iterable<? extends NBTCompoundObject> objects) {
      NBTTagList tagList = new NBTTagList();

      for(NBTCompoundObject object : objects) {
         NBTTagCompound compound = new NBTTagCompound();
         object.writeToNBT(compound);
         tagList.appendTag(compound);
      }

      return tagList;
   }

   public static <T extends NBTCompoundObject> List<T> readListFrom(NBTTagCompound compound, String key, Supplier<? extends T> instantiator) {
      return readList(compound.getTagList(key, 10), instantiator);
   }

   /** @deprecated */
   @Deprecated
   public static <T extends NBTCompoundObject> List<T> readListFrom(NBTTagCompound compound, String key, Class<T> tClass) {
      return readList(compound.getTagList(key, 10), tClass);
   }

   /** @deprecated */
   @Deprecated
   public static <T extends NBTCompoundObject> List<T> readList(NBTTagList list, Class<T> tClass) {
      return readList(list, classSupplier(tClass));
   }

   public static <T extends NBTCompoundObject> List<T> readList(NBTTagList list, Supplier<? extends T> instantiator) {
      List<T> rList = new ArrayList(list.tagCount());

      for(int i = 0; i < list.tagCount(); ++i) {
         rList.add(read(list.getCompoundTagAt(i), instantiator));
      }

      return rList;
   }

   /** @deprecated */
   @Deprecated
   public static <K extends NBTCompoundObject, V extends NBTCompoundObject> Map<K, V> readMapFrom(NBTTagCompound compound, String key, Class<? extends K> keyClass, Class<? extends V> valueClass) {
      return readMapFrom(compound, key, classSupplier(keyClass), classSupplier(valueClass));
   }

   public static <K extends NBTCompoundObject, V extends NBTCompoundObject> Map<K, V> readMapFrom(NBTTagCompound compound, String key, Supplier<? extends K> keySupplier, Supplier<? extends V> valueSupplier) {
      return readMap(compound.getTagList(key, 10), keySupplier, valueSupplier);
   }

   /** @deprecated */
   @Deprecated
   public static <K extends NBTCompoundObject, V extends NBTCompoundObject> Map<K, V> readMap(NBTTagList nbt, Class<? extends K> keyClass, Class<? extends V> valueClass) {
      return readMap(nbt, classSupplier(keyClass), classSupplier(valueClass));
   }

   public static <K extends NBTCompoundObject, V extends NBTCompoundObject> Map<K, V> readMap(NBTTagList nbt, Supplier<? extends K> keyInstantiator, Supplier<? extends V> valueInstantiator) {
      Builder<K, V> map = new Builder();

      for(int i = 0; i < nbt.tagCount(); ++i) {
         NBTTagCompound compound = nbt.getCompoundTagAt(i);
         map.put(readFrom(compound, "key", keyInstantiator), readFrom(compound, "value", valueInstantiator));
      }

      return map.build();
   }

   public static <K extends NBTCompoundObject, V extends NBTCompoundObject> void writeMapTo(NBTTagCompound compound, String key, Map<K, V> map) {
      compound.setTag(key, writeMap(map));
   }

   public static <K extends NBTCompoundObject, V extends NBTCompoundObject> NBTTagList writeMap(Map<K, V> map) {
      NBTTagList nbt = new NBTTagList();

      for(Entry<K, V> entry : map.entrySet()) {
         NBTTagCompound compound = new NBTTagCompound();
         writeTo(compound, "key", (NBTCompoundObject)entry.getKey());
         writeTo(compound, "value", (NBTCompoundObject)entry.getValue());
         nbt.appendTag(compound);
      }

      return nbt;
   }

   private static <T extends NBTCompoundObject> Supplier<T> classSupplier(Class<T> tClass) {
      return () -> {
         return (NBTCompoundObject)IvClasses.instantiate(tClass);
      };
   }
}
