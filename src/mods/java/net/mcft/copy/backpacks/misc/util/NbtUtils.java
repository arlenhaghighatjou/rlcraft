package net.mcft.copy.backpacks.misc.util;

import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.stream.Collector.Characteristics;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.INBTSerializable;

public final class NbtUtils {
   public static final String TAG_INDEX = "index";
   public static final String TAG_STACK = "stack";

   private NbtUtils() {
   }

   public static NBTBase get(ItemStack stack, String... tags) {
      return get(stack.getTagCompound(), tags);
   }

   public static NBTBase get(NBTTagCompound compound, String... tags) {
      if (compound == null) {
         return null;
      } else {
         String tag = null;

         for(int i = 0; i < tags.length; ++i) {
            tag = tags[i];
            if (!compound.hasKey(tag)) {
               return null;
            }

            if (i == tags.length - 1) {
               break;
            }

            compound = compound.getCompoundTag(tag);
         }

         return compound.getTag(tag);
      }
   }

   public static <T> T get(ItemStack stack, T defaultValue, String... tags) {
      return (T)get(stack.getTagCompound(), defaultValue, tags);
   }

   public static <T> T get(NBTTagCompound compound, T defaultValue, String... tags) {
      NBTBase tag = get(compound, tags);
      return (T)(tag != null ? getTagValue(tag) : defaultValue);
   }

   public static boolean has(ItemStack stack, String... tags) {
      return has(stack.getTagCompound(), tags);
   }

   public static boolean has(NBTTagCompound compound, String... tags) {
      return get(compound, tags) != null;
   }

   public static void set(ItemStack stack, NBTBase nbtTag, String... tags) {
      if (stack.isEmpty()) {
         throw new IllegalArgumentException("stack is empty");
      } else {
         NBTTagCompound compound = stack.getTagCompound();
         if (compound == null) {
            stack.setTagCompound(compound = new NBTTagCompound());
         }

         set(compound, nbtTag, tags);
      }
   }

   public static void set(NBTTagCompound compound, NBTBase nbtTag, String... tags) {
      if (compound == null) {
         throw new IllegalArgumentException("compound is null");
      } else {
         String tag = null;

         for(int i = 0; i < tags.length; ++i) {
            tag = tags[i];
            if (i == tags.length - 1) {
               break;
            }

            if (!compound.hasKey(tag)) {
               NBTTagCompound child = new NBTTagCompound();
               compound.setTag(tag, child);
               compound = child;
            } else {
               compound = compound.getCompoundTag(tag);
            }
         }

         compound.setTag(tag, nbtTag);
      }
   }

   public static <T> void set(ItemStack stack, T value, String... tags) {
      set(stack, createTag(value), tags);
   }

   public static <T> void set(NBTTagCompound compound, T value, String... tags) {
      set(compound, createTag(value), tags);
   }

   public static void remove(ItemStack stack, String... tags) {
      if (tags.length == 0) {
         throw new IllegalArgumentException("tags should have at least one element");
      } else if (stack.hasTagCompound()) {
         NBTTagCompound compound = stack.getTagCompound();
         remove(compound, tags);
         if (compound.hasNoTags()) {
            stack.setTagCompound((NBTTagCompound)null);
         }

      }
   }

   public static void remove(NBTTagCompound compound, String... tags) {
      if (tags.length == 0) {
         throw new IllegalArgumentException("tags should have at least one element");
      } else if (compound != null) {
         if (tags.length > 1) {
            NBTBase tag = compound.getTag(tags[0]);
            if (!(tag instanceof NBTTagCompound)) {
               return;
            }

            NBTTagCompound subCompound = (NBTTagCompound)tag;
            remove(subCompound, (String[])Arrays.copyOfRange(tags, 1, tags.length));
            if (!subCompound.hasNoTags()) {
               return;
            }
         }

         compound.removeTag(tags[0]);
      }
   }

   public static NBTTagCompound createCompound(Object... nameValuePairs) {
      return addToCompound(new NBTTagCompound(), nameValuePairs);
   }

