package com.charles445.simpledifficulty.util;

public class VersionDelimiter {
   public int major;
   public int minor;
   public int patch;

   public VersionDelimiter(String ss) {
      if (ss != null && !ss.isEmpty()) {
         String[] split = ss.split("\\.");
         if (split.length != 3) {
            this.wipeVersion();
         } else {
            try {
               this.major = Integer.parseInt(split[0]);
               this.minor = Integer.parseInt(split[1]);
               this.patch = Integer.parseInt(split[2]);
            } catch (NumberFormatException var4) {
               this.wipeVersion();
            }
         }
      } else {
         this.wipeVersion();
      }
   }

   public VersionDelimiter(int major, int minor, int patch) {
      this.major = major;
      this.minor = minor;
      this.patch = patch;
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

   private void wipeVersion() {
      this.major = 0;
      this.minor = 0;
      this.patch = 0;
   }

   public String toString() {
      return "" + this.major + "." + this.minor + "." + this.patch;
   }
}
