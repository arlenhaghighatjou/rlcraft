package net.silentchaos512.lib.tile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
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
   }

   public static enum Type {
      READ,
      WRITE,
      PACKET;

      private Type() {
      }
   }
}
