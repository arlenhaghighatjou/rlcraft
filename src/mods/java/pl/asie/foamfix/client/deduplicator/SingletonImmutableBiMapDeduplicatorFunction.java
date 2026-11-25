package pl.asie.foamfix.client.deduplicator;

import java.lang.invoke.MethodHandle;
import pl.asie.foamfix.util.MethodHandleHelper;

public class SingletonImmutableBiMapDeduplicatorFunction implements DeduplicatorFunction {
   public static final String CLASS_NAME = "com.google.common.collect.SingletonImmutableBiMap";
   private static final MethodHandle KEY_GETTER = MethodHandleHelper.findFieldGetter("com.google.common.collect.SingletonImmutableBiMap", "singleKey");
   private static final MethodHandle KEY_SETTER = MethodHandleHelper.findFieldSetter("com.google.common.collect.SingletonImmutableBiMap", "singleKey");
   private static final MethodHandle VALUE_GETTER = MethodHandleHelper.findFieldGetter("com.google.common.collect.SingletonImmutableBiMap", "singleValue");
   private static final MethodHandle VALUE_SETTER = MethodHandleHelper.findFieldSetter("com.google.common.collect.SingletonImmutableBiMap", "singleValue");
   private final Deduplicator parent;

   public SingletonImmutableBiMapDeduplicatorFunction(Deduplicator parent) {
      this.parent = parent;
   }

   public Object deduplicate(Object o, int recursion) throws Throwable {
      Object element = KEY_GETTER.invoke(o);
      Object elementD = this.parent.deduplicateObject(element, recursion + 1);
      if (elementD != null) {
         KEY_SETTER.invoke(o, elementD);
      }

      element = VALUE_GETTER.invoke(o);
      elementD = this.parent.deduplicateObject(element, recursion + 1);
      if (elementD != null) {
         VALUE_SETTER.invoke(o, elementD);
      }

      return o;
   }
}
