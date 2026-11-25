package com.endertech.minecraft.forge.versions;

public enum GameVersions {
   V7("1.7"),
   V10("1.10"),
   V11("1.11"),
   V12("1.12"),
   DEFAULT("");

   public final String prefix;

   private GameVersions(String beginsWith) {
      this.prefix = beginsWith;
   }

   public boolean isRunning() {
      return "1.12.2".startsWith(this.prefix);
   }

   public static GameVersions getCurrent() {
      for(GameVersions version : values()) {
         if (version.isRunning()) {
            return version;
         }
      }

      return DEFAULT;
   }
}
