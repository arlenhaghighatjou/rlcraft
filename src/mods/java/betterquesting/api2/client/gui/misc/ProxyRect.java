package betterquesting.api2.client.gui.misc;

import javax.annotation.Nonnull;

public class ProxyRect implements IGuiRect {
   private IGuiRect ref;

   public ProxyRect(IGuiRect refRect) {
      this.ref = refRect;
   }

   public void changeReference(IGuiRect refRect) {
      this.ref = refRect;
   }

   public int getX() {
      return this.ref != null ? this.ref.getX() : 0;
   }

   public int getY() {
      return this.ref != null ? this.ref.getY() : 0;
   }

   public int getWidth() {
      return this.ref != null ? this.ref.getWidth() : 0;
   }

   public int getHeight() {
      return this.ref != null ? this.ref.getHeight() : 0;
   }

   public int getDepth() {
      return this.ref != null ? this.ref.getDepth() : 0;
   }

   public IGuiRect getParent() {
      return this.ref != null ? this.ref.getParent() : null;
   }

   public void setParent(IGuiRect rect) {
      if (this.ref != null) {
         this.ref.setParent(rect);
      }

   }

   public boolean contains(int x, int y) {
      return this.ref != null && this.ref.contains(x, y);
   }

   public int compareTo(@Nonnull IGuiRect o) {
      return this.ref != null ? this.ref.compareTo(o) : 0;
   }
}
