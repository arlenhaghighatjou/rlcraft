package ivorius.reccomplex.files.loading;

import com.google.gson.Gson;
import ivorius.reccomplex.RecurrentComplex;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileLoaderRegistryString<S> extends FileLoaderRegistry<S> {
   public FileLoaderRegistryString.Reader<? extends S> reader;

   public FileLoaderRegistryString(String suffix, LeveledRegistry<? super S> registry, FileLoaderRegistryString.Reader<? extends S> reader) {
      super(suffix, registry);
      this.reader = reader;
   }

   public FileLoaderRegistryString(String suffix, LeveledRegistry<? super S> registry, Gson gson, Class<? extends S> type) {
      this(suffix, registry, gsonReader(gson, type));
   }

   public FileLoaderRegistryString(String suffix, LeveledRegistry<? super S> registry, Class<? extends S> type) {
      this(suffix, registry, gsonReader(type));
   }

   public static <S> FileLoaderRegistryString.Reader<S> gsonReader(Gson gson, Class<? extends S> type) {
      return (s) -> {
         return (FileLoaderRegistryString.Reader<S>)gson.fromJson(s, type);
      };
   }

   public static <S> FileLoaderRegistryString.Reader<S> gsonReader(Class<? extends S> type) {
      return gsonReader(new Gson(), type);
   }

   public S read(Path path, String name) {
      String resource = null;

      try {
         resource = new String(Files.readAllBytes(path));
      } catch (IOException var6) {
         RecurrentComplex.logger.warn("Resource is damaged: " + name, var6);
      }

      if (resource == null) {
         return (S)null;
      } else {
         try {
            return (S)this.read(resource);
         } catch (Exception var5) {
            RecurrentComplex.logger.warn("Error reading resource: " + name, var5);
            return (S)null;
         }
      }
   }

   public S read(String file) throws Exception {
      return this.reader.read(file);
   }

   public interface Reader<S> {
      S read(String var1) throws Exception;
   }
}
