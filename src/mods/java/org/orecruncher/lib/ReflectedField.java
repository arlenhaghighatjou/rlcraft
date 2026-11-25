package org.orecruncher.lib;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.CoreModManager;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.LibBase;

public class ReflectedField {
   protected static boolean isDeobfuscatedEnvironment = false;
   protected final String className;
   protected final String fieldName;
   protected final Field field;

   protected ReflectedField(@Nonnull String className, @Nonnull String fieldName, @Nullable String obfName) {
      this.className = className;
      this.fieldName = fieldName;
      this.field = resolve(className, fieldName, obfName);
   }

   protected ReflectedField(@Nonnull Class<?> clazz, @Nonnull String fieldName, @Nullable String obfName) {
      Preconditions.checkNotNull(clazz);
      Preconditions.checkArgument(StringUtils.isNotEmpty(fieldName), "Field name cannot be empty");
      this.className = clazz.getName();
      this.fieldName = fieldName;
      this.field = resolve(clazz, fieldName, obfName);
   }

   @Nullable
   private static Field resolve(@Nonnull String className, @Nonnull String fieldName, @Nullable String obfName) {
      try {
         return resolve(Class.forName(className), fieldName, obfName);
      } catch (Throwable var4) {
         return null;
      }
   }

   @Nullable
   private static Field resolve(@Nonnull Class<?> clazz, @Nonnull String fieldName, @Nullable String obfName) {
      String nameToFind = isDeobfuscatedEnvironment ? fieldName : (String)MoreObjects.firstNonNull(obfName, fieldName);

      try {
         Field f = clazz.getDeclaredField(nameToFind);
         f.setAccessible(true);
         return f;
      } catch (Throwable var6) {
         String msg = String.format("Unable to locate field [%s::%s]", clazz.getName(), nameToFind);
         LibBase.log().warn(msg);
         return null;
      }
   }

   public boolean isAvailable() {
      return this.field != null;
   }

   protected void check() {
      if (!this.isAvailable()) {
         String msg = String.format("Uninitialized field [%s::%s]", this.className, this.fieldName);
         throw new IllegalStateException(msg);
      }
   }

   protected void report(@Nonnull Throwable t) {
      String msg = String.format("Unable to access field [%s::%s]", this.className, this.fieldName);
      LibBase.log().error(msg, t);
   }

   @Nullable
   public static Class<?> resolveClass(@Nonnull String className) {
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

   public static class BooleanField<T> extends ReflectedField {
      public final boolean defaultValue;

      public BooleanField(@Nonnull Class<T> clazz, @Nonnull String fieldName, @Nonnull String obfName) {
         this(clazz, fieldName, obfName, false);
      }

      public BooleanField(@Nonnull Class<T> clazz, @Nonnull String fieldName, @Nonnull String obfName, boolean defaultValue) {
         super(clazz, fieldName, obfName);
         this.defaultValue = defaultValue;
      }

      public boolean get(@Nullable T obj) {
         this.check();

         try {
            return this.field.getBoolean(obj);
         } catch (Throwable var3) {
            this.report(var3);
            return this.defaultValue;
         }
      }

      public void set(@Nullable T obj, boolean val) {
         this.check();

         try {
            this.field.setBoolean(obj, val);
         } catch (Throwable var4) {
            this.report(var4);
         }

      }
   }

   public static class FloatField<T> extends ReflectedField {
      public final float defaultValue;

      public FloatField(@Nonnull String className, @Nonnull String fieldName, @Nonnull String obfName) {
         super(className, fieldName, obfName);
         this.defaultValue = 0.0F;
      }

      public FloatField(@Nonnull Class<T> clazz, @Nonnull String fieldName, @Nonnull String obfName) {
         this(clazz, fieldName, obfName, 0.0F);
      }

      public FloatField(@Nonnull Class<T> clazz, @Nonnull String fieldName, @Nonnull String obfName, float defaultValue) {
         super(clazz, fieldName, obfName);
         this.defaultValue = defaultValue;
      }

      public float get(@Nullable T obj) {
         this.check();

         try {
            return this.field.getFloat(obj);
         } catch (Throwable var3) {
            this.report(var3);
            return this.defaultValue;
         }
      }

      public void set(@Nullable T obj, float val) {
         this.check();

         try {
            this.field.setFloat(obj, val);
         } catch (Throwable var4) {
            this.report(var4);
         }

      }
   }

   public static class IntegerField<T> extends ReflectedField {
      public final int defaultValue;

      public IntegerField(@Nonnull String className, @Nonnull String fieldName, @Nonnull String obfName) {
         super(className, fieldName, obfName);
         this.defaultValue = 0;
      }

      public IntegerField(@Nonnull Class<T> clazz, @Nonnull String fieldName, @Nonnull String obfName) {
         this(clazz, fieldName, obfName, 0);
      }

      public IntegerField(@Nonnull Class<T> clazz, @Nonnull String fieldName, @Nonnull String obfName, int defaultValue) {
         super(clazz, fieldName, obfName);
         this.defaultValue = defaultValue;
      }

      public int get(@Nullable T obj) {
         this.check();

         try {
            return this.field.getInt(obj);
         } catch (Throwable var3) {
            this.report(var3);
            return this.defaultValue;
         }
      }

      public void set(@Nullable T obj, int val) {
         this.check();

         try {
            this.field.setInt(obj, val);
         } catch (Throwable var4) {
            this.report(var4);
         }

      }
   }

   public static class ObjectField<T, R> extends ReflectedField {
      public final R defaultValue;

      public ObjectField(@Nonnull String className, @Nonnull String fieldName, @Nonnull String obfName) {
         super(className, fieldName, obfName);
         this.defaultValue = null;
      }

      public ObjectField(@Nonnull Class<T> clazz, @Nonnull String fieldName, @Nonnull String obfName) {
         this(clazz, fieldName, obfName, (Object)null);
      }

      public ObjectField(@Nonnull Class<T> clazz, @Nonnull String fieldName, @Nonnull String obfName, @Nullable R defaultValue) {
         super(clazz, fieldName, obfName);
         this.defaultValue = defaultValue;
      }

      public R get(@Nullable T obj) {
         this.check();
         R result = null;

         try {
            result = (R)this.field.get(obj);
         } catch (Throwable var4) {
            this.report(var4);
         }

         return (R)(result == null ? this.defaultValue : result);
      }

      public void set(@Nullable T obj, @Nullable Object val) {
         this.check();

         try {
            this.field.set(obj, val);
         } catch (Throwable var4) {
            this.report(var4);
         }

      }
   }
}
