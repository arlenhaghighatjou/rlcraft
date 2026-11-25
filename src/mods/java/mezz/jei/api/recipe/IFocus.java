package mezz.jei.api.recipe;

public interface IFocus<V> {
   V getValue();

   IFocus.Mode getMode();

   public static enum Mode {
      INPUT,
      OUTPUT;

      private Mode() {
      }
   }
}
