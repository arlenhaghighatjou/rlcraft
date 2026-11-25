package goblinbob.mobends.core.client.gui.elements;

public abstract class GuiPanel extends GuiElement {
   private boolean shown = true;
   private int width;
   private int height;
   private GuiPanel.Direction direction;

   public GuiPanel(GuiElement element, int x, int y, int width, int height, GuiPanel.Direction direction) {
      super(element, x, y);
      this.width = width;
      this.height = height;
      this.direction = direction;
   }

   public void drawChildren(float partialTicks) {
      float t = this.shown ? 0.0F : 1.0F;
      if (t != 1.0F) {
         super.drawChildren(partialTicks);
      }

   }

   public void setShown(boolean shown) {
      this.shown = shown;
   }

   public boolean isShown() {
      return this.shown;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public static enum Direction {
      UP,
      RIGHT,
      DOWN,
      LEFT;

      private Direction() {
      }
   }
}
