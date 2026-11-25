package org.orecruncher.lib;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.CoreModManager;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.LibBase;

public class ReflectedMethod<R> {
   protected static boolean isDeobfuscatedEnvironment = false;
   protected final String className;
   protected final String methodName;
   protected final Method method;

   public ReflectedMethod(@Nonnull String className, @Nonnull String methodName, @Nullable String obfMethodName, Class<?>... parameters) {
      this.className = className;
      this.methodName = methodName;
      this.method = this.resolve(className, methodName, obfMethodName, parameters);
   }

   public ReflectedMethod(@Nonnull Class<?> clazz, @Nonnull String methodName, @Nullable String obfMethodName, Class<?>... parameters) {
      Preconditions.checkNotNull(clazz);
      Preconditions.checkArgument(StringUtils.isNotEmpty(methodName), "Field name cannot be empty");
      this.className = clazz.getName();
      this.methodName = methodName;
      this.method = this.resolve(clazz, methodName, obfMethodName, parameters);
   }

   @Nullable
   private Method resolve(@Nonnull String className, @Nonnull String methodName, @Nullable String obfMethodName, Class<?>... parameters) {
      try {
         return this.resolve(Class.forName(className), methodName, obfMethodName, parameters);
      } catch (Throwable var6) {
         return null;
      }
   }

   @Nullable
   private Method resolve(@Nonnull Class<?> clazz, @Nonnull String methodName, @Nullable String obfMethodName, Class<?>... parameters) {
      String nameToFind = isDeobfuscatedEnvironment ? methodName : (String)MoreObjects.firstNonNull(obfMethodName, methodName);

      try {
         Method f = clazz.getDeclaredMethod(nameToFind, parameters);
         f.setAccessible(true);
         return f;
      } catch (Throwable var8) {
         String msg = String.format("Unable to locate field [%s::%s]", clazz.getName(), nameToFind);
         LibBase.log().warn(msg);
         return null;
      }
   }

   public boolean isAvailable() {
      return this.method != null;
   }

   public R invoke(Object ref, Object... parms) {
      try {
         return (R)this.method.invoke(ref, parms);
      } catch (Exception var4) {
         var4.printStackTrace();
         return (R)null;
      }
   }

   protected void check() {
      if (!this.isAvailable()) {
         String msg = String.format("Uninitialized method [%s::%s]", this.className, this.methodName);
         throw new IllegalStateException(msg);
      }
   }

   protected void report(@Nonnull Throwable t) {
      String msg = String.format("Unable to access field [%s::%s]", this.className, this.methodName);
      LibBase.log().error(msg, t);
   }

   @Nullable
   protected static Class<?> resolveClass(@Nonnull String className) {
      try {
         return Class.forName(className);
      } catch (Throwable var2) {
         return null;
      }
   }

   static {
      try {
         Field f = CoreModManager.class.getDeclaredField("deobfuscatedEnvironment");
         f.setAccessible(true);
         isDeobfuscatedEnvironment = f.getBoolean((Object)null);
      } catch (Throwable var1) {
         LibBase.log().error("Unable to determine obsfucated environment", var1);
      }

   }
}
