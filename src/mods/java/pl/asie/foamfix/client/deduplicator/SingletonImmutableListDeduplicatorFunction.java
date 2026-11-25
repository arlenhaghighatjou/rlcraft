package pl.asie.foamfix.client.deduplicator;

import java.lang.invoke.MethodHandle;
import pl.asie.foamfix.util.MethodHandleHelper;

public class SingletonImmutableListDeduplicatorFunction implements DeduplicatorFunction {
   public static final String CLASS_NAME = "com.google.common.collect.SingletonImmutableList";
   private static final MethodHandle ELEMENT_GETTER = MethodHandleHelper.findFieldGetter("com.google.common.collect.SingletonImmutableList", "element");
   private static final MethodHandle ELEMENT_SETTER = MethodHandleHelper.findFieldSetter("com.google.common.collect.SingletonImmutableList", "element");
   private final Deduplicator parent;

   public SingletonImmutableListDeduplicatorFunction(Deduplicator parent) {
      this.parent = parent;
   }

   public Object deduplicate(Object o, int recursion) throws Throwable {
      Object element = ELEMENT_GETTER.invoke(o);
      Object elementD = this.parent.deduplicateObject(element, recursion + 1);
      if (elementD != null) {
         ELEMENT_SETTER.invoke(o, elementD);
      }

      return o;
   }
}
