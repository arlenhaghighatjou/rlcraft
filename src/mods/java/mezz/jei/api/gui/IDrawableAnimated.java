package mezz.jei.api.gui;

public interface IDrawableAnimated extends IDrawable {
   public static enum StartDirection {
      TOP,
      BOTTOM,
      LEFT,
      RIGHT;

      private StartDirection() {
      }
   }
}
