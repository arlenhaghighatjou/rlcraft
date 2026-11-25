package betterquesting.api2.client.gui.panels;

import betterquesting.api2.client.gui.misc.ComparatorGuiDepth;
import betterquesting.api2.client.gui.misc.IGuiRect;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.annotation.Nonnull;

public class CanvasEmpty implements IGuiCanvas {
   private final List<IGuiPanel> guiPanels = new CopyOnWriteArrayList();
   private final IGuiRect transform;
   private boolean enabled = true;

   public CanvasEmpty(IGuiRect rect) {
      this.transform = rect;
   }

   public IGuiRect getTransform() {
      return this.transform;
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

   public void drawPanel(int mx, int my, float partialTick) {
      for(IGuiPanel entry : this.guiPanels) {
         if (entry.isEnabled()) {
            entry.drawPanel(mx, my, partialTick);
         }
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

      return used;
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

      return used;
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

      return used;
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

      return null;
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
