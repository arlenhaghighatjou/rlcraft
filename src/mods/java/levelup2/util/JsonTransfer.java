package levelup2.util;

import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

public class JsonTransfer {
   public JsonTransfer() {
   }

   public static Set<String> findResources(String filepath, Set<String> fileNames) {
      Set<String> files = new HashSet();

      for(String name : fileNames) {
         String str = filepath + "/" + name + ".json";
         files.add(str);
      }

      return files;
   }

   public static void copyResource(String from, Path to, boolean overwrite) {
      try {
         if (overwrite || !Files.exists(to, new LinkOption[0])) {
            Files.createDirectories(to.getParent());
            Files.copy(getResourcesAsStream(from), to, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   public static InputStream getResourcesAsStream(String location) {
      InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("/" + location);
      return in != null ? in : JsonTransfer.class.getResourceAsStream("/" + location);
   }
}
