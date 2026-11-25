package me.lucko.spark.common.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class TemporaryFiles {
   private static final Set<Path> DELETE_SET = Collections.synchronizedSet(new HashSet());

   private TemporaryFiles() {
   }

   public static Path create(String prefix, String suffix) throws IOException {
      return register(Files.createTempFile(prefix, suffix));
   }

   public static Path register(Path path) {
      path.toFile().deleteOnExit();
      DELETE_SET.add(path);
      return path;
   }

   public static void deleteTemporaryFiles() {
      synchronized(DELETE_SET) {
         for(Iterator<Path> iterator = DELETE_SET.iterator(); iterator.hasNext(); iterator.remove()) {
            Path path = (Path)iterator.next();

            try {
               Files.deleteIfExists(path);
            } catch (IOException var5) {
               ;
            }
         }

      }
   }
}
