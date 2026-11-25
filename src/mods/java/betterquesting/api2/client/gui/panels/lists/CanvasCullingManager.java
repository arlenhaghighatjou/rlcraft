package betterquesting.api2.client.gui.panels.lists;

import betterquesting.api2.client.gui.misc.ComparatorGuiDepth;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class CanvasCullingManager {
   private final List<IGuiPanel> dynamicPanels;
   private final Map<String, CanvasCullingManager.RegionInfo> panelRegions;
   private final List<IGuiPanel> cachedPanels;
   private final int gridSize;

   public CanvasCullingManager() {
      this(128);
   }

   public CanvasCullingManager(int gridSize) {
      this.dynamicPanels = new ArrayList();
      this.panelRegions = new HashMap();
      this.cachedPanels = new CopyOnWriteArrayList();
      this.gridSize = gridSize;
   }

   public void reset() {
      this.dynamicPanels.clear();
      this.panelRegions.clear();
      this.cachedPanels.clear();
   }

   public void addPanel(IGuiPanel panel, boolean useBlocks) {
      if (!useBlocks) {
         if (!this.dynamicPanels.contains(panel)) {
            this.dynamicPanels.add(panel);
            this.cachedPanels.add(panel);
         }
      } else {
         int x = panel.getTransform().getX();
         int y = panel.getTransform().getY();
         x = (x - (x % this.gridSize + this.gridSize) % this.gridSize) / this.gridSize;
         y = (y - (y % this.gridSize + this.gridSize) % this.gridSize) / this.gridSize;
         CanvasCullingManager.RegionInfo cbl = (CanvasCullingManager.RegionInfo)this.panelRegions.get(x + "," + y);
         if (cbl != null) {
            if (!cbl.panels.contains(panel)) {
               cbl.panels.add(panel);
               int minX = Math.min(cbl.rect.x, panel.getTransform().getX());
               int minY = Math.min(cbl.rect.y, panel.getTransform().getY());
               int maxX = Math.max(cbl.rect.x + cbl.rect.w, panel.getTransform().getX() + panel.getTransform().getWidth());
               int maxY = Math.max(cbl.rect.y + cbl.rect.h, panel.getTransform().getY() + panel.getTransform().getHeight());
               cbl.rect.x = minX;
               cbl.rect.y = minY;
               cbl.rect.w = maxX - minX;
               cbl.rect.h = maxY - minY;
               if (cbl.enabled) {
                  this.cachedPanels.add(panel);
                  this.cachedPanels.sort(ComparatorGuiDepth.INSTANCE);
               }
            }
         } else {
            cbl = new CanvasCullingManager.RegionInfo(x, y, this.gridSize);
            int minX = Math.min(cbl.rect.x, panel.getTransform().getX());
            int minY = Math.min(cbl.rect.y, panel.getTransform().getY());
            int maxX = Math.max(cbl.rect.x + cbl.rect.w, panel.getTransform().getX() + panel.getTransform().getWidth());
            int maxY = Math.max(cbl.rect.y + cbl.rect.h, panel.getTransform().getY() + panel.getTransform().getHeight());
            cbl.rect.x = minX;
            cbl.rect.y = minY;
            cbl.rect.w = maxX - minX;
            cbl.rect.h = maxY - minY;
            cbl.panels.add(panel);
            this.panelRegions.put(x + "," + y, cbl);
         }
      }

   }

   public void removePanel(IGuiPanel panel) {
      if (!this.dynamicPanels.remove(panel)) {
         for(CanvasCullingManager.RegionInfo cbl : this.panelRegions.values()) {
            if (cbl.panels.remove(panel)) {
               cbl.refreshBounds();
               this.cachedPanels.remove(panel);
               break;
            }
         }
      } else {
         this.cachedPanels.remove(panel);
      }

   }

   public List<IGuiPanel> getVisiblePanels() {
      return this.cachedPanels;
   }

   public void updateVisiblePanels(IGuiRect region) {
      boolean changed = false;

      for(CanvasCullingManager.RegionInfo cb : this.panelRegions.values()) {
         boolean prevState = cb.enabled;
         cb.enabled = overlapCheck(cb.rect, region);
         if (prevState != cb.enabled) {
            if (cb.enabled) {
               this.cachedPanels.addAll(cb.panels);
            } else {
               this.cachedPanels.removeAll(cb.panels);
            }

            changed = true;
         }
      }

      if (changed) {
         this.cachedPanels.sort(ComparatorGuiDepth.INSTANCE);
      }

   }

   private static boolean overlapCheck(IGuiRect rect1, IGuiRect rect2) {
      if (rect1.getX() + rect1.getWidth() >= rect2.getX() && rect1.getX() <= rect2.getX() + rect2.getWidth()) {
         return rect1.getY() + rect1.getHeight() >= rect2.getY() && rect1.getY() <= rect2.getY() + rect2.getHeight();
      } else {
         return false;
      }
   }

   private static class RegionInfo {
      private final List<IGuiPanel> panels;
      private boolean enabled;
      private final GuiRectangle rect;

      private RegionInfo(int blockX, int blockY, int gridSize) {
         this.panels = new ArrayList();
         this.enabled = false;
         this.rect = new GuiRectangle(blockX * gridSize, blockY * gridSize, gridSize, gridSize);
      }

      private void refreshBounds() {
         boolean set = false;
         int minX = 0;
         int minY = 0;
         int maxX = 0;
         int maxY = 0;

         for(IGuiPanel pan : this.panels) {
            if (!set) {
               set = true;
               minX = pan.getTransform().getX();
               minY = pan.getTransform().getY();
               maxX = minX + pan.getTransform().getWidth();
               maxY = minY + pan.getTransform().getHeight();
            } else {
               minX = Math.min(minX, pan.getTransform().getX());
               minY = Math.min(minY, pan.getTransform().getY());
               maxX = Math.max(maxX, pan.getTransform().getX() + pan.getTransform().getWidth());
               maxY = Math.max(maxY, pan.getTransform().getY() + pan.getTransform().getHeight());
            }
         }

         this.rect.x = minX;
         this.rect.y = minY;
         this.rect.w = maxX - minX;
         this.rect.h = maxY - minY;
      }
   }
}
