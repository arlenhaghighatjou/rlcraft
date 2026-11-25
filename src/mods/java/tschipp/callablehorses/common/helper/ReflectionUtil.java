package tschipp.callablehorses.common.helper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToFindMethodException;

public class ReflectionUtil {
   public ReflectionUtil() {
   }

   public static MethodHandle findMethod(Class<?> clazz, String methodName, @Nullable String methodObfName, Class<?>... parameterTypes) {
      Method method = ReflectionHelper.findMethod(clazz, methodName, methodObfName, parameterTypes);

      try {
         return MethodHandles.lookup().unreflect(method);
      } catch (IllegalAccessException var6) {
         throw new UnableToFindMethodException(var6);
      }
   }

   public static MethodHandle findFieldGetter(Class<?> clazz, String... fieldNames) {
      Field field = ReflectionHelper.findField(clazz, fieldNames);

      try {
         return MethodHandles.lookup().unreflectGetter(field);
      } catch (IllegalAccessException var4) {
         throw new UnableToAccessFieldException(fieldNames, var4);
      }
   }

   public static MethodHandle findFieldSetter(Class<?> clazz, String... fieldNames) {
      Field field = ReflectionHelper.findField(clazz, fieldNames);

      try {
         return MethodHandles.lookup().unreflectSetter(field);
      } catch (IllegalAccessException var4) {
         throw new UnableToAccessFieldException(fieldNames, var4);
      }
   }
}
