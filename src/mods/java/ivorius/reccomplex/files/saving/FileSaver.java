package ivorius.reccomplex.files.saving;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.files.FileHandler;
import ivorius.reccomplex.files.RCFiles;
import ivorius.reccomplex.files.loading.FileLoaderAdapter;
import ivorius.reccomplex.files.loading.LeveledRegistry;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

public class FileSaver extends FileHandler {
   private final Map<String, FileSaverAdapter> adapters = new HashMap();

   public FileSaver() {
   }

   @Nullable
   public FileSaverAdapter get(String id) {
      return (FileSaverAdapter)this.adapters.get(id);
   }

   @Nonnull
   public FileSaverAdapter forceGet(String id) {
      FileSaverAdapter adapter = this.get(id);
      if (adapter == null) {
         throw new NoSuchElementException();
      } else {
         return adapter;
      }
   }

   public FileSaverAdapter register(FileSaverAdapter adapter) {
      if (this.adapters.containsKey(adapter.getId())) {
         throw new IllegalArgumentException();
      } else {
         return (FileSaverAdapter)this.adapters.put(adapter.getId(), adapter);
      }
   }

   public void unregister(FileLoaderAdapter handler) {
      this.adapters.remove(handler.getSuffix());
   }

   public Set<String> keySet() {
      return this.adapters.keySet();
   }

   public boolean has(String id) {
      return this.adapters.containsKey(id);
   }

   @Nonnull
   public String suffix(String adapter) {
      return this.forceGet(adapter).getSuffix();
   }

   @Nonnull
   public LeveledRegistry registry(String adapter) {
      return this.forceGet(adapter).getRegistry();
   }

   public boolean trySave(Path path, String adapter, String name) {
      try {
         this.save(path, adapter, name);
         return true;
      } catch (NoSuchElementException var5) {
         RecurrentComplex.logger.error(String.format("No entry found: %s in %s", name, RCFiles.extension(path)), var5);
      } catch (IllegalArgumentException var6) {
         RecurrentComplex.logger.error(String.format("No handler found: %s", path.getFileName()), var6);
      } catch (Exception var7) {
         RecurrentComplex.logger.error(String.format("Error writing file: %s", path), var7);
      }

      return false;
   }

   public void save(Path directory, String adapter, String name) throws Exception {
      FileSaverAdapter adapterObj = this.forceGet(adapter);
      Path path = directory.resolve(name + "." + adapterObj.getSuffix());
      Files.deleteIfExists(path);
      adapterObj.saveFile(path, name);
   }

   public Pair<Set<Path>, Set<Path>> tryDeleteWithID(Path path, String adapter, String name) {
      return this.tryDelete(path, name, this.suffix(adapter));
   }
}
