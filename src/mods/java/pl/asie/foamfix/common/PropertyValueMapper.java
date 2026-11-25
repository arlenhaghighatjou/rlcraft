package pl.asie.foamfix.common;

import com.google.common.collect.Lists;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.MathHelper;
import pl.asie.foamfix.util.HashingStrategies;

public class PropertyValueMapper {
   private static final Comparator<? super IProperty<?>> COMPARATOR_BIT_FITNESS = (first, second) -> {
      int diff1 = getPropertyEntry(first).bitSize - first.getAllowedValues().size();
      int diff2 = getPropertyEntry(second).bitSize - second.getAllowedValues().size();
      return diff1 == diff2 ? first.getName().compareTo(second.getName()) : diff1 - diff2;
   };
   private static final Map<IProperty<?>, PropertyValueMapper.Entry> entryMap = new IdentityHashMap();
   private static final int MAX_BIT_POS = 31;
   private final PropertyValueMapper.Entry[] entryList;
   private final TObjectIntMap<String> entryPositionMap;
   private final IBlockState[] stateMap;

   public PropertyValueMapper(BlockStateContainer container) {
      Collection<IProperty<?>> properties = container.getProperties();
      this.entryList = new PropertyValueMapper.Entry[properties.size()];
      List<IProperty<?>> propertiesSortedFitness = Lists.newArrayList(properties);
      propertiesSortedFitness.sort(COMPARATOR_BIT_FITNESS);
      int i = 0;

      for(IProperty<?> p : propertiesSortedFitness) {
         this.entryList[i++] = getPropertyEntry(p);
      }

      this.entryPositionMap = new TObjectIntHashMap(10, 0.5F, -1);
      int bitPos = 0;
      PropertyValueMapper.Entry lastEntry = null;

      for(PropertyValueMapper.Entry ee : this.entryList) {
         this.entryPositionMap.put(ee.property.getName(), bitPos);
         bitPos += ee.bits;
         lastEntry = ee;
      }

      if (bitPos <= 31) {
         if (lastEntry == null) {
            this.stateMap = new IBlockState[1 << bitPos];
         } else {
            this.stateMap = new IBlockState[(1 << bitPos - lastEntry.bits) * lastEntry.property.getAllowedValues().size()];
         }
      } else {
         this.stateMap = null;
      }

   }

   public boolean isValid() {
      return this.stateMap != null;
   }

   protected static PropertyValueMapper.Entry getPropertyEntry(IProperty property) {
      PropertyValueMapper.Entry e = (PropertyValueMapper.Entry)entryMap.get(property);
      if (e == null) {
         Class<?> propertyClass = property.getClass();
         if (propertyClass == PropertyInteger.class) {
            e = PropertyValueMapper.IntegerEntry.create((PropertyInteger)property);
         } else if (propertyClass == PropertyBool.class && property.getAllowedValues().size() == 2) {
            e = new PropertyValueMapper.BooleanEntry(property);
         } else if (propertyClass != PropertyEnum.class && propertyClass != PropertyDirection.class) {
            e = new PropertyValueMapper.ObjectEntry(property, false);
         } else {
            e = PropertyValueMapper.EnumEntrySorted.create((PropertyEnum)property);
         }

         entryMap.put(property, e);
      }

      return e;
   }

   protected int generateValue(IBlockState state) {
      int bitPos = 0;
      int value = 0;

      for(PropertyValueMapper.Entry e : this.entryList) {
         value |= e.get(state.getValue(e.property)) << bitPos;
         bitPos += e.bits;
      }

      this.stateMap[value] = state;
      return value;
   }

   public <T extends Comparable<T>, V extends T> IBlockState withProperty(int value, IProperty<T> property, V propertyValue) {
      int bitPos = this.entryPositionMap.get(property.getName());
      if (bitPos >= 0) {
         PropertyValueMapper.Entry e = getPropertyEntry(property);
         int nv = e.get(propertyValue);
         if (nv < 0) {
            return null;
         } else {
            int bitMask = e.bitSize - 1;
            value = value & ~(bitMask << bitPos) | nv << bitPos;
            return this.stateMap[value];
         }
      } else {
         return null;
      }
   }

   public IBlockState getPropertyByValue(int value) {
      return this.stateMap[value];
   }

