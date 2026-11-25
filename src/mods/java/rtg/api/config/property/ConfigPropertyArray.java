package rtg.api.config.property;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;

public abstract class ConfigPropertyArray<T extends Comparable<T>> extends ConfigProperty {
   private final Class<T> clazz;
   private T minValue;
   private T maxValue;
   private Collection<T> values;

   @SafeVarargs
   private ConfigPropertyArray(ConfigProperty.Type type, String name, String category, String description, Class<T> clazz, @Nullable T minValue, @Nullable T maxValue, T... values) {
      super(type, name, category, description);
      this.clazz = clazz;
      this.minValue = minValue;
      this.maxValue = maxValue;
      this.values = Arrays.asList(values);
   }

   public T getMinValue() {
      return this.minValue;
   }

   public T getMaxValue() {
      return this.maxValue;
   }

   public T[] getValues() {
      return (T[])(this.values.toArray((Comparable[])Array.newInstance(this.clazz, this.values.size())));
   }

   public Collection<T> getAsCollection() {
      return Collections.unmodifiableCollection(this.values);
   }

   public final void set(T[] values) {
      this.values = Collections.unmodifiableCollection(Arrays.asList(values));
   }

   public static final class ConfigPropertyArrayDouble extends ConfigPropertyArray<Double> {
      public ConfigPropertyArrayDouble(String name, String category, String description, double minValue, double maxValue, Double... values) {
      }

      public double[] getPrimitives() {
         return ArrayUtils.toPrimitive((Double[])super.getValues());
      }
   }

   public static final class ConfigPropertyArrayInteger extends ConfigPropertyArray<Integer> {
      public ConfigPropertyArrayInteger(String name, String category, String description, int minValue, int maxValue, Integer... values) {
      }

      public int[] getPrimitives() {
         return ArrayUtils.toPrimitive((Integer[])super.getValues());
      }
   }

   public static final class ConfigPropertyArrayString extends ConfigPropertyArray<String> {
      public ConfigPropertyArrayString(String name, String category, String description, String[] values) {
      }
   }
}
