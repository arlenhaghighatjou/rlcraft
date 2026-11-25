package meldexun.configutil;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Predicate;

public class ConfigUtil {
   public ConfigUtil() {
   }

   public static <T> void copyClientSettings(T src, T dest) throws ReflectiveOperationException {
      CopyUtil.copy(src, dest, (field) -> {
         return (void)(!field.isAnnotationPresent(ConfigUtil.Sync.class));
      });
   }

   public static <T> void copyAllSettings(T src, T dest) throws ReflectiveOperationException {
      CopyUtil.copy(src, dest, (Predicate)null);
   }

   public static <T> void writeServerSettings(T src, ByteBuf buffer) throws ReflectiveOperationException {
      ByteBufUtil.write(src, buffer, (field) -> {
         return (void)field.isAnnotationPresent(ConfigUtil.Sync.class);
      });
   }

   public static <T> void readServerSettings(T dest, ByteBuf buffer) throws ReflectiveOperationException {
      ByteBufUtil.read(dest, buffer, (field) -> {
         return (void)field.isAnnotationPresent(ConfigUtil.Sync.class);
      });
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface Sync {
   }
}
