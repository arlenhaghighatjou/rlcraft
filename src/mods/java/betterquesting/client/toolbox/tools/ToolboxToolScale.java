package betterquesting.client.toolbox.tools;

import betterquesting.api.client.toolbox.IToolboxTool;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.questing.IQuestLineEntry;
import betterquesting.api2.client.gui.controls.PanelButtonQuest;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.panels.lists.CanvasQuestLine;
import betterquesting.api2.client.gui.resources.colors.GuiColorPulse;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.lines.BoxLine;
import betterquesting.api2.client.gui.resources.lines.IGuiLine;
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
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.vector.Vector4f;

public class ToolboxToolScale implements IToolboxTool {
   private CanvasQuestLine gui;
   private final NonNullList<ToolboxToolScale.GrabEntry> grabList = NonNullList.create();
   private final GuiRectangle scaleBounds = new GuiRectangle(0, 0, 0, 0);
   private IGuiLine selLine = new BoxLine();
   private IGuiColor selCol = new GuiColorPulse(-1, -16777216, 2.0D, 0.0F);

   public ToolboxToolScale() {
   }

   public void initTool(CanvasQuestLine gui) {
      this.gui = gui;
      this.grabList.clear();
   }

   public void disableTool() {
      if (this.grabList.size() > 0) {
         for(ToolboxToolScale.GrabEntry grab : this.grabList) {
            IQuestLineEntry qle = (IQuestLineEntry)this.gui.getQuestLine().getValue(((DBEntry)grab.btn.getStoredValue()).getID());
            if (qle != null) {
               grab.btn.rect.x = qle.getPosX();
               grab.btn.rect.y = qle.getPosY();
               grab.btn.rect.w = qle.getSizeX();
               grab.btn.rect.h = qle.getSizeY();
            }
         }

         this.grabList.clear();
      }

   }

   public void refresh(CanvasQuestLine gui) {
      List<ToolboxToolScale.GrabEntry> tmp = new ArrayList();

      label23:
      for(ToolboxToolScale.GrabEntry grab : this.grabList) {
         Iterator var5 = PanelToolController.selected.iterator();

         PanelButtonQuest btn;
         while(true) {
            if (!var5.hasNext()) {
               continue label23;
            }

            btn = (PanelButtonQuest)var5.next();
            if (((DBEntry)btn.getStoredValue()).getID() == ((DBEntry)grab.btn.getStoredValue()).getID()) {
               break;
            }
         }

         tmp.add(new ToolboxToolScale.GrabEntry(btn, grab.anchor));
      }

      this.grabList.clear();
      this.grabList.addAll(tmp);
   }

