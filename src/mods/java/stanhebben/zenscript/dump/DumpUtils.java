package stanhebben.zenscript.dump;

public class DumpUtils {
   public DumpUtils() {
   }

   public static String getClassNameFromPath(String path) {
      int lastChar = path.lastIndexOf(46);
      if (lastChar < 0) {
         lastChar = path.lastIndexOf(92);
      }

      if (lastChar < 0) {
         lastChar = path.lastIndexOf(47);
      }

      return lastChar < 0 ? path : path.substring(lastChar + 1, path.length());
   }
}
