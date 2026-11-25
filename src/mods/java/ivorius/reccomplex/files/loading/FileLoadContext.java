package ivorius.reccomplex.files.loading;

public class FileLoadContext {
   public final String domain;
   public final boolean active;
   public final LeveledRegistry.Level level;

   public FileLoadContext(String domain, boolean active, LeveledRegistry.Level level) {
      this.domain = domain;
      this.active = active;
      this.level = level;
   }
}
