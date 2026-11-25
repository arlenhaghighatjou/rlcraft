package ivorius.reccomplex..shadow.mcopts.reflection;

import ivorius.reccomplex..shadow.mcopts.MCOpts;
import java.lang.reflect.Field;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class SafeReflector {
   public SafeReflector() {
   }

   public static <T> T get(Class<?> clazz, String name, Object object) {
      try {
         return (T)ReflectionHelper.findField(clazz, new String[]{name}).get(object);
      } catch (Exception var4) {
         MCOpts.logger.error(var4);
         throw new RuntimeException("Unable to resolve: " + name);
      }
   }

   public static <T> T get(Class<?> clazz, String name, Object object, T fallback) {
      try {
         return (T)ReflectionHelper.findField(clazz, new String[]{name}).get(object);
      } catch (Exception var5) {
         MCOpts.logger.error(var5);
         return fallback;
      }
   }

   public static boolean of(Class<?> clazz, String name, SafeReflector.FieldTask task) {
      try {
         task.execute(ReflectionHelper.findField(clazz, new String[]{name}));
         return true;
      } catch (Exception var4) {
         MCOpts.logger.error(var4);
         return false;
      }
   }

   public interface FieldTask {
      void execute(Field var1) throws Exception;
   }
}
