package betterquesting.client.toolbox;

import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.panels.lists.CanvasQuestLine;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.lines.IGuiLine;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetLine;
import betterquesting.api2.client.toolbox.IToolTab;
import betterquesting.client.gui2.editors.designer.PanelToolController;

public class ToolboxTabMain implements IToolTab {
   public static final ToolboxTabMain INSTANCE = new ToolboxTabMain();
   private int dragSnap = 4;
   private int[] snaps = new int[]{1, 4, 6, 8, 12, 16, 24, 32};

   public ToolboxTabMain() {
   }

   public String getUnlocalisedName() {
      return "betterquesting.toolbox.tab.main";
   }

   public IGuiPanel getTabGui(IGuiRect rect, CanvasQuestLine cvQuestLine, PanelToolController toolController) {
      return new PanelTabMain(rect, cvQuestLine, toolController);
   }

   public void toggleSnap() {
      this.dragSnap = (this.dragSnap + 1) % this.snaps.length;
   }

   public int getSnapValue() {
      return this.snaps[this.dragSnap % this.snaps.length];
   }

   public int getSnapIndex() {
      return this.dragSnap;
   }

   public void drawGrid(CanvasQuestLine ui) {
      if (this.getSnapValue() > 1) {
         float zs = ui.getZoom();
         int snap = this.getSnapValue();
         float offX = (float)(-ui.getScrollX());
         offX = (offX % (float)snap + (float)snap) % (float)snap * zs;
         int midX = Math.floorDiv(-ui.getScrollX(), snap);
         float offY = (float)(-ui.getScrollY());
         offY = (offY % (float)snap + (float)snap) % (float)snap * zs;
         int midY = Math.floorDiv(-ui.getScrollY(), snap);
         int x = ui.getTransform().getX();
         int y = ui.getTransform().getY();
         int width = ui.getTransform().getWidth();
         int height = ui.getTransform().getHeight();
         int divX = (int)Math.ceil((double)(((float)width - offX) / (zs * (float)snap)));
         int divY = (int)Math.ceil((double)(((float)height - offY) / (zs * (float)snap)));
         IGuiColor gMinor = PresetColor.GRID_MINOR.getColor();
         IGuiColor gMajor = PresetColor.GRID_MAJOR.getColor();
         IGuiLine lMinor = PresetLine.GRID_MINOR.getLine();
         IGuiLine lMajor = PresetLine.GRID_MAJOR.getLine();
         GuiRectangle p1 = new GuiRectangle(0, 0, 0, 0);
         GuiRectangle p2 = new GuiRectangle(0, 0, 0, 0);
         p1.y = y;
         p2.y = y + height;

         for(int i = 0; i < divX; ++i) {
            int lx = x + (int)((float)(i * snap) * zs + offX);
            p1.x = lx;
            p2.x = lx;
            if (i == midX) {
               lMajor.drawLine(p1, p2, 2, gMajor, 1.0F);
            } else {
               lMinor.drawLine(p1, p2, 1, gMinor, 1.0F);
            }
         }

         p1.x = x;
         p2.x = x + width;

         for(int j = 0; j < divY; ++j) {
            int ly = y + (int)((float)(j * snap) * zs + offY);
            p1.y = ly;
            p2.y = ly;
            if (j == midY) {
               lMajor.drawLine(p1, p2, 2, gMajor, 1.0F);
            } else {
               lMinor.drawLine(p1, p2, 1, gMinor, 1.0F);
            }
         }

      }
   }
}
