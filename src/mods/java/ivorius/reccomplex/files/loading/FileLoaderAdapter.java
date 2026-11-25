package ivorius.reccomplex.files.loading;

import java.nio.file.Path;
import javax.annotation.ParametersAreNonnullByDefault;

public abstract class FileLoaderAdapter {
   protected String suffix;

   public FileLoaderAdapter(String suffix) {
      this.suffix = suffix;
   }

   public String getSuffix() {
      return this.suffix;
   }

   public void setSuffix(String suffix) {
      this.suffix = suffix;
   }

   @ParametersAreNonnullByDefault
   protected abstract boolean loadFile(Path var1, String var2, FileLoadContext var3) throws Exception;

   @ParametersAreNonnullByDefault
   abstract void clearFiles(LeveledRegistry.Level var1);
}
