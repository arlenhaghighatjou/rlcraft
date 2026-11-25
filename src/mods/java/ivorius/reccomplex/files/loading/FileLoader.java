package ivorius.reccomplex.files.loading;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.files.FileHandler;
import ivorius.reccomplex.files.RCFiles;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;

public class FileLoader extends FileHandler {
   private final Map<String, FileLoaderAdapter> adapters = new HashMap();

   public FileLoader() {
   }

   protected static String defaultName(Path path, String customID) {
      return customID != null ? customID : FilenameUtils.getBaseName(path.getFileName().toString());
   }

   public FileLoaderAdapter get(String suffix) {
      return (FileLoaderAdapter)this.adapters.get(suffix);
   }

   public FileLoaderAdapter register(FileLoaderAdapter handler) {
      if (this.adapters.containsKey(handler.getSuffix())) {
         throw new IllegalArgumentException();
      } else {
         return (FileLoaderAdapter)this.adapters.put(handler.getSuffix(), handler);
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

   public void clearFiles(LeveledRegistry.Level level) {
      this.adapters.values().forEach((h) -> {
         h.clearFiles(level);
      });
   }

   public void clearFiles(Collection<String> suffices, LeveledRegistry.Level level) {
      this.adapters.entrySet().stream().filter((entry) -> {
         return (void)suffices.contains(entry.getKey());
      }).forEach((entry) -> {
         ((FileLoaderAdapter)entry.getValue()).clearFiles(level);
      });
   }

   @ParametersAreNonnullByDefault
   public int tryLoadAll(ResourceLocation resourceLocation, FileLoadContext context) {
      return this.tryLoadAll(resourceLocation, context, this.keySet());
   }

   @ParametersAreNonnullByDefault
   public int tryLoadAll(ResourceLocation resourceLocation, FileLoadContext context, Collection<String> suffices) {
      Path path = RCFiles.tryPathFromResourceLocation(resourceLocation);
      return path != null ? this.tryLoadAll(path, context, suffices) : 0;
   }

   @ParametersAreNonnullByDefault
   public int tryLoadAll(Path path, FileLoadContext context) {
      return this.tryLoadAll(path, context, this.keySet());
   }

   @ParametersAreNonnullByDefault
   public int tryLoadAll(Path path, FileLoadContext context, Collection<String> suffices) {
      int[] added = new int[1];
      RCFiles.walkFilesRecursively(path, new FileSuffixFilter(suffices), true, (file) -> {
         this.tryLoad(file, (String)null, context);
         ++added[0];
      });
      return added[0];
   }

   @ParametersAreNonnullByDefault
   public boolean tryLoad(ResourceLocation resourceLocation, @Nullable String customID, FileLoadContext context) {
      Path path = null;

      boolean e;
      try {
         path = RCFiles.tryPathFromResourceLocation(resourceLocation);
         if (path == null) {
            return false;
         }

         try {
            e = this.load(path, customID, context);
         } catch (UnsupportedOperationException var10) {
            RecurrentComplex.logger.error(String.format("Reading unsupported: ?.%s", RCFiles.extension(path)), var10);
            return false;
         } catch (Exception var11) {
            RecurrentComplex.logger.error("Error reading from resource location '" + resourceLocation + "'", var11);
            return false;
         }
      } finally {
         RCFiles.closeQuietly(path);
      }

      return e;
   }

   @ParametersAreNonnullByDefault
   public boolean tryLoad(Path path, @Nullable String customID, FileLoadContext context) {
      try {
         return this.load(path, customID, context);
      } catch (UnsupportedOperationException var5) {
         RecurrentComplex.logger.error(String.format("Reading unsupported: ?.%s", RCFiles.extension(path)), var5);
      } catch (Exception var6) {
         RecurrentComplex.logger.error("Error loading resource: " + path, var6);
      }

      return false;
   }

   @ParametersAreNonnullByDefault
   public boolean load(Path path, @Nullable String customID, FileLoadContext context) throws Exception {
      FileLoaderAdapter handler = this.get(RCFiles.extension(path));
      String id = defaultName(path, customID);
      return handler != null && handler.loadFile(path, id, context);
   }
}
