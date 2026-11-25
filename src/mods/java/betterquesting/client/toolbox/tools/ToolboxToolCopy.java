package betterquesting.client.toolbox.tools;

import betterquesting.api.client.toolbox.IToolboxTool;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api2.client.gui.controls.PanelButtonQuest;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.panels.lists.CanvasQuestLine;
import betterquesting.api2.storage.DBEntry;
import betterquesting.client.gui2.editors.designer.PanelToolController;
import betterquesting.client.toolbox.ToolboxTabMain;
import betterquesting.network.handlers.NetChapterEdit;
import betterquesting.network.handlers.NetQuestEdit;
import betterquesting.questing.QuestDatabase;
import betterquesting.questing.QuestLineDatabase;
import betterquesting.questing.QuestLineEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

public class ToolboxToolCopy implements IToolboxTool {
   private CanvasQuestLine gui = null;
   private final NonNullList<ToolboxToolCopy.GrabEntry> grabList = NonNullList.create();

   public ToolboxToolCopy() {
   }

   public void initTool(CanvasQuestLine gui) {
      this.gui = gui;
      this.grabList.clear();
   }

   public void disableTool() {
      this.grabList.clear();
   }

   public void refresh(CanvasQuestLine gui) {
      if (this.grabList.size() > 0) {
         List<ToolboxToolCopy.GrabEntry> tmp = new ArrayList();

         label27:
         for(ToolboxToolCopy.GrabEntry grab : this.grabList) {
            Iterator var5 = PanelToolController.selected.iterator();

            PanelButtonQuest btn;
            while(true) {
               if (!var5.hasNext()) {
                  continue label27;
               }

               btn = (PanelButtonQuest)var5.next();
               if (((DBEntry)btn.getStoredValue()).getID() == ((DBEntry)grab.btn.getStoredValue()).getID()) {
                  break;
               }
            }

            tmp.add(new ToolboxToolCopy.GrabEntry(btn, grab.offX, grab.offY));
         }

         this.grabList.clear();
         this.grabList.addAll(tmp);
      }
   }

   public void drawCanvas(int mx, int my, float partialTick) {
      if (this.grabList.size() > 0) {
         int snap = Math.max(1, ToolboxTabMain.INSTANCE.getSnapValue());
         int dx = (mx % snap + snap) % snap;
         int dy = (my % snap + snap) % snap;
         dx = mx - dx;
         dy = my - dy;

         for(ToolboxToolCopy.GrabEntry grab : this.grabList) {
            grab.btn.rect.x = dx + grab.offX;
            grab.btn.rect.y = dy + grab.offY;
            grab.btn.drawPanel(dx, dy, partialTick);
         }

      }
   }

   public void drawOverlay(int mx, int my, float partialTick) {
      if (this.grabList.size() > 0) {
         ToolboxTabMain.INSTANCE.drawGrid(this.gui);
      }

   }

   public List<String> getTooltip(int mx, int my) {
      return this.grabList.size() <= 0 ? null : Collections.emptyList();
   }

