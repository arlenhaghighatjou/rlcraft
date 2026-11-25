package io.github.xcube16.iseedragons.asm;

public class PatchResult {
   public static PatchResult NO_MUTATION = new PatchResult(-1);
   public static PatchResult NO_FLAGS = new PatchResult(0);
   public static PatchResult MAXS = new PatchResult(1);
   public static PatchResult MAXS_FRAMES = new PatchResult(3);
   public static PatchResult FRAMES = new PatchResult(2);
   private final int flags;

   public PatchResult(int flags) {
      this.flags = flags;
   }

   public int getFlags() {
      return Math.max(this.flags, 0);
   }

   public boolean isMutated() {
      return this.flags >= 0;
   }

   public PatchResult add(PatchResult other) {
      return new PatchResult(!this.isMutated() && !other.isMutated() ? -1 : this.getFlags() | other.getFlags());
   }

   public boolean equals(Object other) {
      return other instanceof PatchResult && Math.max(-1, ((PatchResult)other).flags) == Math.max(-1, this.flags);
   }

   public int hashCode() {
      return Integer.hashCode(Math.max(-1, this.flags));
   }

   public String toString() {
      switch(this.getFlags()) {
      case 0:
         return "None";
      case 1:
         return "MAXS";
      case 2:
         return "FRAMES";
      case 3:
         return "MAXS | FRAMES";
      default:
         return "(unknown " + this.getFlags() + ")";
      }
   }
}
