package betterquesting.client.toolbox.tools;

import betterquesting.api.client.toolbox.IToolboxTool;
import betterquesting.api2.client.gui.controls.PanelButtonQuest;
import betterquesting.api2.client.gui.panels.lists.CanvasQuestLine;
import betterquesting.api2.storage.DBEntry;
import betterquesting.client.gui2.editors.designer.PanelToolController;
import betterquesting.network.handlers.NetQuestEdit;
import java.util.Collections;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class ToolboxToolComplete implements IToolboxTool {
   private CanvasQuestLine gui = null;

   public ToolboxToolComplete() {
   }

   public void initTool(CanvasQuestLine gui) {
      this.gui = gui;
   }

   public void disableTool() {
   }

   public void refresh(CanvasQuestLine gui) {
   }

   public void drawCanvas(int mx, int my, float partialTick) {
   }

   public void drawOverlay(int mx, int my, float partialTick) {
   }

   public List<String> getTooltip(int mx, int my) {
      return null;
   }

   public boolean onMouseClick(int mx, int my, int click) {
      if (click == 0 && this.gui.getTransform().contains(mx, my)) {
         PanelButtonQuest btn = this.gui.getButtonAt(mx, my);
         if (btn == null) {
            return false;
         } else if (PanelToolController.selected.size() > 0 && !PanelToolController.selected.contains(btn)) {
            return false;
         } else {
            List<PanelButtonQuest> btnList = (List<PanelButtonQuest>)(PanelToolController.selected.size() > 0 ? PanelToolController.selected : Collections.singletonList(btn));
            int[] questIDs = new int[btnList.size()];

            for(int i = 0; i < btnList.size(); ++i) {
               questIDs[i] = ((DBEntry)((PanelButtonQuest)btnList.get(i)).getStoredValue()).getID();
            }

            NBTTagCompound payload = new NBTTagCompound();
            payload.setIntArray("questIDs", questIDs);
            payload.setBoolean("state", true);
            payload.setInteger("action", 2);
            NetQuestEdit.sendEdit(payload);
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean onMouseRelease(int mx, int my, int click) {
      return false;
   }

   public boolean onMouseScroll(int mx, int my, int scroll) {
      return false;
   }

   public boolean onKeyPressed(char c, int key) {
      if (PanelToolController.selected.size() > 0 && key == 28) {
         List<PanelButtonQuest> btnList = PanelToolController.selected;
         int[] questIDs = new int[btnList.size()];

         for(int i = 0; i < btnList.size(); ++i) {
            questIDs[i] = ((DBEntry)((PanelButtonQuest)btnList.get(i)).getStoredValue()).getID();
         }

         NBTTagCompound payload = new NBTTagCompound();
         payload.setIntArray("questIDs", questIDs);
         payload.setBoolean("state", true);
         payload.setInteger("action", 2);
         NetQuestEdit.sendEdit(payload);
         return true;
      } else {
         return false;
      }
   }

   public boolean clampScrolling() {
      return true;
   }

   public void onSelection(NonNullList<PanelButtonQuest> buttons) {
   }

   public boolean useSelection() {
      return true;
   }
}