   public boolean onMouseClick(int mx, int my, int click) {
      if (click == 1 && this.grabList.size() > 0) {
         this.grabList.clear();
         return true;
      } else if (click == 0 && this.gui.getTransform().contains(mx, my)) {
         if (this.grabList.size() <= 0) {
            PanelButtonQuest btnClicked = this.gui.getButtonAt(mx, my);
            if (btnClicked == null) {
               return false;
            } else {
               if (PanelToolController.selected.size() > 0) {
                  if (!PanelToolController.selected.contains(btnClicked)) {
                     return false;
                  }

                  for(PanelButtonQuest btn : PanelToolController.selected) {
                     GuiRectangle rect = new GuiRectangle(btn.rect);
                     this.grabList.add(new ToolboxToolCopy.GrabEntry(new PanelButtonQuest(rect, -1, "", (DBEntry)btn.getStoredValue()), rect.x - btnClicked.rect.x, rect.y - btnClicked.rect.y));
                  }
               } else {
                  this.grabList.add(new ToolboxToolCopy.GrabEntry(new PanelButtonQuest(new GuiRectangle(btnClicked.rect), -1, "", (DBEntry)btnClicked.getStoredValue()), 0, 0));
               }

               return true;
            }
         } else {
            IQuestLine qLine = this.gui.getQuestLine();
            int lID = QuestLineDatabase.INSTANCE.getID(qLine);
            int[] nextIDs = this.getNextIDs(this.grabList.size());
            HashMap<Integer, Integer> remappedIDs = new HashMap();

            for(int i = 0; i < this.grabList.size(); ++i) {
               remappedIDs.put(Integer.valueOf(((DBEntry)((ToolboxToolCopy.GrabEntry)this.grabList.get(i)).btn.getStoredValue()).getID()), Integer.valueOf(nextIDs[i]));
            }

            NBTTagList qdList = new NBTTagList();

            for(int i = 0; i < this.grabList.size(); ++i) {
               ToolboxToolCopy.GrabEntry grab = (ToolboxToolCopy.GrabEntry)this.grabList.get(i);
               IQuest quest = (IQuest)((DBEntry)grab.btn.getStoredValue()).getValue();
               int qID = nextIDs[i];
               if (qLine.getValue(qID) == null) {
                  qLine.add(qID, new QuestLineEntry(grab.btn.rect.x, grab.btn.rect.y, grab.btn.rect.w, grab.btn.rect.h));
               }

               NBTTagCompound questTags = (NBTTagCompound)quest.writeToNBT(new NBTTagCompound());
               int[] oldIDs = Arrays.copyOf(quest.getRequirements(), quest.getRequirements().length);

               for(int n = 0; n < oldIDs.length; ++n) {
                  if (remappedIDs.containsKey(Integer.valueOf(oldIDs[n]))) {
                     oldIDs[n] = ((Integer)remappedIDs.get(Integer.valueOf(oldIDs[n]))).intValue();
                  }
               }

               questTags.setIntArray("preRequisites", oldIDs);
               NBTTagCompound tagEntry = new NBTTagCompound();
               tagEntry.setInteger("questID", qID);
               tagEntry.setTag("config", questTags);
               qdList.appendTag(tagEntry);
            }

            this.grabList.clear();
            NBTTagCompound quPayload = new NBTTagCompound();
            quPayload.setTag("data", qdList);
            quPayload.setInteger("action", 3);
            NetQuestEdit.sendEdit(quPayload);
            NBTTagCompound chPayload = new NBTTagCompound();
            NBTTagList cdList = new NBTTagList();
            NBTTagCompound tagEntry = new NBTTagCompound();
            tagEntry.setInteger("chapterID", lID);
            tagEntry.setTag("config", qLine.writeToNBT(new NBTTagCompound(), (List)null));
            cdList.appendTag(tagEntry);
            chPayload.setTag("data", cdList);
            chPayload.setInteger("action", 0);
            NetChapterEdit.sendEdit(chPayload);
            return true;
         }
      } else {
         return false;
      }
   }

   private int[] getNextIDs(int num) {
      List<DBEntry<IQuest>> listDB = QuestDatabase.INSTANCE.getEntries();
      int[] nxtIDs = new int[num];
      if (listDB.size() > 0 && ((DBEntry)listDB.get(listDB.size() - 1)).getID() != listDB.size() - 1) {
         int n1 = 0;
         int n2 = 0;

         for(int i = 0; i < num; ++i) {
            while(n2 < listDB.size() && ((DBEntry)listDB.get(n2)).getID() == n1) {
               ++n1;
               ++n2;
            }

            nxtIDs[i] = n1++;
         }

         return nxtIDs;
      } else {
         for(int i = 0; i < num; ++i) {
            nxtIDs[i] = listDB.size() + i;
         }

         return nxtIDs;
      }
   }

   public boolean onMouseRelease(int mx, int my, int click) {
      return false;
   }

   public boolean onMouseScroll(int mx, int my, int scroll) {
      return false;
   }

   public boolean onKeyPressed(char c, int keyCode) {
      return this.grabList.size() > 0;
   }

   public boolean clampScrolling() {
      return this.grabList.size() <= 0;
   }

   public void onSelection(NonNullList<PanelButtonQuest> buttons) {
   }

   public boolean useSelection() {
      return this.grabList.size() <= 0;
   }

   private class GrabEntry {
      private final PanelButtonQuest btn;
      private final int offX;
      private final int offY;

      private GrabEntry(PanelButtonQuest btn, int offX, int offY) {
         this.btn = btn;
         this.offX = offX;
         this.offY = offY;
      }
   }
}
