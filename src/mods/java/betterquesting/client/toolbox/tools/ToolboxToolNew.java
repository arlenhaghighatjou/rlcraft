package betterquesting.client.toolbox.tools;

import betterquesting.api.client.toolbox.IToolboxTool;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.questing.IQuestLineEntry;
import betterquesting.api2.client.gui.controls.PanelButtonQuest;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.panels.lists.CanvasQuestLine;
import betterquesting.api2.storage.DBEntry;
import betterquesting.client.toolbox.ToolboxTabMain;
import betterquesting.network.handlers.NetChapterEdit;
import betterquesting.network.handlers.NetQuestEdit;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.QuestLineDatabase;
import betterquesting.questing.QuestLineEntry;
import java.util.Collections;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

public class ToolboxToolNew implements IToolboxTool {
   private CanvasQuestLine gui = null;
   private PanelButtonQuest nQuest;

   public ToolboxToolNew() {
   }

   public void initTool(CanvasQuestLine gui) {
      this.gui = gui;
      this.nQuest = new PanelButtonQuest(new GuiRectangle(0, 0, 24, 24), -1, "", (DBEntry)null);
   }

   public void refresh(CanvasQuestLine gui) {
   }

   public void drawCanvas(int mx, int my, float partialTick) {
      if (this.nQuest != null) {
         int snap = ToolboxTabMain.INSTANCE.getSnapValue();
         int modX = (mx % snap + snap) % snap;
         int modY = (my % snap + snap) % snap;
         mx = mx - modX;
         my = my - modY;
         this.nQuest.rect.x = mx;
         this.nQuest.rect.y = my;
         this.nQuest.drawPanel(mx, my, partialTick);
      }
   }

   public void drawOverlay(int mx, int my, float partialTick) {
      ToolboxTabMain.INSTANCE.drawGrid(this.gui);
   }

   public List<String> getTooltip(int mx, int my) {
      return Collections.emptyList();
   }

   public void disableTool() {
      if (this.nQuest != null) {
         this.nQuest = null;
      }

   }

   public boolean onMouseClick(int mx, int my, int click) {
      if (click == 0 && this.gui.getTransform().contains(mx, my)) {
         IQuestLine qLine = this.gui.getQuestLine();
         int qID = QuestDatabase.INSTANCE.nextID();
         int lID = QuestLineDatabase.INSTANCE.getID(qLine);
         IQuestLineEntry qe = (IQuestLineEntry)qLine.getValue(qID);
         if (qe == null) {
            IQuestLineEntry var14 = new QuestLineEntry(this.nQuest.rect.x, this.nQuest.rect.y, 24, 24);
            qLine.add(qID, var14);
         } else {
            qe.setPosition(this.nQuest.rect.x, this.nQuest.rect.y);
            qe.setSize(24, 24);
         }

         NBTTagCompound quPayload = new NBTTagCompound();
         NBTTagList qdList = new NBTTagList();
         NBTTagCompound qTag = new NBTTagCompound();
         qTag.setInteger("questID", qID);
         qdList.appendTag(qTag);
         quPayload.setTag("data", qdList);
         quPayload.setInteger("action", 3);
         NetQuestEdit.sendEdit(quPayload);
         NBTTagCompound chPayload = new NBTTagCompound();
         NBTTagList cdList = new NBTTagList();
         NBTTagCompound cTag = new NBTTagCompound();
         cTag.setInteger("chapterID", lID);
         cTag.setTag("config", qLine.writeToNBT(new NBTTagCompound(), (List)null));
         cdList.appendTag(cTag);
         chPayload.setTag("data", cdList);
         chPayload.setInteger("action", 0);
         NetChapterEdit.sendEdit(chPayload);
         return true;
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

   public boolean onKeyPressed(char c, int keyCode) {
      return false;
   }

   public boolean clampScrolling() {
      return false;
   }

   public void onSelection(NonNullList<PanelButtonQuest> buttons) {
   }

   public boolean useSelection() {
      return false;
   }
}
