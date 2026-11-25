package net.ilexiconn.llibrary.client.gui.element;

import java.util.function.Supplier;
import net.ilexiconn.llibrary.LLibrary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ScrollbarElement<T extends IElementGUI> extends Element<T> {
   private int maxScroll;
   private float scrollPerEntry;
   private Supplier<Integer> entryCount;
   private float entryHeight;
   private Supplier<Float> offsetX;
   private Supplier<Float> offsetY;
   private Supplier<Float> displayHeight;
   private float scroll;
   private float scrollYOffset;
   private boolean scrolling;
   private float scrollVelocity;

   public ScrollbarElement(Element<T> parent, Supplier<Float> posX, Supplier<Float> posY, Supplier<Float> displayHeight, int entryHeight, Supplier<Integer> entryCount) {
      super(parent.gui, ((Float)posX.get()).floatValue(), ((Float)posY.get()).floatValue(), 4, 0);
      this.withParent(parent);
      this.offsetX = posX;
      this.offsetY = posY;
      this.entryHeight = (float)entryHeight;
      this.entryCount = entryCount;
      this.displayHeight = displayHeight;
   }

   public void render(float mouseX, float mouseY, float partialTicks) {
      if (this.maxScroll > 0) {
         this.drawRectangle((double)this.getPosX(), (double)this.getPosY(), (double)this.getWidth(), (double)(this.getHeight() - 3), this.scrolling ? LLibrary.CONFIG.getAccentColor() : LLibrary.CONFIG.getSecondaryColor());
      }

      this.scroll -= this.scrollVelocity;
      this.scrollVelocity *= 0.6F;
      if (this.scroll > (float)this.maxScroll / this.scrollPerEntry) {
         this.scroll = (float)((int)((float)this.maxScroll / this.scrollPerEntry));
      } else if (this.scroll < 0.0F) {
         this.scroll = 0.0F;
      }

   }

   public boolean mouseClicked(float mouseX, float mouseY, int button) {
      if (this.maxScroll > 0 && button == 0 && this.isSelected(mouseX, mouseY)) {
         this.scrolling = true;
         this.scrollYOffset = (float)((int)(mouseY - this.getPosY()));
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
      if (this.scrolling) {
         this.scroll = (float)((int)Math.max(0.0F, Math.min((float)this.maxScroll / this.scrollPerEntry, mouseY - this.scrollYOffset - (((Float)this.offsetY.get()).floatValue() + this.getParent().getPosY()))));
      }

      return this.scrolling;
   }

   public boolean mouseReleased(float mouseX, float mouseY, int button) {
      this.scrolling = false;
      return false;
   }

   public void update() {
      this.setPosX(((Float)this.offsetX.get()).floatValue());
      this.setPosY(((Float)this.offsetY.get()).floatValue() + this.scroll);
      float parentHeight = (float)this.getParent().getHeight();
      int maxDisplayEntries = (int)(parentHeight / this.entryHeight);
      int entryCount = ((Integer)this.entryCount.get()).intValue();
      this.maxScroll = Math.max(0, entryCount - maxDisplayEntries);
      this.scrollPerEntry = (float)entryCount / parentHeight;
      this.setHeight((int)(parentHeight / ((float)entryCount / (float)maxDisplayEntries)));
   }

   public float getScrollOffset() {
      return this.scroll * ((float)((Integer)this.entryCount.get()).intValue() / ((Float)this.displayHeight.get()).floatValue()) * this.entryHeight;
   }

   public boolean isScrolling() {
      return this.scrolling;
   }

   public int getMaxScroll() {
      return this.maxScroll;
   }

   public float getScrollVelocity() {
      return this.scrollVelocity;
   }

   public void setScrollVelocity(float scrollVelocity) {
      this.scrollVelocity = scrollVelocity;
   }
}
