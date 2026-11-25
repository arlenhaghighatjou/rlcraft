package prospector.shootingstar.version;

public class VersionUtils {
   public VersionUtils() {
   }

   public static boolean isVersionLessOrEqual(Version comparate1, Version comparate2) {
      if (comparate1.major > comparate2.major) {
         return false;
      } else if (comparate1.major == comparate2.major) {
         if (comparate1.minor > comparate2.minor) {
            return false;
         } else {
            return comparate1.major != comparate2.major || comparate1.minor != comparate2.minor || comparate1.patch <= comparate2.patch;
         }
      } else {
         return true;
      }
   }
}
