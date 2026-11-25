package betterquesting.client.toolbox.tools;

import betterquesting.api.client.toolbox.IToolboxTool;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api2.client.gui.controls.PanelButtonQuest;
import betterquesting.api2.client.gui.panels.lists.CanvasQuestLine;
import betterquesting.api2.storage.DBEntry;
import betterquesting.client.gui2.editors.designer.PanelToolController;
import betterquesting.network.handlers.NetChapterEdit;
import betterquesting.questing.QuestLineDatabase;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

public class ToolboxToolRemove implements IToolboxTool {
   private CanvasQuestLine gui;

   public ToolboxToolRemove() {
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
         IQuestLine line = this.gui.getQuestLine();
         PanelButtonQuest btn = this.gui.getButtonAt(mx, my);
         if (line != null && btn != null) {
            if (PanelToolController.selected.size() > 0) {
               if (!PanelToolController.selected.contains(btn)) {
                  return false;
               }

               for(PanelButtonQuest b : PanelToolController.selected) {
                  line.removeID(((DBEntry)b.getStoredValue()).getID());
               }
            } else {
               int qID = ((DBEntry)btn.getStoredValue()).getID();
               line.removeID(qID);
            }

            NBTTagCompound chPayload = new NBTTagCompound();
            NBTTagList cdList = new NBTTagList();
            NBTTagCompound cTag = new NBTTagCompound();
            cTag.setInteger("chapterID", QuestLineDatabase.INSTANCE.getID(line));
            cTag.setTag("config", line.writeToNBT(new NBTTagCompound(), (List)null));
            cdList.appendTag(cTag);
            chPayload.setTag("data", cdList);
            chPayload.setInteger("action", 0);
            NetChapterEdit.sendEdit(chPayload);
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
      if (PanelToolController.selected.size() > 0 && key == 28) {
         IQuestLine line = this.gui.getQuestLine();

         for(PanelButtonQuest b : PanelToolController.selected) {
            line.removeID(((DBEntry)b.getStoredValue()).getID());
         }

         NBTTagCompound chPayload = new NBTTagCompound();
         NBTTagList cdList = new NBTTagList();
         NBTTagCompound cTag = new NBTTagCompound();
         cTag.setInteger("chapterID", QuestLineDatabase.INSTANCE.getID(line));
         cTag.setTag("config", line.writeToNBT(new NBTTagCompound(), (List)null));
         cdList.appendTag(cTag);
         chPayload.setTag("data", cdList);
         chPayload.setInteger("action", 0);
         NetChapterEdit.sendEdit(chPayload);
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
