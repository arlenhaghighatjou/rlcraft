package yalter.mousetweaks;

public enum ScrollItemScaling {
   PROPORTIONAL(0),
   ALWAYS_ONE(1);

   public static final int scrollStep = 120;
   private final int id;

   private ScrollItemScaling(int id) {
      this.id = id;
   }

   public int getValue() {
      return this.id;
   }

   public static ScrollItemScaling fromId(int id) {
      return id == PROPORTIONAL.id ? PROPORTIONAL : ALWAYS_ONE;
   }

   public int scale(int scrollDelta) {
      switch(this) {
      case PROPORTIONAL:
         return scrollDelta;
      case ALWAYS_ONE:
         return Integer.signum(scrollDelta) * 120;
      default:
         throw new AssertionError();
      }
   }
}
