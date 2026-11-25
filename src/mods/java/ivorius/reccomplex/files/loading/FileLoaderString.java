package ivorius.reccomplex.files.loading;

import ivorius.reccomplex.RecurrentComplex;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.ParametersAreNonnullByDefault;

public abstract class FileLoaderString<S> extends FileLoaderAdapter {
   public FileLoaderString(String suffix) {
      super(suffix);
   }

   @ParametersAreNonnullByDefault
   public abstract boolean loadFile(String var1, Path var2, String var3, FileLoadContext var4) throws Exception;

   @ParametersAreNonnullByDefault
   public boolean loadFile(Path path, String id, FileLoadContext context) {
      String resource = null;

      try {
         resource = new String(Files.readAllBytes(path));
      } catch (IOException var7) {
         RecurrentComplex.logger.error("Resource is damaged: " + path, var7);
      }

      if (resource == null) {
         return false;
      } else {
         try {
            return this.loadFile(resource, path, id, context);
         } catch (Exception var6) {
            RecurrentComplex.logger.error("Error loading resource: " + path, var6);
            return false;
         }
      }
   }
}
