package ivorius.reccomplex.utils.zip;

import ivorius.reccomplex.utils.ByteArrays;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.zip.ZipInputStream;

public class ZipFinder {
   private final Map<String, ZipFinder.EntryConsumer<InputStream>> map = new HashMap();

   public ZipFinder() {
   }

   public void read(ZipInputStream stream) throws IOException {
      IvZips.walkStreams(stream, (name, inputStream) -> {
         ZipFinder.EntryConsumer<InputStream> consumer = (ZipFinder.EntryConsumer)this.map.get(name);
         if (consumer != null) {
            consumer.accept(inputStream);
         }

      });
   }

   public <T> ZipFinder.Result<T> stream(String name, ZipFinder.Function<InputStream, T> function) {
      if (this.map.containsKey(name)) {
         throw new IllegalArgumentException();
      } else {
         ZipFinder.Result<T> result = new ZipFinder.Result<T>(name);
         this.map.put(name, (inputStream) -> {
            result.set(function.apply(inputStream));
         });
         return result;
      }
   }

   public <T> ZipFinder.Result<T> bytes(String name, ZipFinder.Function<byte[], T> function) {
      return this.<T>stream(name, (inputStream) -> {
         byte[] bytes = ByteArrays.completeByteArray(inputStream);
         return (ZipFinder.Result<T>)(bytes != null ? function.apply(bytes) : null);
      });
   }

   @FunctionalInterface
   private interface EntryConsumer<T> {
      void accept(T var1) throws IOException;
   }

   @FunctionalInterface
   public interface Function<S, D> {
      D apply(S var1) throws IOException;
   }

   public class MissingEntryException extends RuntimeException {
      public final String name;

      public MissingEntryException(String name) {
         super("Missing Entry: " + name);
         this.name = name;
      }
   }

   public class Result<T> {
      protected String name;
      protected T t;
      protected boolean present;

      public Result(String name) {
         this.name = name;
      }

      protected void set(T t) {
         this.t = t;
         this.present = true;
      }

      public boolean isPresent() {
         return this.present;
      }

      public T get() {
         if (!this.isPresent()) {
            throw ZipFinder.this.new MissingEntryException(this.name);
         } else {
            return this.t;
         }
      }

      public T orElse(Supplier<T> defaultVal) {
         return (T)(this.isPresent() ? this.t : defaultVal.get());
      }

      public Optional<T> peek() {
         return this.present ? Optional.of(this.t) : Optional.empty();
      }
   }
}
