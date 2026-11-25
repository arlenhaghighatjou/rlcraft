package betterquesting.api2.client.gui.panels.lists;

import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.controls.IValueIO;
import betterquesting.api2.client.gui.controls.io.FloatSimpleIO;
import betterquesting.api2.client.gui.misc.ComparatorGuiDepth;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiCanvas;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

public class CanvasScrolling implements IGuiCanvas {
   private final List<IGuiPanel> guiPanels = new CopyOnWriteArrayList();
   private final IGuiRect transform;
   private boolean enabled = true;
   protected final GuiRectangle scrollBounds = new GuiRectangle(0, 0, 0, 0);
   private final GuiRectangle scrollWindow = new GuiRectangle(0, 0, 0, 0);
   protected boolean extendedScroll = false;
   protected boolean zoomMode = false;
   protected int margin = 0;
   protected IValueIO<Float> scrollX;
   protected IValueIO<Float> scrollY;
   protected IValueIO<Float> zoomScale;
   private boolean isDragging = false;
   private boolean hasDragged = false;
   protected int scrollSpeed = 12;
   private float dragSX = 0.0F;
   private float dragSY = 0.0F;
   private int dragMX = 0;
   private int dragMY = 0;
   protected float lsz = 1.0F;
   protected int lsx = 0;
   protected int lsy = 0;
   private boolean useBlocking = true;
   private final CanvasCullingManager cullingManager = new CanvasCullingManager();
   private final GuiRectangle refRect = new GuiRectangle(0, 0, 0, 0);

   public CanvasScrolling(IGuiRect rect) {
      this.transform = rect;
      this.scrollX = (new FloatSimpleIO()).setLerp(true, 0.02F);
      this.scrollY = (new FloatSimpleIO()).setLerp(true, 0.02F);
      this.zoomScale = (new FloatSimpleIO(1.0F, 0.2F, 2.0F)).setLerp(true, 0.02F);
   }

   public CanvasScrolling setScrollDriverX(IValueIO<Float> driver) {
      this.scrollX = driver;
      return this;
   }

   public CanvasScrolling setScrollDriverY(IValueIO<Float> driver) {
      this.scrollY = driver;
      return this;
   }

   public CanvasScrolling setZoomDriver(IValueIO<Float> driver) {
      this.zoomScale = driver;
      return this;
   }

   public CanvasScrolling setScrollSpeed(int dx) {
      this.scrollSpeed = dx;
      return this;
   }

   public CanvasScrolling setupAdvanceScroll(boolean scrollToZoom, boolean extendedScroll, int scrollMargin) {
      this.zoomMode = scrollToZoom;
      this.extendedScroll = extendedScroll;
      this.margin = scrollMargin;
      return this;
   }

   public CanvasScrolling enableBlocking(boolean state) {
      this.useBlocking = state;
      return this;
   }

   public IGuiRect getScrollBounds() {
      return this.scrollBounds;
   }

   public int getScrollX() {
      return Math.round((float)this.scrollBounds.getX() + (float)this.scrollBounds.getWidth() * ((Float)this.scrollX.readValue()).floatValue());
   }

   public int getScrollY() {
      return Math.round((float)this.scrollBounds.getY() + (float)this.scrollBounds.getHeight() * ((Float)this.scrollY.readValue()).floatValue());
   }

   public float getZoom() {
      return ((Float)this.zoomScale.readValue()).floatValue();
   }

   public void setScrollX(int sx) {
      if (this.scrollBounds.getWidth() > 0) {
         this.scrollX.writeValueRaw(Float.valueOf((float)(sx - this.scrollBounds.getX()) / (float)this.scrollBounds.getWidth()));
         this.lsx = this.getScrollX();
      }
   }

   public void setScrollY(int sy) {
      if (this.scrollBounds.getHeight() > 0) {
         this.scrollY.writeValueRaw(Float.valueOf((float)(sy - this.scrollBounds.getY()) / (float)this.scrollBounds.getHeight()));
         this.lsy = this.getScrollY();
      }
   }

   public void setZoom(float z) {
      this.zoomScale.writeValueRaw(Float.valueOf(z));
      this.lsz = ((Float)this.zoomScale.readValue()).floatValue();
      this.refreshScrollBounds();
   }

   public void initPanel() {
      this.guiPanels.clear();
      this.cullingManager.reset();
   }

