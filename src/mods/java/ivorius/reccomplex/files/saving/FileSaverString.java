package ivorius.reccomplex.files.saving;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ivorius.reccomplex.files.loading.LeveledRegistry;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

public class FileSaverString<S> extends FileSaverAdapter<S> {
   public FileSaverString.Writer<? super S> writer;

   public FileSaverString(String id, String suffix, LeveledRegistry<S> registry, FileSaverString.Writer<? super S> writer) {
      super(id, suffix, registry);
      this.writer = writer;
   }

   public FileSaverString(String id, String suffix, LeveledRegistry<S> registry, Gson gson, Class<? extends S> type) {
      this(id, suffix, registry, gsonSaver(gson, type));
   }

   public FileSaverString(String id, String suffix, LeveledRegistry<S> registry, Class<? extends S> type) {
      this(id, suffix, registry, gsonSaver(type));
   }

   public static <S> FileSaverString.Writer<S> gsonSaver(Gson gson, Class<? extends S> type) {
      return (s) -> {
         return gson.toJson(s, type);
      };
   }

   public static <S> FileSaverString.Writer<S> gsonSaver(Class<? extends S> type) {
      return gsonSaver((new GsonBuilder()).setPrettyPrinting().create(), type);
   }

   public void saveFile(Path path, S s) throws Exception {
      Files.write(path, this.write(s).getBytes(), new OpenOption[0]);
   }

   public String write(S s) throws Exception {
      if (this.writer == null) {
         throw new UnsupportedOperationException("write");
      } else {
         return this.writer.write(s);
      }
   }

   public interface Writer<S> {
      String write(S var1) throws Exception;
   }
}
