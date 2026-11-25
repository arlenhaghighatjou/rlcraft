package org.orecruncher.lib;

import javax.annotation.Nonnull;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.versioning.ComparableVersion;

public final class VersionHelper {
   private VersionHelper() {
   }

   public static int compareVersions(@Nonnull String v1, @Nonnull String v2) {
      if (!StringUtils.isNullOrEmpty(v1) && v1.charAt(0) != '@') {
         if (!StringUtils.isNullOrEmpty(v2) && v2.charAt(0) != '@') {
            ComparableVersion ver1 = new ComparableVersion(v1);
            ComparableVersion ver2 = new ComparableVersion(v2);
            return ver1.compareTo(ver2);
         } else {
            return 1;
         }
      } else {
         return -1;
      }
   }
}
