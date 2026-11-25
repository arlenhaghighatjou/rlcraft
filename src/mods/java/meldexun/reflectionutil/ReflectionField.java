package meldexun.reflectionutil;

import java.lang.reflect.Field;

public class ReflectionField<T> {
   private final Field field;

   public ReflectionField(Field field) {
      this.field = field;
   }

   public ReflectionField(Class<?> clazz, String obfuscatedName, String deobfuscatedName) {
      Field f = null;

      try {
         f = clazz.getDeclaredField(obfuscatedName);
         f.setAccessible(true);
      } catch (NoSuchFieldException var8) {
         try {
            f = clazz.getDeclaredField(deobfuscatedName);
            f.setAccessible(true);
         } catch (NoSuchFieldException var7) {
            ;
         }
      }

      this.field = f;
   }

   public ReflectionField(String className, String obfuscatedName, String deobfuscatedName) {
      Class<?> clazz;
      try {
         clazz = Class.forName(className);
      } catch (ClassNotFoundException var10) {
         this.field = null;
         return;
      }

      Field f = null;

      try {
         f = clazz.getDeclaredField(obfuscatedName);
         f.setAccessible(true);
      } catch (NoSuchFieldException var9) {
         try {
            f = clazz.getDeclaredField(deobfuscatedName);
            f.setAccessible(true);
         } catch (NoSuchFieldException var8) {
            ;
         }
      }

      this.field = f;
   }

   public void set(Object obj, T value) {
      try {
         this.field.set(obj, value);
      } catch (ReflectiveOperationException var4) {
         throw new RuntimeException(var4);
      }
   }

   public void setBoolean(Object obj, boolean value) {
      try {
         this.field.setBoolean(obj, value);
      } catch (ReflectiveOperationException var4) {
         throw new RuntimeException(var4);
      }
   }

   public void setByte(Object obj, byte value) {
      try {
         this.field.setByte(obj, value);
      } catch (ReflectiveOperationException var4) {
         throw new RuntimeException(var4);
      }
   }

   public void setShort(Object obj, short value) {
      try {
         this.field.setShort(obj, value);
      } catch (ReflectiveOperationException var4) {
         throw new RuntimeException(var4);
      }
   }

   public void setInt(Object obj, int value) {
      try {
         this.field.setInt(obj, value);
      } catch (ReflectiveOperationException var4) {
         throw new RuntimeException(var4);
      }
   }

   public void setLong(Object obj, long value) {
      try {
         this.field.setLong(obj, value);
      } catch (ReflectiveOperationException var5) {
         throw new RuntimeException(var5);
      }
   }

   public void setFloat(Object obj, float value) {
      try {
         this.field.setFloat(obj, value);
      } catch (ReflectiveOperationException var4) {
         throw new RuntimeException(var4);
      }
   }

   public void setDouble(Object obj, double value) {
      try {
         this.field.setDouble(obj, value);
      } catch (ReflectiveOperationException var5) {
         throw new RuntimeException(var5);
      }
   }

   public void setChar(Object obj, char value) {
      try {
         this.field.setChar(obj, value);
      } catch (ReflectiveOperationException var4) {
         throw new RuntimeException(var4);
      }
   }

   public T get(Object obj) {
      try {
         return (T)this.field.get(obj);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   public boolean getBoolean(Object obj) {
      try {
         return this.field.getBoolean(obj);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   public byte getByte(Object obj) {
      try {
         return this.field.getByte(obj);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   public short getShort(Object obj) {
      try {
         return this.field.getShort(obj);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   public int getInt(Object obj) {
      try {
         return this.field.getInt(obj);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   public long getLong(Object obj) {
      try {
         return this.field.getLong(obj);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   public float getFloat(Object obj) {
      try {
         return this.field.getFloat(obj);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   public double getDouble(Object obj) {
      try {
         return this.field.getDouble(obj);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   public char getChar(Object obj) {
      try {
         return this.field.getChar(obj);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      }
   }

   public boolean isPresent() {
      return this.field != null;
   }
}
