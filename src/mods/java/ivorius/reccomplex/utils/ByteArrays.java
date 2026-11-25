package ivorius.reccomplex.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ByteArrays {
   public ByteArrays() {
   }

   public static byte[] completeByteArray(InputStream inputStream) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

      int aByte;
      try {
         while((aByte = inputStream.read()) >= 0) {
            byteArrayOutputStream.write(aByte);
         }
      } catch (Exception var4) {
         return null;
      }

      return byteArrayOutputStream.toByteArray();
   }

   public static byte[] toByteArray(ByteArrays.IOConsumer<ByteArrayOutputStream> consumer) throws IOException {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      consumer.accept(stream);
      return stream.toByteArray();
   }

   @FunctionalInterface
   public interface IOConsumer<T> {
      void accept(T var1) throws IOException;

      default ByteArrays.IOConsumer<T> andThen(ByteArrays.IOConsumer<? super T> after) {
         Objects.requireNonNull(after);
         return (t) -> {
            this.accept(t);
            after.accept(t);
         };
      }
   }
}
