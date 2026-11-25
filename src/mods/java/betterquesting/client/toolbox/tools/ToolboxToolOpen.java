package betterquesting.client.toolbox.tools;

import betterquesting.api.client.toolbox.IToolboxTool;
import betterquesting.api2.client.gui.controls.PanelButtonQuest;
import betterquesting.api2.client.gui.panels.lists.CanvasQuestLine;
import betterquesting.api2.storage.DBEntry;
import betterquesting.client.gui2.GuiQuest;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.NonNullList;

public class ToolboxToolOpen implements IToolboxTool {
   private CanvasQuestLine gui;

   public ToolboxToolOpen() {
   }

   public void initTool(CanvasQuestLine gui) {
      this.gui = gui;
   }

   public void disableTool() {
   }

   public void refresh(CanvasQuestLine gui) {
   }

   public boolean onMouseClick(int mx, int my, int click) {
      if (click == 0 && this.gui.getTransform().contains(mx, my)) {
         PanelButtonQuest btn = this.gui.getButtonAt(mx, my);
         if (btn != null) {
            int qID = ((DBEntry)btn.getStoredValue()).getID();
            Minecraft mc = Minecraft.getMinecraft();
            mc.displayGuiScreen(new GuiQuest(mc.currentScreen, qID));
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean onMouseRelease(int mx, int my, int click) {
      return false;
   }

   public void drawCanvas(int mx, int my, float partialTick) {
   }

   public void drawOverlay(int mx, int my, float partialTick) {
   }

   public List<String> getTooltip(int mx, int my) {
      return null;
   }

   public boolean onMouseScroll(int mx, int my, int scroll) {
      return false;
   }

   public boolean onKeyPressed(char c, int key) {
      return false;
   }

   public boolean clampScrolling() {
      return true;
   }

   public void onSelection(NonNullList<PanelButtonQuest> buttons) {
   }

   public boolean useSelection() {
      return false;
   }
}
