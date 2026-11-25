package mezz.jei.util;

import java.lang.reflect.Field;
import java.util.Optional;
import javax.annotation.Nullable;
import mezz.jei.collect.Table;

public final class ReflectionUtil {
   private static final Table<Class, Class, Optional<Field>> CACHE = Table.<Class, Class, Optional<Field>>hashBasedTable();

   private ReflectionUtil() {
   }

   @Nullable
   public static <T> T getFieldWithClass(Object object, Class<T> fieldClass) {
      Field field = getField(object, fieldClass);
      if (field != null) {
         try {
            Object fieldValue = field.get(object);
            if (fieldClass.isInstance(fieldValue)) {
               return (T)fieldClass.cast(fieldValue);
            }
         } catch (IllegalAccessException var4) {
            ;
         }
      }

      return (T)null;
   }

   @Nullable
   private static Field getField(Object object, Class<?> fieldClass) {
      Class<?> objectClass = object.getClass();
      Optional<Field> cachedField = (Optional)CACHE.get(fieldClass, objectClass);
      if (cachedField != null) {
         return (Field)cachedField.orElse((Object)null);
      } else {
         try {
            Field[] fields = objectClass.getDeclaredFields();

            for(Field field : fields) {
               if (fieldClass.isAssignableFrom(field.getType())) {
                  if (!field.isAccessible()) {
                     field.setAccessible(true);
                  }

                  CACHE.put(fieldClass, objectClass, Optional.of(field));
                  return field;
               }
            }
         } catch (SecurityException var9) {
            ;
         }

         CACHE.put(fieldClass, objectClass, Optional.empty());
         return null;
      }
   }
}
