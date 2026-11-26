package levelup2.util;

import java.util.HashSet;
import java.util.Set;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;

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

   public static void copyResource(String from, VFile2 to, boolean overwrite) {
      if (overwrite || !to.exists()) {
         byte[] data = getResourceBytes(from);
         if (data == null) {
            System.err.println("[LEVELUP] ERROR: Missing bundled resource " + from);
         } else {
            to.setAllBytes(data);
         }
      }

   }

   public static byte[] getResourceBytes(String location) {
      return EagRuntime.getResourceBytes("/levelup2/" + location);
   }
}
