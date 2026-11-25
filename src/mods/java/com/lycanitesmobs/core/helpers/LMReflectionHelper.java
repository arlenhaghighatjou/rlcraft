package com.lycanitesmobs.core.helpers;

import java.lang.reflect.Field;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class LMReflectionHelper {
   public LMReflectionHelper() {
   }

   public static <T> Field removeFinal(Class<? super T> classToAccess, T instance, String... fieldNames) {
      Field field = ReflectionHelper.findField(classToAccess, ObfuscationReflectionHelper.remapFieldNames(classToAccess.getName(), fieldNames));

      try {
         Field modifiersField = Field.class.getDeclaredField("modifiers");
         modifiersField.setAccessible(true);
         modifiersField.setInt(field, field.getModifiers() & -17);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      return field;
   }

   public static <T, E> void setPrivateFinalValue(Class<? super T> classToAccess, T instance, E value, String... fieldNames) {
      Field field = ReflectionHelper.findField(classToAccess, ObfuscationReflectionHelper.remapFieldNames(classToAccess.getName(), fieldNames));

      try {
         Field modifiersField = Field.class.getDeclaredField("modifiers");
         modifiersField.setAccessible(true);
         modifiersField.setInt(field, field.getModifiers() & -17);
         field.set(instance, value);
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }
}