   public <T extends Comparable<T>, V extends T> int withPropertyValue(int value, IProperty<T> property, V propertyValue) {
      int bitPos = this.entryPositionMap.get(property.getName());
      if (bitPos >= 0) {
         PropertyValueMapper.Entry e = getPropertyEntry(property);
         int nv = e.get(propertyValue);
         if (nv < 0) {
            return -1;
         } else {
            int bitMask = e.bitSize - 1;
            value = value & ~(bitMask << bitPos) | nv << bitPos;
            return value;
         }
      } else {
         return -1;
      }
   }

   public static class BooleanEntry extends PropertyValueMapper.Entry {
      private BooleanEntry(IProperty property) {
      }

      public int get(Object v) {
         return v == Boolean.TRUE ? 1 : 0;
      }
   }

   public abstract static class Entry {
      private final IProperty property;
      private final int bitSize;
      private final int bits;

      private Entry(IProperty property) {
         this.property = property;
         this.bitSize = MathHelper.smallestEncompassingPowerOfTwo(property.getAllowedValues().size());
         int bits = 0;

         for(int b = this.bitSize - 1; b != 0; b >>= 1) {
            ++bits;
         }

         this.bits = bits;
      }

      public abstract int get(Object var1);

      public boolean equals(Object other) {
         return !(other instanceof PropertyValueMapper.Entry) ? false : ((PropertyValueMapper.Entry)other).property.equals(this.property);
      }

      public int hashCode() {
         return this.property.hashCode();
      }
   }

   public static class EnumEntrySorted extends PropertyValueMapper.Entry {
      private EnumEntrySorted(IProperty property, int count) {
      }

      public int get(Object v) {
         return ((Enum)v).ordinal();
      }

      public static PropertyValueMapper.Entry create(PropertyEnum entry) {
         Object[] values = entry.getValueClass().getEnumConstants();
         return (PropertyValueMapper.Entry)(entry.getAllowedValues().size() == values.length ? new PropertyValueMapper.EnumEntrySorted(entry, values.length) : new PropertyValueMapper.ObjectEntry(entry, true));
      }
   }

   public static class IntegerEntry extends PropertyValueMapper.Entry {
      private Int2IntMap values = new Int2IntOpenHashMap();

      private IntegerEntry(IProperty property) {
         this.values.defaultReturnValue(-1);
         Collection<Object> allowedValues = property.getAllowedValues();
         int i = 0;

         for(Object o : allowedValues) {
            this.values.put(((Integer)o).intValue(), i++);
         }

      }

      public int get(Object v) {
         return this.values.get(((Integer)v).intValue());
      }

      public static PropertyValueMapper.Entry create(PropertyInteger entry) {
         List<Integer> sorted = Lists.newArrayList(entry.getAllowedValues());
         sorted.sort(Comparator.naturalOrder());
         int min = ((Integer)sorted.get(0)).intValue();

         for(int i = 1; i < sorted.size(); ++i) {
            if (((Integer)sorted.get(i)).intValue() - ((Integer)sorted.get(i - 1)).intValue() != 1) {
               return new PropertyValueMapper.IntegerEntry(entry);
            }
         }

         return new PropertyValueMapper.IntegerEntrySorted(entry, min, sorted.size());
      }
   }

   public static class IntegerEntrySorted extends PropertyValueMapper.Entry {
      private final int minValue;
      private final int count;

      private IntegerEntrySorted(IProperty property, int minValue, int count) {
         this.minValue = minValue;
         this.count = count;
      }

      public int get(Object v) {
         int vv = ((Integer)v).intValue() - this.minValue;
         return vv < this.count ? vv : -1;
      }
   }

   public static class ObjectEntry extends PropertyValueMapper.Entry {
      private Object2IntMap values;

      private ObjectEntry(IProperty property, boolean identity) {
         this.values = (Object2IntMap)(identity ? new Object2IntOpenCustomHashMap(HashingStrategies.FASTUTIL_IDENTITY) : new Object2IntOpenHashMap());
         this.values.defaultReturnValue(-1);
         Collection<Object> allowedValues = property.getAllowedValues();
         int i = 0;

         for(Object o : allowedValues) {
            this.values.put(o, i++);
         }

      }

      public int get(Object v) {
         return this.values.getInt(v);
      }
   }
}