   public void setEnabled(boolean state) {
      this.enabled = state;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public IGuiRect getTransform() {
      return this.transform;
   }

   @Nonnull
   public List<IGuiPanel> getChildren() {
      return this.guiPanels;
   }

   public void drawPanel(int mx, int my, float partialTick) {
      if (!this.isRectEqual(this.refRect, this.transform)) {
         this.refreshScrollBounds();
      }

      float zs = ((Float)this.zoomScale.readValue()).floatValue();
      int tx = this.transform.getX();
      int ty = this.transform.getY();
      if (!this.isDragging || !Mouse.isButtonDown(0) && !Mouse.isButtonDown(2)) {
         if (this.isDragging || this.hasDragged) {
            this.isDragging = false;
            this.hasDragged = false;
         }
      } else {
         int dx = (int)((float)(this.dragMX - mx) / zs);
         int dy = (int)((float)(this.dragMY - my) / zs);
         if (this.scrollBounds.getWidth() > 0) {
            float dsx = (float)dx / (float)this.scrollBounds.getWidth() + this.dragSX;
            this.scrollX.writeValue(Float.valueOf(dsx));
            if (!this.hasDragged && Math.abs(this.dragSX - ((Float)this.scrollX.readValue()).floatValue()) > 0.05F) {
               this.hasDragged = true;
            }
         }

         if (this.scrollBounds.getHeight() > 0) {
            float dsy = (float)dy / (float)this.scrollBounds.getHeight() + this.dragSY;
            this.scrollY.writeValue(Float.valueOf(dsy));
            if (!this.hasDragged && Math.abs(this.dragSY - ((Float)this.scrollY.readValue()).floatValue()) > 0.05F) {
               this.hasDragged = true;
            }
         }
      }

      if (this.lsz != zs) {
         float change = zs / this.lsz;
         int csx = this.getScrollX();
         int csy = this.getScrollY();
         float swcx = (float)this.scrollWindow.w / 2.0F;
         float swcy = (float)this.scrollWindow.h / 2.0F;
         swcx = swcx - swcx / change;
         swcy = swcy - swcy / change;
         this.refreshScrollBounds();
         if (this.scrollBounds.getWidth() > 0) {
            this.scrollX.writeValue(Float.valueOf(((float)csx + swcx - (float)this.scrollBounds.getX()) / (float)this.scrollBounds.getWidth()));
         }

         if (this.scrollBounds.getHeight() > 0) {
            this.scrollY.writeValue(Float.valueOf(((float)csy + swcy - (float)this.scrollBounds.getY()) / (float)this.scrollBounds.getHeight()));
         }

         this.lsx = this.getScrollX();
         this.lsy = this.getScrollY();
         this.lsz = zs;
      } else if (this.lsx != this.getScrollX() || this.lsy != this.getScrollY()) {
         this.updatePanelScroll();
      }

      GlStateManager.pushMatrix();
      RenderUtils.startScissor(this.transform);
      GlStateManager.translate((float)tx - (float)this.lsx * zs, (float)ty - (float)this.lsy * zs, 0.0F);
      GlStateManager.scale(zs, zs, zs);
      int smx = (int)((float)(mx - tx) / zs) + this.lsx;
      int smy = (int)((float)(my - ty) / zs) + this.lsy;

      for(IGuiPanel panel : this.getVisiblePanels()) {
         if (panel.isEnabled()) {
            panel.drawPanel(smx, smy, partialTick);
         }
      }

      RenderUtils.endScissor();
      GlStateManager.popMatrix();
   }

   public boolean onMouseClick(int mx, int my, int click) {
      if (!this.transform.contains(mx, my)) {
         return false;
      } else {
         float zs = ((Float)this.zoomScale.readValue()).floatValue();
         int tx = this.transform.getX();
         int ty = this.transform.getY();
         int smx = (int)((float)(mx - tx) / zs) + this.lsx;
         int smy = (int)((float)(my - ty) / zs) + this.lsy;
         boolean used = false;
         ListIterator<IGuiPanel> pnIter = this.guiPanels.listIterator(this.guiPanels.size());

         while(pnIter.hasPrevious()) {
            IGuiPanel entry = (IGuiPanel)pnIter.previous();
            if (entry.isEnabled() && entry.onMouseClick(smx, smy, click)) {
               used = true;
               break;
            }
         }

         if (used || click != 0 && click != 2) {
            return used;
         } else {
            this.dragSX = ((Float)this.scrollX.readValue()).floatValue();
            this.dragSY = ((Float)this.scrollY.readValue()).floatValue();
            this.dragMX = mx;
            this.dragMY = my;
            this.isDragging = true;
            return true;
         }
      }
   }

   public boolean onMouseRelease(int mx, int my, int click) {
      boolean used = false;
      if (!this.hasDragged) {
         if (!this.transform.contains(mx, my)) {
            return false;
         }

         float zs = ((Float)this.zoomScale.readValue()).floatValue();
         int tx = this.transform.getX();
         int ty = this.transform.getY();
         int smx = (int)((float)(mx - tx) / zs) + this.lsx;
         int smy = (int)((float)(my - ty) / zs) + this.lsy;
         ListIterator<IGuiPanel> pnIter = this.guiPanels.listIterator(this.guiPanels.size());

         while(pnIter.hasPrevious()) {
            if (((IGuiPanel)pnIter.previous()).onMouseRelease(smx, smy, click)) {
               used = true;
               break;
            }
         }
      }

      if (this.isDragging) {
         if (!Mouse.isButtonDown(0) && !Mouse.isButtonDown(2)) {
            this.isDragging = false;
         }

         return true;
      } else {
         return used;
      }
   }

   public boolean onMouseScroll(int mx, int my, int scroll) {
      if (scroll != 0 && this.transform.contains(mx, my)) {
         float zs = ((Float)this.zoomScale.readValue()).floatValue();
         int tx = this.transform.getX();
         int ty = this.transform.getY();
         int smx = (int)((float)(mx - tx) / zs) + this.lsx;
         int smy = (int)((float)(my - ty) / zs) + this.lsy;
         boolean used = false;
         ListIterator<IGuiPanel> pnIter = this.guiPanels.listIterator(this.guiPanels.size());

         while(pnIter.hasPrevious()) {
            IGuiPanel entry = (IGuiPanel)pnIter.previous();
            if (entry.isEnabled() && entry.onMouseScroll(smx, smy, scroll)) {
               used = true;
               break;
            }
         }

         if (!used) {
            if (this.zoomMode) {
               float dy = (float)(-scroll) * 0.05F;
               float cs = ((Float)this.zoomScale.readValueRaw()).floatValue();
               this.zoomScale.writeValue(Float.valueOf(cs + dy));
               used = true;
            } else if (this.scrollBounds.getHeight() > 0) {
               float dy = (float)(scroll * this.scrollSpeed) / (float)this.scrollBounds.getHeight();
               float cs = ((Float)this.scrollY.readValue()).floatValue();
               if ((dy >= 0.0F || cs > 0.0F) && (dy <= 0.0F || cs < 1.0F)) {
                  this.scrollY.writeValue(Float.valueOf(cs + dy));
                  this.updatePanelScroll();
                  used = true;
               }
            } else if (this.scrollBounds.getWidth() > 0) {
               float dy = (float)(scroll * this.scrollSpeed) / (float)this.scrollBounds.getWidth();
               float cs = ((Float)this.scrollX.readValue()).floatValue();
               if ((dy >= 0.0F || cs > 0.0F) && (dy <= 0.0F || cs < 1.0F)) {
                  this.scrollX.writeValue(Float.valueOf(cs + dy));
                  this.updatePanelScroll();
                  used = true;
               }
            }
         }

         return used;
      } else {
         return false;
      }
   }

   public boolean onKeyTyped(char c, int keycode) {
      boolean used = false;
      ListIterator<IGuiPanel> pnIter = this.guiPanels.listIterator(this.guiPanels.size());

      while(pnIter.hasPrevious()) {
         IGuiPanel entry = (IGuiPanel)pnIter.previous();
         if (entry.isEnabled() && entry.onKeyTyped(c, keycode)) {
            used = true;
            break;
         }
      }

      return used;
   }

   public List<String> getTooltip(int mx, int my) {
      if (this.transform.contains(mx, my) && !this.isDragging) {
         float zs = ((Float)this.zoomScale.readValue()).floatValue();
         int tx = this.transform.getX();
         int ty = this.transform.getY();
         int smx = (int)((float)(mx - tx) / zs) + this.lsx;
         int smy = (int)((float)(my - ty) / zs) + this.lsy;
         List<IGuiPanel> tmpList = this.getVisiblePanels();
         ListIterator<IGuiPanel> pnIter = tmpList.listIterator(tmpList.size());

         while(pnIter.hasPrevious()) {
            IGuiPanel entry = (IGuiPanel)pnIter.previous();
            if (entry.isEnabled()) {
               List<String> tt = entry.getTooltip(smx, smy);
               if (tt != null && tt.size() > 0) {
                  return tt;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public void addPanel(IGuiPanel panel) {
      this.addCulledPanel(panel, true);
   }

   public void addCulledPanel(IGuiPanel panel, boolean useCulling) {
      if (panel != null && !this.guiPanels.contains(panel)) {
         this.guiPanels.add(panel);
         this.guiPanels.sort(ComparatorGuiDepth.INSTANCE);
         this.cullingManager.addPanel(panel, useCulling);
         panel.initPanel();
         this.refreshScrollBounds();
      }
   }

   public boolean removePanel(IGuiPanel panel) {
      boolean b = this.guiPanels.remove(panel);
      if (b) {
         this.cullingManager.removePanel(panel);
         this.refreshScrollBounds();
      }

      return b;
   }

   public void refreshScrollBounds() {
      boolean first = true;
      int left = 0;
      int right = 0;
      int top = 0;
      int bottom = 0;
      float zs = ((Float)this.zoomScale.readValue()).floatValue();

      for(IGuiPanel panel : this.guiPanels) {
         if (first) {
            left = panel.getTransform().getX();
            top = panel.getTransform().getY();
            right = panel.getTransform().getX() + panel.getTransform().getWidth();
            bottom = panel.getTransform().getY() + panel.getTransform().getHeight();
            first = false;
         } else {
            left = Math.min(left, panel.getTransform().getX());
            top = Math.min(top, panel.getTransform().getY());
            right = Math.max(right, panel.getTransform().getX() + panel.getTransform().getWidth());
            bottom = Math.max(bottom, panel.getTransform().getY() + panel.getTransform().getHeight());
         }
      }

      left = left - this.margin;
      right = right + this.margin;
      top = top - this.margin;
      bottom = bottom + this.margin;
      right = right - (int)Math.ceil((double)((float)this.getTransform().getWidth() / zs));
      bottom = bottom - (int)Math.ceil((double)((float)this.getTransform().getHeight() / zs));
      if (this.extendedScroll) {
         this.scrollBounds.x = Math.min(left, right);
         this.scrollBounds.y = Math.min(top, bottom);
         this.scrollBounds.w = Math.max(left, right) - this.scrollBounds.x;
         this.scrollBounds.h = Math.max(top, bottom) - this.scrollBounds.y;
      } else {
         this.scrollBounds.x = left;
         this.scrollBounds.y = top;
         this.scrollBounds.w = Math.max(0, right - left);
         this.scrollBounds.h = Math.max(0, bottom - top);
      }

      this.updatePanelScroll();
      this.refRect.x = this.transform.getX();
      this.refRect.y = this.transform.getY();
      this.refRect.w = this.transform.getWidth();
      this.refRect.h = this.transform.getHeight();
   }

   public void updatePanelScroll() {
      this.lsx = this.getScrollX();
      this.lsy = this.getScrollY();
      float zs = ((Float)this.zoomScale.readValue()).floatValue();
      this.scrollWindow.x = this.lsx;
      this.scrollWindow.y = this.lsy;
      this.scrollWindow.w = (int)Math.ceil((double)((float)this.transform.getWidth() / zs));
      this.scrollWindow.h = (int)Math.ceil((double)((float)this.transform.getHeight() / zs));
      this.cullingManager.updateVisiblePanels(this.scrollWindow);
   }

   public void resetCanvas() {
      this.guiPanels.clear();
      this.cullingManager.reset();
      this.refreshScrollBounds();
   }

   private List<IGuiPanel> getVisiblePanels() {
      return this.useBlocking ? this.cullingManager.getVisiblePanels() : this.guiPanels;
   }

   private boolean isRectEqual(IGuiRect r1, IGuiRect r2) {
      return r1.getX() == r2.getX() && r1.getY() == r2.getY() && r1.getWidth() == r2.getWidth() && r1.getHeight() == r2.getHeight();
   }
}