   public void drawCanvas(int mx, int my, float partialTick) {
      if (this.grabList.size() > 0) {
         int snap = Math.max(1, ToolboxTabMain.INSTANCE.getSnapValue());
         int dx = mx + snap / 2;
         int dy = my + snap / 2;
         dx = (dx % snap + snap) % snap;
         dy = (dy % snap + snap) % snap;
         dx = mx + snap / 2 - dx;
         dy = my + snap / 2 - dy;
         this.scaleBounds.w = dx - this.scaleBounds.x;
         this.scaleBounds.h = dy - this.scaleBounds.y;
         boolean shift = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);

         for(ToolboxToolScale.GrabEntry grab : this.grabList) {
            grab.btn.rect.w = Math.max(1, Math.round((float)this.scaleBounds.w * grab.anchor.z));
            grab.btn.rect.h = Math.max(1, Math.round((float)this.scaleBounds.h * grab.anchor.w));
            if (shift) {
               grab.btn.rect.x = grab.sx - grab.btn.rect.w / 2;
               grab.btn.rect.y = grab.sy - grab.btn.rect.h / 2;
            } else {
               grab.btn.rect.x = this.scaleBounds.x + Math.round((float)this.scaleBounds.w * grab.anchor.x);
               grab.btn.rect.y = this.scaleBounds.y + Math.round((float)this.scaleBounds.h * grab.anchor.y);
            }
         }

         if (this.grabList.size() > 1 && !shift) {
            this.selLine.drawLine(this.scaleBounds, this.scaleBounds, 2, this.selCol, partialTick);
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
         for(ToolboxToolScale.GrabEntry grab : this.grabList) {
            IQuestLineEntry qle = (IQuestLineEntry)this.gui.getQuestLine().getValue(((DBEntry)grab.btn.getStoredValue()).getID());
            if (qle != null) {
               grab.btn.rect.x = qle.getPosX();
               grab.btn.rect.y = qle.getPosY();
               grab.btn.rect.w = qle.getSizeX();
               grab.btn.rect.h = qle.getSizeY();
            }
         }

         this.grabList.clear();
         return true;
      } else if (click == 0 && this.gui.getTransform().contains(mx, my)) {
         if (this.grabList.size() > 0) {
            IQuestLine qLine = this.gui.getQuestLine();
            int lID = QuestLineDatabase.INSTANCE.getID(qLine);

            for(ToolboxToolScale.GrabEntry grab : this.grabList) {
               IQuestLineEntry qle = (IQuestLineEntry)this.gui.getQuestLine().getValue(((DBEntry)grab.btn.getStoredValue()).getID());
               if (qle != null) {
                  qle.setPosition(grab.btn.rect.x, grab.btn.rect.y);
                  qle.setSize(grab.btn.rect.w, grab.btn.rect.h);
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

                  boolean first = true;

                  for(PanelButtonQuest btn : PanelToolController.selected) {
                     if (first) {
                        this.scaleBounds.x = btn.rect.x;
                        this.scaleBounds.y = btn.rect.y;
                        this.scaleBounds.w = btn.rect.w;
                        this.scaleBounds.h = btn.rect.h;
                        first = false;
                     } else {
                        this.scaleBounds.x = Math.min(this.scaleBounds.x, btn.rect.x);
                        this.scaleBounds.y = Math.min(this.scaleBounds.y, btn.rect.y);
                        this.scaleBounds.w = Math.max(this.scaleBounds.x + this.scaleBounds.w, btn.rect.x + btn.rect.w) - this.scaleBounds.x;
                        this.scaleBounds.h = Math.max(this.scaleBounds.y + this.scaleBounds.h, btn.rect.y + btn.rect.h) - this.scaleBounds.y;
                     }
                  }

                  for(PanelButtonQuest btn : PanelToolController.selected) {
                     float x = (float)(btn.rect.x - this.scaleBounds.x) / (float)this.scaleBounds.w;
                     float y = (float)(btn.rect.y - this.scaleBounds.y) / (float)this.scaleBounds.h;
                     float w = (float)btn.rect.w / (float)this.scaleBounds.w;
                     float h = (float)btn.rect.h / (float)this.scaleBounds.h;
                     this.grabList.add(new ToolboxToolScale.GrabEntry(btn, new Vector4f(x, y, w, h)));
                  }
               } else {
                  this.scaleBounds.x = btnClicked.rect.x;
                  this.scaleBounds.y = btnClicked.rect.y;
                  this.scaleBounds.w = btnClicked.rect.w;
                  this.scaleBounds.h = btnClicked.rect.h;
                  this.grabList.add(new ToolboxToolScale.GrabEntry(btnClicked, new Vector4f(0.0F, 0.0F, 1.0F, 1.0F)));
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

   public boolean onKeyPressed(char c, int key) {
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
      private final Vector4f anchor;
      private final int sx;
      private final int sy;

      private GrabEntry(PanelButtonQuest btn, Vector4f anchor) {
         this.btn = btn;
         this.anchor = anchor;
         this.sx = btn.rect.x + btn.rect.w / 2;
         this.sy = btn.rect.y + btn.rect.h / 2;
      }
   }
}
