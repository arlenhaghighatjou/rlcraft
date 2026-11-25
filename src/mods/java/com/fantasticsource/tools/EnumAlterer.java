package com.fantasticsource.tools;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import sun.reflect.ConstructorAccessor;
import sun.reflect.FieldAccessor;
import sun.reflect.ReflectionFactory;

public class EnumAlterer {
   private static ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();

   public EnumAlterer() {
   }

   private static void setFailsafeFieldValue(Field field, Object target, Object value) throws NoSuchFieldException, IllegalAccessException {
      field.setAccessible(true);
      Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      int modifiers = modifiersField.getInt(field);
      modifiers = modifiers & -17;
      modifiersField.setInt(field, modifiers);
      FieldAccessor fa = reflectionFactory.newFieldAccessor(field, false);
      fa.set(target, value);
   }

   private static void blankField(Class<?> enumClass, String fieldName) throws NoSuchFieldException, IllegalAccessException {
      for(Field field : Class.class.getDeclaredFields()) {
         if (field.getName().contains(fieldName)) {
            AccessibleObject.setAccessible(new Field[]{field}, true);
            setFailsafeFieldValue(field, enumClass, (Object)null);
            break;
         }
      }

   }

   private static void cleanEnumCache(Class<?> enumClass) throws NoSuchFieldException, IllegalAccessException {
      blankField(enumClass, "enumConstantDirectory");
      blankField(enumClass, "enumConstants");
   }

   private static ConstructorAccessor getConstructorAccessor(Class<?> enumClass, Class<?>[] additionalParameterTypes) throws NoSuchMethodException {
      Class<?>[] parameterTypes = new Class[additionalParameterTypes.length + 2];
      parameterTypes[0] = String.class;
      parameterTypes[1] = Integer.TYPE;
      System.arraycopy(additionalParameterTypes, 0, parameterTypes, 2, additionalParameterTypes.length);
      return reflectionFactory.newConstructorAccessor(enumClass.getDeclaredConstructor(parameterTypes));
   }

   private static Object makeEnum(Class<?> enumClass, String value, int ordinal, Class<?>[] additionalTypes, Object[] additionalValues) throws Exception {
      Object[] params = new Object[additionalValues.length + 2];
      params[0] = value;
      params[1] = ordinal;
      System.arraycopy(additionalValues, 0, params, 2, additionalValues.length);
      return enumClass.cast(getConstructorAccessor(enumClass, additionalTypes).newInstance(params));
   }

   public static <T extends Enum<?>> T addEnum(Class<T> enumType, String enumName) {
      if (!Enum.class.isAssignableFrom(enumType)) {
         throw new RuntimeException("class " + enumType + " is not an instance of Enum");
      } else {
         Field valuesField = null;
         Field[] fields = enumType.getDeclaredFields();

         for(Field field : fields) {
            if (field.getName().contains("$VALUES")) {
               valuesField = field;
               break;
            }
         }

         AccessibleObject.setAccessible(new Field[]{valuesField}, true);

         try {
            T[] previousValues = (Enum[])valuesField.get(enumType);
            List<T> values = new ArrayList(Arrays.asList(previousValues));
            T newValue = makeEnum(enumType, enumName, values.size(), new Class[0], new Object[0]);
            values.add(newValue);
            setFailsafeFieldValue(valuesField, (Object)null, values.toArray((Enum[])Array.newInstance(enumType, 0)));
            cleanEnumCache(enumType);
            return newValue;
         } catch (Exception var8) {
            var8.printStackTrace();
            throw new RuntimeException(var8.getMessage(), var8);
         }
      }
   }

   public static void setEnumOrdinal(Enum object, int ordinal) {
      try {
         Field field = object.getClass().getSuperclass().getDeclaredField("ordinal");
         field.setAccessible(true);
         field.set(object, Integer.valueOf(ordinal));
      } catch (Exception var3) {
         throw new RuntimeException("Can't update enum ordinal: " + var3);
      }
   }
}
