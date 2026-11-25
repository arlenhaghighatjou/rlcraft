package com.endertech.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Fields {
   public Fields() {
   }

   public static void unlockFinal(Field field) {
      field.setAccessible(true);

      try {
         Field modifiers = Field.class.getDeclaredField("modifiers");
         modifiers.setAccessible(true);
         modifiers.setInt(field, field.getModifiers() & -17);
      } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException var2) {
         var2.printStackTrace();
      }

   }

   public static List<Field> getStaticFrom(Class<?> clazz) {
      return (List)Arrays.stream(clazz.getDeclaredFields()).filter((f) -> {
         return Modifier.isStatic(f.getModifiers());
      }).collect(Collectors.toList());
   }
}
