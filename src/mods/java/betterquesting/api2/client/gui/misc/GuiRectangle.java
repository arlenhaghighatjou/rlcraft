package betterquesting.api2.client.gui.misc;

import javax.annotation.Nonnull;

public class GuiRectangle implements IGuiRect {
   public int x;
   public int y;
   public int w;
   public int h;
   public int d;
   private IGuiRect parent;

   public GuiRectangle(int x, int y, int w, int h) {
      this(x, y, w, h, 0);
   }

   public GuiRectangle(int x, int y, int w, int h, int d) {
      this.parent = null;
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      this.d = d;
   }

   public GuiRectangle(IGuiRect rect) {
      this(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), rect.getDepth());
   }

   public int getX() {
      return this.x + (this.parent == null ? 0 : this.parent.getX());
   }

   public int getY() {
      return this.y + (this.parent == null ? 0 : this.parent.getY());
   }

   public int getWidth() {
      return this.w;
   }

   public int getHeight() {
      return this.h;
   }

   public int getDepth() {
      return this.d;
   }

   public IGuiRect getParent() {
      return this.parent;
   }

   public void setParent(IGuiRect rect) {
      this.parent = rect;
   }

   public boolean contains(int x, int y) {
      int x1 = this.getX();
      int x2 = x1 + this.getWidth();
      int y1 = this.getY();
      int y2 = y1 + this.getHeight();
      return x >= x1 && x < x2 && y >= y1 && y < y2;
   }

   public int compareTo(@Nonnull IGuiRect o) {
      return (int)Math.signum((float)(o.getDepth() - this.d));
   }
}
