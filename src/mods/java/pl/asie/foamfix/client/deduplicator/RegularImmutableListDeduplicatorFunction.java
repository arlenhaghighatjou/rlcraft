package pl.asie.foamfix.client.deduplicator;

import java.lang.invoke.MethodHandle;
import pl.asie.foamfix.util.MethodHandleHelper;

public class RegularImmutableListDeduplicatorFunction implements DeduplicatorFunction {
   public static final String CLASS_NAME = "com.google.common.collect.RegularImmutableList";
   private static final MethodHandle ARRAY_GETTER = MethodHandleHelper.findFieldGetter("com.google.common.collect.RegularImmutableList", "array");
   private final Deduplicator parent;

   public RegularImmutableListDeduplicatorFunction(Deduplicator parent) {
      this.parent = parent;
   }

   public Object deduplicate(Object o, int recursion) throws Throwable {
      Object[] array = ARRAY_GETTER.invoke(o);

      for(int i = 0; i < array.length; ++i) {
         Object entry = array[i];
         Object entryD = this.parent.deduplicateObject(entry, recursion + 1);
         if (entryD != null) {
            array[i] = entryD;
         }
      }

      return o;
   }
}
