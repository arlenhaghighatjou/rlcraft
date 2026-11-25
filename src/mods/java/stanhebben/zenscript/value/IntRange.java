package stanhebben.zenscript.value;

public class IntRange {
   private final int from;
   private final int to;

   public IntRange(int from, int to) {
      this.from = from;
      this.to = to;
   }

   public int getFrom() {
      return this.from;
   }

   public int getTo() {
      return this.to;
   }
}
