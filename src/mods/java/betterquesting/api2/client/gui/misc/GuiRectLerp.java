package betterquesting.api2.client.gui.misc;

import betterquesting.api.utils.RenderUtils;
import javax.annotation.Nonnull;

public class GuiRectLerp implements IGuiRect {
   private IGuiRect startRect;
   private IGuiRect targetRect;
   private long duration = 200L;
   private long et;
   private final ProxyRect pxRect;

   public GuiRectLerp(@Nonnull IGuiRect start) {
      this.startRect = start;
      this.targetRect = start;
      this.pxRect = new ProxyRect(start);
      this.et = System.currentTimeMillis();
   }

   public void lerpTo(@Nonnull IGuiRect target, long time) {
      if (time > 0L) {
         this.targetRect = target;
         this.duration = time;
         this.et = System.currentTimeMillis();
         this.pxRect.changeReference(target);
      }
   }

   public void snapTo(@Nonnull IGuiRect target) {
      this.startRect = target;
      this.targetRect = target;
      this.et = System.currentTimeMillis();
      this.pxRect.changeReference(target);
   }

   public IGuiRect getProxyRect() {
      return this.pxRect;
   }

   public boolean isIdle() {
      if (this.startRect == this.targetRect) {
         return true;
      } else if (System.currentTimeMillis() - this.et >= this.duration) {
         this.startRect = this.targetRect;
         return true;
      } else {
         return false;
      }
   }

   public int getX() {
      return this.isIdle() ? this.targetRect.getX() : (int)Math.round(RenderUtils.lerpDouble((double)this.startRect.getX(), (double)this.targetRect.getX(), (double)(System.currentTimeMillis() - this.et) / (double)this.duration));
   }

   public int getY() {
      return this.isIdle() ? this.targetRect.getY() : (int)Math.round(RenderUtils.lerpDouble((double)this.startRect.getY(), (double)this.targetRect.getY(), (double)(System.currentTimeMillis() - this.et) / (double)this.duration));
   }

   public int getWidth() {
      return this.isIdle() ? this.targetRect.getWidth() : (int)Math.round(RenderUtils.lerpDouble((double)this.startRect.getWidth(), (double)this.targetRect.getWidth(), (double)(System.currentTimeMillis() - this.et) / (double)this.duration));
   }

   public int getHeight() {
      return this.isIdle() ? this.targetRect.getHeight() : (int)Math.round(RenderUtils.lerpDouble((double)this.startRect.getHeight(), (double)this.targetRect.getHeight(), (double)(System.currentTimeMillis() - this.et) / (double)this.duration));
   }

   public int getDepth() {
      return this.targetRect.getDepth();
   }

   public IGuiRect getParent() {
      return this.targetRect.getParent();
   }

   public void setParent(IGuiRect rect) {
      this.targetRect.setParent(rect);
   }

   public boolean contains(int x, int y) {
      return this.targetRect.contains(x, y);
   }

   public int compareTo(@Nonnull IGuiRect o) {
      return this.targetRect.compareTo(o);
   }
}
