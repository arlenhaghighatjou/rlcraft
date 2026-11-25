package com.charles445.rltweaker.util;

import javax.annotation.Nullable;

public class VersionDelimiter {
   public static final VersionDelimiter UNKOWN = new VersionDelimiter(0, 0, 0, (String)null);
   public final int major;
   public final int minor;
   public final int patch;
   public final String classifier;

   public VersionDelimiter(int major, int minor, int patch) {
      this(major, minor, patch, (String)null);
   }

   public VersionDelimiter(int major, int minor, int patch, @Nullable String classifier) {
      this.major = major;
      this.minor = minor;
      this.patch = patch;
      this.classifier = classifier;
   }

   public static VersionDelimiter parse(String s) {
      if (s != null && !s.isEmpty()) {
         int i = s.indexOf(45);
         return i != -1 ? parse(s.substring(0, i), s.substring(i + 1)) : parse(s, (String)null);
      } else {
         return UNKOWN;
      }
   }

   private static VersionDelimiter parse(String s, @Nullable String classifier) {
      int i1 = s.indexOf(46);
      int i2 = s.indexOf(46, i1 + 1);
      if (i1 != -1 && i2 != -1) {
         try {
            String s1 = s.substring(0, i1);
            String s2 = s.substring(i1 + 1, i2);
            String s3 = s.substring(i2 + 1);
            return new VersionDelimiter(Integer.parseInt(s1), Integer.parseInt(s2), Integer.parseInt(s3), classifier);
         } catch (NumberFormatException var7) {
            return UNKOWN;
         }
      } else {
         return UNKOWN;
      }
   }

   public boolean isSameOrNewerVersion(VersionDelimiter vd) {
      return this.isSameOrNewerVersion(vd.major, vd.minor, vd.patch);
   }

   public boolean isSameOrNewerVersion(int major, int minor) {
      return this.isSameOrNewerVersion(major, minor, 0);
   }

   public boolean isSameOrNewerVersion(int major, int minor, int patch) {
      if (this.major > major) {
         return true;
      } else {
         if (this.major == major) {
            if (this.minor > minor) {
               return true;
            }

            if (this.minor == minor && this.patch >= patch) {
               return true;
            }
         }

         return false;
      }
   }

   public String toString() {
      return this.classifier != null ? String.format("%d.%d.%d-%s", this.major, this.minor, this.patch, this.classifier) : String.format("%d.%d.%d", this.major, this.minor, this.patch);
   }
}
