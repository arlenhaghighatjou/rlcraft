package ivorius.reccomplex.files.saving;

import ivorius.reccomplex.files.loading.LeveledRegistry;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import javax.annotation.ParametersAreNonnullByDefault;

public abstract class FileSaverAdapter<S> {
   protected String id;
   protected String suffix;
   protected LeveledRegistry<S> registry;

   public FileSaverAdapter(String id, String suffix, LeveledRegistry<S> registry) {
      this.id = id;
      this.suffix = suffix;
      this.registry = registry;
   }

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getSuffix() {
      return this.suffix;
   }

   public void setSuffix(String suffix) {
      this.suffix = suffix;
   }

   public LeveledRegistry<S> getRegistry() {
      return this.registry;
   }

   public void setRegistry(LeveledRegistry<S> registry) {
      this.registry = registry;
   }

   public void saveFile(Path path, String id) throws Exception {
      S s = this.registry.get(id);
      if (s == null) {
         throw new NoSuchElementException();
      } else {
         this.saveFile(path, s);
      }
   }

   @ParametersAreNonnullByDefault
   public abstract void saveFile(Path var1, S var2) throws Exception;
}
