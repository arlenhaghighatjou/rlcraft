package betterquesting.api2.client.gui.misc;

public class GuiPadding {
   public int l;
   public int t;
   public int r;
   public int b;

   public GuiPadding() {
      this(0, 0, 0, 0);
   }

   public GuiPadding(int left, int top, int right, int bottom) {
      this.b = 0;
      this.setPadding(left, top, right, bottom);
   }

   public GuiPadding copy() {
      return new GuiPadding(this.l, this.t, this.r, this.b);
   }

   public GuiPadding setPadding(int left, int top, int right, int bottom) {
      this.l = left;
      this.t = top;
      this.r = right;
      this.b = bottom;
      return this;
   }

   public int getLeft() {
      return this.l;
   }

   public int getTop() {
      return this.t;
   }

   public int getRight() {
      return this.r;
   }

   public int getBottom() {
      return this.b;
   }

   public String toString() {
      return this.getClass().getName() + "[left=" + this.l + ",top=" + this.t + ",right=" + this.r + ",bottom=" + this.b + "]";
   }
}
