package pl.asie.foamfix.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public final class MethodHandleHelper {
   private MethodHandleHelper() {
   }

   public static MethodHandle findConstructor(String s, Class<?>... types) {
      try {
         return MethodHandles.lookup().unreflectConstructor(ReflectionHelper.findConstructor(Class.forName(s), types));
      } catch (ClassNotFoundException | IllegalAccessException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public static MethodHandle findMethod(Class c, String nameDeobf, String nameObf, Class<?>... types) {
      try {
         return MethodHandles.lookup().unreflect(ReflectionHelper.findMethod(c, nameDeobf, nameObf, types));
      } catch (IllegalAccessException var5) {
         var5.printStackTrace();
         return null;
      }
   }

   public static MethodHandle findFieldGetter(Class c, String... names) {
      try {
         return MethodHandles.lookup().unreflectGetter(ReflectionHelper.findField(c, names));
      } catch (IllegalAccessException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public static MethodHandle findFieldSetter(Class c, String... names) {
      try {
         return MethodHandles.lookup().unreflectSetter(ReflectionHelper.findField(c, names));
      } catch (IllegalAccessException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public static MethodHandle findFieldGetter(String s, String... names) {
      try {
         return findFieldGetter(Class.forName(s), names);
      } catch (ClassNotFoundException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public static MethodHandle findFieldSetter(String s, String... names) {
      try {
         return findFieldSetter(Class.forName(s), names);
      } catch (ClassNotFoundException var3) {
         var3.printStackTrace();
         return null;
      }
   }
}
