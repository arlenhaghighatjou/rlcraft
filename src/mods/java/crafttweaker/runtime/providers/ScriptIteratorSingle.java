package crafttweaker.runtime.providers;

import crafttweaker.runtime.IScriptIterator;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ScriptIteratorSingle implements IScriptIterator {
   private final File file;
   private File directory;
   private boolean first = true;

   public ScriptIteratorSingle(File file) {
      this.file = file;
   }

   public ScriptIteratorSingle(File file, File directory) {
      this.file = file;
      this.directory = directory;
   }

   public String getGroupName() {
      if (this.file != null && this.directory != null) {
         return this.file.getAbsolutePath().substring(this.directory.getAbsolutePath().length() + 1);
      } else if (this.file != null && this.file.getAbsolutePath().contains("scripts")) {
         return this.file.getAbsolutePath().split("scripts")[1];
      } else {
         return this.file != null ? this.file.getAbsolutePath() : "invalid_group_name";
      }
   }

   public boolean next() {
      if (this.first) {
         this.first = false;
         return true;
      } else {
         return false;
      }
   }

   public String getName() {
      return this.file.getName();
   }

   public InputStream open() throws IOException {
      return new BufferedInputStream(new FileInputStream(this.file));
   }

   public IScriptIterator copyCurrent() {
      return new ScriptIteratorSingle(this.file, this.directory);
   }
}
