package crafttweaker.util;

public enum SuppressErrorFlag {
   DEFAULT,
   ONLY_WARNINGS,
   ALL,
   FORCED;

   private SuppressErrorFlag() {
   }

   public boolean isForced() {
      return this.equals(FORCED);
   }

   public boolean isSuppressingWarnings() {
      return this.equals(ONLY_WARNINGS) || this.equals(ALL);
   }

   public boolean isSuppressingErrors() {
      return this.equals(ALL);
   }
}
