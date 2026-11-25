package net.silentchaos512.lib.tile;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.activation.UnsupportedDataTypeException;
import net.minecraft.nbt.NBTTagCompound;
import net.silentchaos512.lib.util.NBTSerializer;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SyncVariable {
   String name();

   boolean onRead() default true;

   boolean onWrite() default true;

   boolean onPacket() default true;

   public static final class Helper {
      static final Map<Class, Function<NBTTagCompound, ?>> READERS = new HashMap();
      static final Map<Class, Function<?, NBTTagCompound>> WRITERS = new HashMap();
      static final Map<Class, NBTSerializer> SERIALIZERS = new HashMap();

      private Helper() {
      }

      public static <T> void registerSerializer(Class<T> clazz, final Function<NBTTagCompound, T> reader, final BiConsumer<NBTTagCompound, T> writer) {
         SERIALIZERS.put(clazz, new NBTSerializer<T>() {
            public T read(NBTTagCompound tags) {
               return (T)reader.apply(tags);
            }

            public void write(NBTTagCompound tags, T obj) {
               writer.accept(tags, obj);
            }
         });
      }

      public static void readSyncVars(Object obj, NBTTagCompound tags) {
         for(Field field : obj.getClass().getDeclaredFields()) {
            for(Annotation annotation : field.getDeclaredAnnotations()) {
               if (annotation instanceof SyncVariable) {
                  SyncVariable sync = (SyncVariable)annotation;

                  try {
                     if (!field.isAccessible()) {
                        field.setAccessible(true);
                     }

                     String name = sync.name();
                     if (field.getType() == Integer.TYPE) {
                        field.setInt(obj, tags.getInteger(name));
                     } else if (field.getType() == Float.TYPE) {
                        field.setFloat(obj, tags.getFloat(name));
                     } else if (field.getType() == String.class) {
                        field.set(obj, tags.getString(name));
                     } else if (field.getType() == Boolean.TYPE) {
                        field.setBoolean(obj, tags.getBoolean(name));
                     } else if (field.getType() == Double.TYPE) {
                        field.setDouble(obj, tags.getDouble(name));
                     } else if (field.getType() == Long.TYPE) {
                        field.setLong(obj, tags.getLong(name));
                     } else if (field.getType() == Short.TYPE) {
                        field.setShort(obj, tags.getShort(name));
                     } else if (field.getType() == Byte.TYPE) {
                        field.setByte(obj, tags.getByte(name));
                     } else {
                        if (!SERIALIZERS.containsKey(field.getType())) {
                           throw new UnsupportedDataTypeException("Don't know how to read type " + field.getType() + " from NBT!");
                        }

                        NBTSerializer serializer = (NBTSerializer)SERIALIZERS.get(field.getType());
                        NBTTagCompound compound = tags.getCompoundTag(name);
                        field.set(obj, serializer.read(compound));
                     }
                  } catch (UnsupportedDataTypeException | IllegalAccessException var14) {
                     var14.printStackTrace();
                  }
               }
            }
         }

      }

      public static NBTTagCompound writeSyncVars(Object obj, NBTTagCompound tags, SyncVariable.Type syncType) {
         for(Field field : obj.getClass().getDeclaredFields()) {
            for(Annotation annotation : field.getDeclaredAnnotations()) {
               if (annotation instanceof SyncVariable) {
                  SyncVariable sync = (SyncVariable)annotation;
                  if (syncType == SyncVariable.Type.WRITE && sync.onWrite() || syncType == SyncVariable.Type.PACKET && sync.onPacket()) {
                     try {
                        if (!field.isAccessible()) {
                           field.setAccessible(true);
                        }

                        String name = sync.name();
                        if (field.getType() == Integer.TYPE) {
                           tags.setInteger(name, field.getInt(obj));
                        } else if (field.getType() == Float.TYPE) {
                           tags.setFloat(name, field.getFloat(obj));
                        } else if (field.getType() == String.class) {
                           tags.setString(name, (String)field.get(obj));
                        } else if (field.getType() == Boolean.TYPE) {
                           tags.setBoolean(name, field.getBoolean(obj));
                        } else if (field.getType() == Double.TYPE) {
                           tags.setDouble(name, field.getDouble(obj));
                        } else if (field.getType() == Long.TYPE) {
                           tags.setLong(name, field.getLong(obj));
                        } else if (field.getType() == Short.TYPE) {
                           tags.setShort(name, field.getShort(obj));
                        } else if (field.getType() == Byte.TYPE) {
                           tags.setByte(name, field.getByte(obj));
                        } else {
                           if (!SERIALIZERS.containsKey(field.getType())) {
                              throw new UnsupportedDataTypeException("Don't know how to write type " + field.getType() + " to NBT!");
                           }

                           NBTTagCompound compound = new NBTTagCompound();
                           NBTSerializer serializer = (NBTSerializer)SERIALIZERS.get(field.getType());
                           serializer.write(compound, field.get(obj));
                           tags.setTag(name, compound);
                        }
                     } catch (UnsupportedDataTypeException | IllegalAccessException var15) {
                        var15.printStackTrace();
                     }
                  }
               }
            }
         }

         return tags;
      }
   }

   public static enum Type {
      READ,
      WRITE,
      PACKET;

      private Type() {
      }
   }
}
