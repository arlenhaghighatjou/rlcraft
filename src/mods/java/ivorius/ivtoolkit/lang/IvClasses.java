package ivorius.ivtoolkit.lang;

import ivorius.ivtoolkit.IvToolkit;
import javax.annotation.Nullable;

public class IvClasses {
   public IvClasses() {
   }

   @Nullable
   public static <T> T instantiate(Class<T> clazz) {
      T t = null;

      try {
         t = (T)clazz.newInstance();
      } catch (IllegalAccessException | InstantiationException var3) {
         IvToolkit.logger.error(var3);
      }

      return t;
   }

   @Nullable
   public static <T> T tryInstantiate(Class<T> clazz) {
      T t = null;

      try {
         t = (T)clazz.newInstance();
      } catch (IllegalAccessException | InstantiationException var3) {
         ;
      }

      return t;
   }
}
