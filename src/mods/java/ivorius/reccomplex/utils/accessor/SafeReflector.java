package ivorius.reccomplex.utils.accessor;

import ivorius.reccomplex.RecurrentComplex;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class SafeReflector {
   public SafeReflector() {
   }

   public static <T, O> T get(Class<O> clazz, O object, String... names) {
      try {
         return (T)ReflectionHelper.findField(clazz, names).get(object);
      } catch (Exception var4) {
         RecurrentComplex.logger.error(var4);
         throw new RuntimeException("Unable to resolve: " + names);
      }
   }

   public static <T, O> T get(Class<O> clazz, O object, T fallback, String... names) {
      try {
         return (T)ReflectionHelper.findField(clazz, names).get(object);
      } catch (Exception var5) {
         RecurrentComplex.logger.error(var5);
         return fallback;
      }
   }

   public static boolean of(Class<?> clazz, SafeReflector.FieldTask task, String... names) {
      try {
         task.execute(ReflectionHelper.findField(clazz, names));
         return true;
      } catch (Exception var4) {
         RecurrentComplex.logger.error(var4);
         return false;
      }
   }

   public static <T> T invoke(Object object, Method method, T defaultVal, Object... params) {
      try {
         return (T)method.invoke(object, params);
      } catch (InvocationTargetException | IllegalAccessException var5) {
         RecurrentComplex.logger.error(var5);
         return defaultVal;
      }
   }

   public interface FieldTask {
      void execute(Field var1) throws Exception;
   }
}
