package betterquesting.client.toolbox.tools;

import betterquesting.api.client.toolbox.IToolboxTool;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.questing.IQuestLineEntry;
import betterquesting.api2.client.gui.controls.PanelButtonQuest;
import betterquesting.api2.client.gui.panels.lists.CanvasQuestLine;
import betterquesting.api2.storage.DBEntry;
import betterquesting.client.gui2.editors.designer.PanelToolController;
import betterquesting.client.toolbox.ToolboxTabMain;
import betterquesting.network.handlers.NetChapterEdit;
import betterquesting.questing.QuestLineDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

public class ToolboxToolGrab implements IToolboxTool {
   private CanvasQuestLine gui;
   private final NonNullList<ToolboxToolGrab.GrabEntry> grabList = NonNullList.create();

   public ToolboxToolGrab() {
   }

   public void initTool(CanvasQuestLine gui) {
      this.gui = gui;
      this.grabList.clear();
   }

   public void disableTool() {
      for(ToolboxToolGrab.GrabEntry grab : this.grabList) {
         IQuestLineEntry qle = (IQuestLineEntry)this.gui.getQuestLine().getValue(((DBEntry)grab.btn.getStoredValue()).getID());
         if (qle != null) {
            grab.btn.rect.x = qle.getPosX();
            grab.btn.rect.y = qle.getPosY();
         }
      }

      this.grabList.clear();
   }

   public void refresh(CanvasQuestLine gui) {
      if (this.grabList.size() > 0) {
         List<ToolboxToolGrab.GrabEntry> tmp = new ArrayList();

         label27:
         for(ToolboxToolGrab.GrabEntry grab : this.grabList) {
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

            tmp.add(new ToolboxToolGrab.GrabEntry(btn, grab.offX, grab.offY));
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

         for(ToolboxToolGrab.GrabEntry grab : this.grabList) {
            grab.btn.rect.x = dx + grab.offX;
            grab.btn.rect.y = dy + grab.offY;
         }

      }
   }

   public void drawOverlay(int mx, int my, float partialTick) {
      ToolboxTabMain.INSTANCE.drawGrid(this.gui);
   }

   public List<String> getTooltip(int mx, int my) {
      if (this.grabList.size() <= 0) {
         return null;
      } else {
         for(ToolboxToolGrab.GrabEntry grab : this.grabList) {
            if (grab.offX == 0 && grab.offY == 0) {
               List<String> list = new ArrayList();
               list.add("X: " + grab.btn.rect.x);
               list.add("Y: " + grab.btn.rect.y);
               return list;
            }
         }

         return Collections.emptyList();
      }
   }

   public void onSelection(NonNullList<PanelButtonQuest> buttons) {
   }

   public boolean onMouseClick(int mx, int my, int click) {
      if (click == 1 && this.grabList.size() > 0) {
         for(ToolboxToolGrab.GrabEntry grab : this.grabList) {
            IQuestLineEntry qle = (IQuestLineEntry)this.gui.getQuestLine().getValue(((DBEntry)grab.btn.getStoredValue()).getID());
            if (qle != null) {
               grab.btn.rect.x = qle.getPosX();
               grab.btn.rect.y = qle.getPosY();
            }
         }

         this.grabList.clear();
         return true;
      } else if (click == 0 && this.gui.getTransform().contains(mx, my)) {
         if (this.grabList.size() > 0) {
            IQuestLine qLine = this.gui.getQuestLine();
            int lID = QuestLineDatabase.INSTANCE.getID(qLine);

            for(ToolboxToolGrab.GrabEntry grab : this.grabList) {
               IQuestLineEntry qle = (IQuestLineEntry)this.gui.getQuestLine().getValue(((DBEntry)grab.btn.getStoredValue()).getID());
               if (qle != null) {
                  qle.setPosition(grab.btn.rect.x, grab.btn.rect.y);
               }
            }

            NBTTagCompound chPayload = new NBTTagCompound();
            NBTTagList cdList = new NBTTagList();
            NBTTagCompound tagEntry = new NBTTagCompound();
            tagEntry.setInteger("chapterID", lID);
            tagEntry.setTag("config", qLine.writeToNBT(new NBTTagCompound(), (List)null));
            cdList.appendTag(tagEntry);
            chPayload.setTag("data", cdList);
            chPayload.setInteger("action", 0);
            NetChapterEdit.sendEdit(chPayload);
            this.grabList.clear();
            return true;
         } else {
            PanelButtonQuest btnClicked = this.gui.getButtonAt(mx, my);
            if (btnClicked == null) {
               return false;
            } else {
               if (PanelToolController.selected.size() > 0) {
                  if (!PanelToolController.selected.contains(btnClicked)) {
                     return false;
                  }

                  for(PanelButtonQuest btn : PanelToolController.selected) {
                     this.grabList.add(new ToolboxToolGrab.GrabEntry(btn, btn.rect.x - btnClicked.rect.x, btn.rect.y - btnClicked.rect.y));
                  }
               } else {
                  this.grabList.add(new ToolboxToolGrab.GrabEntry(btnClicked, 0, 0));
               }

               return true;
            }
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

   public boolean onKeyPressed(char c, int keyCode) {
      return this.grabList.size() > 0;
   }

   public boolean clampScrolling() {
      return this.grabList.size() <= 0;
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