   public static NBTTagCompound add(ItemStack stack, Object... nameValuePairs) {
      if (stack.isEmpty()) {
         throw new IllegalArgumentException("stack is empty");
      } else {
         if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
         }

         return addToCompound(stack.getTagCompound(), nameValuePairs);
      }
   }

   public static NBTTagCompound addToCompound(NBTTagCompound compound, Object... nameValuePairs) {
      if (compound == null) {
         throw new IllegalArgumentException("compound is null");
      } else {
         for(int i = 0; i < nameValuePairs.length; i += 2) {
            String name = (String)nameValuePairs[i];
            Object value = nameValuePairs[i + 1];
            if (value != null) {
               compound.setTag(name, createTag(value));
            }
         }

         return compound;
      }
   }

   public static NBTTagList createList(Object... values) {
      return addToList(new NBTTagList(), values);
   }

   public static NBTTagList addToList(NBTTagList list, Object... values) {
      if (list == null) {
         throw new IllegalArgumentException("list is null");
      } else {
         for(Object value : values) {
            if (value != null) {
               list.appendTag(createTag(value));
            }
         }

         return list;
      }
   }

   public static NBTTagCompound writeItem(ItemStack item) {
      return writeItem(item, true);
   }

   public static NBTTagCompound writeItem(ItemStack item, boolean writeNullAsEmptyCompound) {
      return !item.isEmpty() ? item.writeToNBT(new NBTTagCompound()) : (writeNullAsEmptyCompound ? new NBTTagCompound() : null);
   }

   public static ItemStack readItem(NBTTagCompound compound) {
      return compound != null && !compound.hasNoTags() ? new ItemStack(compound) : ItemStack.EMPTY;
   }

   public static NBTTagList writeItems(ItemStack[] items) {
      NBTTagList list = new NBTTagList();

      for(int i = 0; i < items.length; ++i) {
         if (items[i] != null) {
            list.appendTag(createCompound("index", (short)i, "stack", writeItem(items[i])));
         }
      }

      return list;
   }

   public static ItemStack[] readItems(NBTTagList list, ItemStack[] items) {
      return readItems(list, items, (List)null);
   }

   public static ItemStack[] readItems(NBTTagList list, ItemStack[] items, List<ItemStack> invalid) {
      for(int i = 0; i < list.tagCount(); ++i) {
         NBTTagCompound compound = list.getCompoundTagAt(i);
         int index = compound.getShort("index");
         ItemStack stack = readItem(compound.getCompoundTag("stack"));
         if (index < 0 && index >= items.length) {
            if (invalid != null) {
               invalid.add(stack);
            }
         } else {
            items[index] = stack;
         }
      }

      return items;
   }

   public static <T> T getTagValue(NBTBase tag) {
      if (tag == null) {
         throw new IllegalArgumentException("tag is null");
      } else if (tag instanceof NBTTagByte) {
         return (T)Byte.valueOf(((NBTTagByte)tag).getByte());
      } else if (tag instanceof NBTTagShort) {
         return (T)Short.valueOf(((NBTTagShort)tag).getShort());
      } else if (tag instanceof NBTTagInt) {
         return (T)Integer.valueOf(((NBTTagInt)tag).getInt());
      } else if (tag instanceof NBTTagLong) {
         return (T)Long.valueOf(((NBTTagLong)tag).getLong());
      } else if (tag instanceof NBTTagFloat) {
         return (T)Float.valueOf(((NBTTagFloat)tag).getFloat());
      } else if (tag instanceof NBTTagDouble) {
         return (T)Double.valueOf(((NBTTagDouble)tag).getDouble());
      } else if (tag instanceof NBTTagString) {
         return (T)((NBTTagString)tag).getString();
      } else if (tag instanceof NBTTagByteArray) {
         return (T)((NBTTagByteArray)tag).getByteArray();
      } else if (tag instanceof NBTTagIntArray) {
         return (T)((NBTTagIntArray)tag).getIntArray();
      } else {
         throw new IllegalArgumentException(NBTBase.NBT_TYPES[tag.getId()] + " isn't a primitive NBT tag");
      }
   }

   public static NBTBase createTag(Object value) {
      if (value == null) {
         throw new IllegalArgumentException("value is null");
      } else if (value instanceof NBTBase) {
         return (NBTBase)value;
      } else if (value instanceof INBTSerializable) {
         return ((INBTSerializable)value).serializeNBT();
      } else if (value instanceof Collection) {
         return (NBTBase)((Collection)value).stream().map(NbtUtils::createTag).collect(toList());
      } else if (value instanceof Byte) {
         return new NBTTagByte(((Byte)value).byteValue());
      } else if (value instanceof Short) {
         return new NBTTagShort(((Short)value).shortValue());
      } else if (value instanceof Integer) {
         return new NBTTagInt(((Integer)value).intValue());
      } else if (value instanceof Long) {
         return new NBTTagLong(((Long)value).longValue());
      } else if (value instanceof Float) {
         return new NBTTagFloat(((Float)value).floatValue());
      } else if (value instanceof Double) {
         return new NBTTagDouble(((Double)value).doubleValue());
      } else if (value instanceof String) {
         return new NBTTagString((String)value);
      } else if (value instanceof byte[]) {
         return new NBTTagByteArray((byte[])value);
      } else if (value instanceof int[]) {
         return new NBTTagIntArray((int[])value);
      } else {
         throw new IllegalArgumentException("Can't create an NBT tag of value: " + value);
      }
   }

   public static <N extends NBTBase, T extends INBTSerializable<N>> T getTagValue(N tag, T value) {
      if (tag == null) {
         throw new IllegalArgumentException("tag is null");
      } else if (value == null) {
         throw new IllegalArgumentException("value is null");
      } else {
         value.deserializeNBT(tag);
         return value;
      }
   }

   public static <N extends NBTBase, T extends INBTSerializable<N>> List<T> getTagList(NBTTagList list, Supplier<T> valueSupplier) {
      return (List)stream(list).map((tag) -> {
         return getTagValue(tag, (INBTSerializable)valueSupplier.get());
      }).collect(Collectors.toList());
   }

   public static <T extends NBTBase> Iterable<T> iterate(final NBTTagList list) {
      return new Iterable<T>() {
         public Iterator<T> iterator() {
            return new Iterator<T>() {
               private int _index = 0;

               public boolean hasNext() {
                  return this._index < list.tagCount();
               }

               public T next() {
                  return (T)list.get(this._index++);
               }
            };
         }
      };
   }

   public static Iterable<NbtUtils.CompoundEntry> iterate(NBTTagCompound compound) {
      return Iterables.transform(compound.getKeySet(), (key) -> {
         return new NbtUtils.CompoundEntry(key, compound.getTag(key));
      });
   }

   public static <T extends NBTBase> Stream<T> stream(NBTTagList list) {
      return StreamSupport.stream(NbtUtils.<T>iterate(list).spliterator(), false);
   }

   public static Stream<NbtUtils.CompoundEntry> stream(NBTTagCompound compound) {
      return StreamSupport.stream(iterate(compound).spliterator(), false);
   }

   public static <T> Collector<T, NBTTagList, NBTTagList> toList() {
      return Collector.of(NBTTagList::new, (list, element) -> {
         list.appendTag(createTag(element));
      }, (left, right) -> {
         for(NBTBase tag : iterate(right)) {
            left.appendTag(tag);
         }

         return left;
      }, Characteristics.IDENTITY_FINISH);
   }

   public static <T> Collector<T, NBTTagCompound, NBTTagCompound> toCompound(Function<T, String> keyMapper, Function<T, NBTBase> tagMapper) {
      return Collector.of(NBTTagCompound::new, (compound, element) -> {
         compound.setTag((String)keyMapper.apply(element), (NBTBase)tagMapper.apply(element));
      }, (left, right) -> {
         for(String key : right.getKeySet()) {
            left.setTag(key, right.getTag(key));
         }

         return left;
      }, Characteristics.IDENTITY_FINISH);
   }

   public static class CompoundEntry {
      public final String key;
      public final NBTBase tag;

      public CompoundEntry(String key, NBTBase tag) {
         this.key = key;
         this.tag = tag;
      }
   }

   public static final class NbtType {
      public static final int END = 0;
      public static final int BYTE = 1;
      public static final int SHORT = 2;
      public static final int INT = 3;
      public static final int LONG = 4;
      public static final int FLOAT = 5;
      public static final int DOUBLE = 6;
      public static final int BYTE_ARRAY = 7;
      public static final int STRING = 8;
      public static final int LIST = 9;
      public static final int COMPOUND = 10;
      public static final int INT_ARRAY = 11;

      private NbtType() {
      }
   }
}
