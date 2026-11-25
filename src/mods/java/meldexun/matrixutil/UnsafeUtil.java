package meldexun.matrixutil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import sun.misc.Unsafe;

public class UnsafeUtil {
   public static final Unsafe UNSAFE;

   public UnsafeUtil() {
   }

   static {
      try {
         Field field = (Field)Stream.of(Unsafe.class.getDeclaredFields()).filter((f) -> {
            return f.getType() == Unsafe.class;
         }).filter((f) -> {
            return Modifier.isStatic(f.getModifiers());
         }).filter((f) -> {
            return Modifier.isFinal(f.getModifiers());
         }).findFirst().orElseThrow(NullPointerException::new);
         field.setAccessible(true);
         UNSAFE = (Unsafe)field.get((Object)null);
      } catch (Exception var1) {
         throw new UnsupportedOperationException("Failed to find sun.misc.Unsafe instance");
      }
   }
}
