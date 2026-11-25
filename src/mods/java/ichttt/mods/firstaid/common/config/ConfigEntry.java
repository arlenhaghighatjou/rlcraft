package ichttt.mods.firstaid.common.config;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class ConfigEntry<T extends Annotation> {
   @Nonnull
   public final Field field;
   @Nullable
   private final Object fieldAccessor;
   private final boolean revert;
   @Nonnull
   public final UniqueProperty property;
   @Nonnull
   public final T annotation;
   private boolean hasRemoteData = false;
   @Nullable
   private ByteBuf originalState;

   public ConfigEntry(@Nonnull Field f, @Nullable Object fieldAccessor, @Nonnull T annotation, boolean revertOnSave, @Nonnull UniqueProperty property) {
      this.field = f;
      this.fieldAccessor = fieldAccessor;
      this.revert = revertOnSave;
      this.property = property;
      this.annotation = annotation;
      if (revertOnSave) {
         ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();

         try {
            Object originalState = f.get(fieldAccessor);
            this.writeToBuf(buffer);
            this.readFromBuf(buffer);
            if (!originalState.equals(f.get(fieldAccessor))) {
               throw new RuntimeException("Error while writing and reading: Values do not match!");
            }

            buffer.release();
         } catch (IllegalAccessException var11) {
            buffer.release();
            throw new RuntimeException("Failed to access field " + f, var11);
         } finally {
            this.hasRemoteData = false;
         }

         this.originalState = ByteBufAllocator.DEFAULT.buffer();
         this.writeToBuf(this.originalState);
      }

   }

   protected void finalize() throws Throwable {
      super.finalize();
      if (this.originalState != null && this.originalState.refCnt() > 0) {
         this.originalState.release(this.originalState.refCnt());
      }

   }

   public boolean hasRemoteData() {
      return this.hasRemoteData;
   }

   public void revert() {
      if (this.revert && this.hasRemoteData) {
         this.readFromBuf(((ByteBuf)Objects.requireNonNull(this.originalState)).copy());
         this.hasRemoteData = false;
      }

   }

   public void updateOrigState() {
      if (this.revert) {
         ((ByteBuf)Objects.requireNonNull(this.originalState)).release();
         this.originalState = ByteBufAllocator.DEFAULT.buffer();
         this.writeToBuf(this.originalState);
      }

   }

   public void writeToBuf(ByteBuf buf) {
      try {
         writeToByteBuf(this.field, this.fieldAccessor, buf);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException("Failed to read config entry " + this.field + " for write to ByteBuffer!", var3);
      }
   }

   public void readFromBuf(ByteBuf buf) {
      try {
         this.hasRemoteData = true;
         readFromByteBuf(this.field, this.fieldAccessor, buf);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException("Failed to write config entry " + this.field + " from ByteBuffer!", var3);
      }
   }

   private static boolean noSync(Field f, boolean isStatic) {
      if (!Modifier.isPublic(f.getModifiers())) {
         return true;
      } else {
         return Modifier.isStatic(f.getModifiers()) != isStatic ? true : f.isAnnotationPresent(Ignore.class);
      }
   }

   private static void readFromByteBuf(Field f, Object fieldAccessor, ByteBuf buf) throws IllegalAccessException {
      if (!noSync(f, fieldAccessor == null)) {
         Class<?> type = f.getType();
         if (type != Boolean.TYPE && type != Boolean.class) {
            if (type != Float.TYPE && type != Float.class) {
               if (type != Double.TYPE && type != Double.class) {
                  if (type != Byte.TYPE && type != Byte.class) {
                     if (type != Character.TYPE && type != Character.class) {
                        if (type != Short.TYPE && type != Short.class) {
                           if (type != Integer.TYPE && type != Integer.class) {
                              if (type == String.class) {
                                 f.set(fieldAccessor, ByteBufUtils.readUTF8String(buf));
                              } else {
                                 if (type == boolean[].class || type == Boolean[].class) {
                                    throw new UnsupportedOperationException("Cannot read from buf: Class not implemented for sync: " + type);
                                 }

                                 if (type == float[].class || type == Float[].class) {
                                    throw new UnsupportedOperationException("Cannot read from buf: Class not implemented for sync: " + type);
                                 }

                                 if (type == double[].class || type == Double[].class) {
                                    throw new UnsupportedOperationException("Cannot read from buf: Class not implemented for sync: " + type);
                                 }

                                 if (type == byte[].class || type == Byte[].class) {
                                    throw new UnsupportedOperationException("Cannot read from buf: Class not implemented for sync: " + type);
                                 }

                                 if (type == char[].class || type == Character[].class) {
                                    throw new UnsupportedOperationException("Cannot read from buf: Class not implemented for sync: " + type);
                                 }

                                 if (type == short[].class || type == Short[].class) {
                                    throw new UnsupportedOperationException("Cannot read from buf: Class not implemented for sync: " + type);
                                 }

                                 if (type != int[].class && type != Integer[].class) {
                                    if (type == String[].class) {
                                       byte len = buf.readByte();
                                       String[] arr = new String[len];

                                       for(int i = 0; i < len; ++i) {
                                          arr[i] = ByteBufUtils.readUTF8String(buf);
                                       }

                                       f.set(fieldAccessor, arr);
                                    } else if (type.getSuperclass() == Object.class) {
                                       Object newInstance = f.get(fieldAccessor);

                                       for(Field newField : newInstance.getClass().getDeclaredFields()) {
                                          readFromByteBuf(newField, newInstance, buf);
                                       }
                                    }
                                 } else {
                                    byte len = buf.readByte();
                                    int[] arr = new int[len];

                                    for(int i = 0; i < len; ++i) {
                                       arr[i] = buf.readInt();
                                    }

                                    f.set(fieldAccessor, arr);
                                 }
                              }
                           } else {
                              f.setInt(fieldAccessor, buf.readInt());
                           }
                        } else {
                           f.setShort(fieldAccessor, buf.readShort());
                        }
                     } else {
                        f.setChar(fieldAccessor, buf.readChar());
                     }
                  } else {
                     f.setByte(fieldAccessor, buf.readByte());
                  }
               } else {
                  f.setDouble(fieldAccessor, buf.readDouble());
               }
            } else {
               f.setFloat(fieldAccessor, buf.readFloat());
            }
         } else {
            f.setBoolean(fieldAccessor, buf.readBoolean());
         }

      }
   }

   private static void writeToByteBuf(Field f, Object fieldAccessor, ByteBuf buf) throws IllegalAccessException {
      if (!noSync(f, fieldAccessor == null)) {
         Class<?> type = f.getType();
         if (type != Boolean.TYPE && type != Boolean.class) {
            if (type != Float.TYPE && type != Float.class) {
               if (type != Double.TYPE && type != Double.class) {
                  if (type != Byte.TYPE && type != Byte.class) {
                     if (type != Character.TYPE && type != Character.class) {
                        if (type != Short.TYPE && type != Short.class) {
                           if (type != Integer.TYPE && type != Integer.class) {
                              if (type == String.class) {
                                 ByteBufUtils.writeUTF8String(buf, (String)f.get(fieldAccessor));
                              } else {
                                 if (type == boolean[].class || type == Boolean[].class) {
                                    throw new UnsupportedOperationException("Cannot write to buf: Class not implemented for sync: " + type);
                                 }

                                 if (type == float[].class || type == Float[].class) {
                                    throw new UnsupportedOperationException("Cannot write to buf: Class not implemented for sync: " + type);
                                 }

                                 if (type == double[].class || type == Double[].class) {
                                    throw new UnsupportedOperationException("Cannot write to buf: Class not implemented for sync: " + type);
                                 }

                                 if (type == byte[].class || type == Byte[].class) {
                                    throw new UnsupportedOperationException("Cannot write to buf: Class not implemented for sync: " + type);
                                 }

                                 if (type == char[].class || type == Character[].class) {
                                    throw new UnsupportedOperationException("Cannot write to buf: Class not implemented for sync: " + type);
                                 }

                                 if (type == short[].class || type == Short[].class) {
                                    throw new UnsupportedOperationException("Cannot write to buf: Class not implemented for sync: " + type);
                                 }

                                 if (type == int[].class || type == Integer[].class) {
                                    int[] value = (int[])f.get(fieldAccessor);
                                    buf.writeByte(checkedCast(value.length));

                                    for(int i : value) {
                                       buf.writeInt(i);
                                    }
                                 } else if (type == String[].class) {
                                    String[] value = (String[])f.get(fieldAccessor);
                                    buf.writeByte(checkedCast(value.length));

                                    for(String s : value) {
                                       ByteBufUtils.writeUTF8String(buf, s);
                                    }
                                 } else if (type.getSuperclass() == Object.class) {
                                    Object newInstance = f.get(fieldAccessor);

                                    for(Field newField : newInstance.getClass().getDeclaredFields()) {
                                       writeToByteBuf(newField, newInstance, buf);
                                    }
                                 }
                              }
                           } else {
                              buf.writeInt(f.getInt(fieldAccessor));
                           }
                        } else {
                           buf.writeChar(f.getShort(fieldAccessor));
                        }
                     } else {
                        buf.writeChar(f.getChar(fieldAccessor));
                     }
                  } else {
                     buf.writeByte(f.getByte(fieldAccessor));
                  }
               } else {
                  buf.writeDouble(f.getDouble(fieldAccessor));
               }
            } else {
               buf.writeFloat(f.getFloat(fieldAccessor));
            }
         } else {
            buf.writeBoolean(f.getBoolean(fieldAccessor));
         }

      }
   }

   private static byte checkedCast(int val) {
      if (val > 127) {
         throw new RuntimeException(val + " is too large!");
      } else {
         return (byte)val;
      }
   }
}
