package crafttweaker.runtime.providers;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.runtime.IScriptIterator;
import crafttweaker.runtime.IScriptProvider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ScriptProviderDirectory implements IScriptProvider {
   private final File directory;

   public ScriptProviderDirectory(File directory) {
      if (directory == null) {
         throw new IllegalArgumentException("directory cannot be null");
      } else {
         this.directory = directory;
      }
   }

   public Iterator<IScriptIterator> getScripts() {
      List<IScriptIterator> scripts = new ArrayList();
      if (this.directory.exists()) {
         this.iterate(this.directory, scripts);
      }

      if (scripts.size() > 1) {
         scripts.sort((sc, sc1) -> {
            return sc != null && sc1 != null ? sc.getName().compareTo(sc1.getName()) : -1;
         });
      }

      return scripts.iterator();
   }

   private void iterate(File directoryIterated, List<IScriptIterator> scripts) {
      File[] files = directoryIterated.listFiles();
      if (files != null) {
         for(File file : files) {
            if (file.isDirectory()) {
               this.iterate(file, scripts);
            } else if (file.isFile() && file.getName().endsWith(".zs")) {
               scripts.add(new ScriptIteratorSingle(file, this.directory));
            } else if (file.isFile() && file.getName().endsWith(".zip")) {
               try {
                  scripts.add(new ScriptIteratorZip(file, this.directory));
               } catch (IOException var9) {
                  CraftTweakerAPI.logError("Could not load " + file.getName() + ": " + var9.getMessage());
               }
            }
         }

      }
   }
}
