package betterquesting.api2.client.gui.panels;

import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.misc.ComparatorGuiDepth;
import betterquesting.api2.client.gui.misc.GuiRectLerp;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;

public class CanvasResizeable implements IGuiCanvas {
   private final List<IGuiPanel> guiPanels;
   private IGuiTexture bgTexture;
   private final GuiRectLerp rectLerp;
   private boolean enabled;
   private final boolean crop;

   public CanvasResizeable(IGuiRect rect, IGuiTexture texture) {
      this(rect, texture, true);
   }

   public CanvasResizeable(IGuiRect rect, IGuiTexture texture, boolean crop) {
      this.guiPanels = new CopyOnWriteArrayList();
      this.enabled = true;
      this.bgTexture = texture;
      this.rectLerp = new GuiRectLerp(rect);
      this.crop = crop;
   }

   public void changeBG(@Nullable IGuiTexture texture) {
      this.bgTexture = texture;
   }

   public IGuiRect getTransform() {
      return (IGuiRect)(this.crop ? this.rectLerp.getProxyRect() : this.rectLerp);
   }

   public GuiRectLerp getRectLerp() {
      return this.rectLerp;
   }

   @Nonnull
   public List<IGuiPanel> getChildren() {
      return this.guiPanels;
   }

   public void initPanel() {
      this.guiPanels.clear();
   }

   public void setEnabled(boolean state) {
      this.enabled = state;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void lerpToRect(@Nonnull IGuiRect rect, long time, boolean inheritParent) {
      if (inheritParent) {
         rect.setParent(this.rectLerp.getParent());
      }

      this.rectLerp.lerpTo(rect, time);
   }

   public void snapToRect(@Nonnull IGuiRect rect, boolean inheritParent) {
      if (inheritParent) {
         rect.setParent(this.rectLerp.getParent());
      }

      this.rectLerp.snapTo(rect);
   }

   public void drawPanel(int mx, int my, float partialTick) {
      if (this.crop) {
         RenderUtils.startScissor(this.rectLerp);
      }

      if (this.bgTexture != null) {
         IGuiRect bounds = this.rectLerp;
         GlStateManager.pushMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.bgTexture.drawTexture(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 0.0F, partialTick);
         GlStateManager.popMatrix();
      }

      for(IGuiPanel entry : this.guiPanels) {
         if (entry.isEnabled()) {
            entry.drawPanel(mx, my, partialTick);
         }
      }

      if (this.crop) {
         RenderUtils.endScissor();
      }

   }

   public boolean onMouseClick(int mx, int my, int click) {
      boolean used = false;
      ListIterator<IGuiPanel> pnIter = this.guiPanels.listIterator(this.guiPanels.size());

      while(pnIter.hasPrevious()) {
         IGuiPanel entry = (IGuiPanel)pnIter.previous();
         if (entry.isEnabled() && entry.onMouseClick(mx, my, click)) {
            used = true;
            break;
         }
      }

      return used || this.bgTexture != null && this.rectLerp.contains(mx, my);
   }

   public boolean onMouseRelease(int mx, int my, int click) {
      boolean used = false;
      ListIterator<IGuiPanel> pnIter = this.guiPanels.listIterator(this.guiPanels.size());

      while(pnIter.hasPrevious()) {
         IGuiPanel entry = (IGuiPanel)pnIter.previous();
         if (entry.isEnabled() && entry.onMouseRelease(mx, my, click)) {
            used = true;
            break;
         }
      }

      return used || this.bgTexture != null && this.rectLerp.contains(mx, my);
   }

   public boolean onMouseScroll(int mx, int my, int scroll) {
      boolean used = false;
      ListIterator<IGuiPanel> pnIter = this.guiPanels.listIterator(this.guiPanels.size());

      while(pnIter.hasPrevious()) {
         IGuiPanel entry = (IGuiPanel)pnIter.previous();
         if (entry.isEnabled() && entry.onMouseScroll(mx, my, scroll)) {
            used = true;
            break;
         }
      }

      return used || this.bgTexture != null && this.rectLerp.contains(mx, my);
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
      ListIterator<IGuiPanel> pnIter = this.guiPanels.listIterator(this.guiPanels.size());

      while(pnIter.hasPrevious()) {
         IGuiPanel entry = (IGuiPanel)pnIter.previous();
         if (entry.isEnabled()) {
            List<String> tt = entry.getTooltip(mx, my);
            if (tt != null) {
               return tt;
            }
         }
      }

      return this.bgTexture != null && this.rectLerp.contains(mx, my) ? Collections.emptyList() : null;
   }

   public void addPanel(IGuiPanel panel) {
      if (panel != null && !this.guiPanels.contains(panel)) {
         this.guiPanels.add(panel);
         this.guiPanels.sort(ComparatorGuiDepth.INSTANCE);
         panel.getTransform().setParent(this.getTransform());
         panel.initPanel();
      }
   }

   public boolean removePanel(IGuiPanel panel) {
      return this.guiPanels.remove(panel);
   }

   public void resetCanvas() {
      this.guiPanels.clear();
   }
}
