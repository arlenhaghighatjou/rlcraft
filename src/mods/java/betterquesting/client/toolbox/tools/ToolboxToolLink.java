package betterquesting.client.toolbox.tools;

import betterquesting.api.client.toolbox.IToolboxTool;
import betterquesting.api.questing.IQuest;
import betterquesting.api2.client.gui.controls.PanelButtonQuest;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.panels.lists.CanvasQuestLine;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetLine;
import betterquesting.api2.storage.DBEntry;
import betterquesting.client.gui2.editors.designer.PanelToolController;
import betterquesting.network.handlers.NetQuestEdit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

public class ToolboxToolLink implements IToolboxTool {
   private CanvasQuestLine gui;
   private final NonNullList<PanelButtonQuest> linking = NonNullList.create();
   private final GuiRectangle mouseRect = new GuiRectangle(0, 0, 0, 0);

   public ToolboxToolLink() {
   }

   public void initTool(CanvasQuestLine gui) {
      this.gui = gui;
      this.linking.clear();
   }

   public void disableTool() {
      this.linking.clear();
   }

   public void refresh(CanvasQuestLine gui) {
      if (this.linking.size() > 0) {
         List<PanelButtonQuest> tmp = new ArrayList();

         for(PanelButtonQuest b1 : this.linking) {
            for(PanelButtonQuest b2 : gui.getQuestButtons()) {
               if (((DBEntry)b1.getStoredValue()).getID() == ((DBEntry)b2.getStoredValue()).getID()) {
                  tmp.add(b2);
               }
            }
         }

         this.linking.clear();
         this.linking.addAll(tmp);
      }
   }

   public void drawCanvas(int mx, int my, float partialTick) {
      if (this.linking.size() > 0) {
         this.mouseRect.x = mx;
         this.mouseRect.y = my;

         for(PanelButtonQuest btn : this.linking) {
            PresetLine.QUEST_COMPLETE.getLine().drawLine(btn.rect, this.mouseRect, 2, PresetColor.QUEST_LINE_COMPLETE.getColor(), partialTick);
         }

      }
   }

   public void drawOverlay(int mx, int my, float partialTick) {
   }

   public List<String> getTooltip(int mx, int my) {
      return null;
   }

   public boolean onMouseClick(int mx, int my, int click) {
      if (click == 1 && this.linking.size() > 0) {
         this.linking.clear();
         return true;
      } else if (click == 0 && this.gui.getTransform().contains(mx, my)) {
         if (this.linking.size() <= 0) {
            PanelButtonQuest btn = this.gui.getButtonAt(mx, my);
            if (btn == null) {
               return false;
            } else if (PanelToolController.selected.size() > 0) {
               if (!PanelToolController.selected.contains(btn)) {
                  return false;
               } else {
                  this.linking.addAll(PanelToolController.selected);
                  return true;
               }
            } else {
               this.linking.add(btn);
               return true;
            }
         } else {
            PanelButtonQuest b2 = this.gui.getButtonAt(mx, my);
            if (b2 == null) {
               return false;
            } else {
               this.linking.remove(b2);
               if (this.linking.size() <= 0) {
                  return false;
               } else {
                  IQuest q2 = (IQuest)((DBEntry)b2.getStoredValue()).getValue();
                  boolean mod2 = false;
                  NBTTagList dataList = new NBTTagList();

                  for(PanelButtonQuest b1 : this.linking) {
                     IQuest q1 = (IQuest)((DBEntry)b1.getStoredValue()).getValue();
                     boolean mod1 = false;
                     if (!this.containsReq(q2, ((DBEntry)b1.getStoredValue()).getID()) && !this.containsReq(q1, ((DBEntry)b2.getStoredValue()).getID())) {
                        mod2 = this.addReq(q2, ((DBEntry)b1.getStoredValue()).getID()) || mod2;
                     } else {
                        mod2 = this.removeReq(q2, ((DBEntry)b1.getStoredValue()).getID()) || mod2;
                        mod1 = this.removeReq(q1, ((DBEntry)b2.getStoredValue()).getID());
                     }

                     if (mod1) {
                        NBTTagCompound entry = new NBTTagCompound();
                        entry.setInteger("questID", ((DBEntry)b1.getStoredValue()).getID());
                        entry.setTag("config", ((IQuest)((DBEntry)b1.getStoredValue()).getValue()).writeToNBT(new NBTTagCompound()));
                        dataList.appendTag(entry);
                     }
                  }

                  if (mod2) {
                     NBTTagCompound entry = new NBTTagCompound();
                     entry.setInteger("questID", ((DBEntry)b2.getStoredValue()).getID());
                     entry.setTag("config", q2.writeToNBT(new NBTTagCompound()));
                     dataList.appendTag(entry);
                  }

                  NBTTagCompound payload = new NBTTagCompound();
                  payload.setTag("data", dataList);
                  payload.setInteger("action", 0);
                  NetQuestEdit.sendEdit(payload);
                  this.linking.clear();
                  return true;
               }
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
      return false;
   }

   public boolean clampScrolling() {
      return true;
   }

   public void onSelection(NonNullList<PanelButtonQuest> buttons) {
   }

   public boolean useSelection() {
      return this.linking.size() <= 0;
   }

   private boolean containsReq(IQuest quest, int id) {
      for(int reqID : quest.getRequirements()) {
         if (id == reqID) {
            return true;
         }
      }

      return false;
   }

   private boolean removeReq(IQuest quest, int id) {
      int[] orig = quest.getRequirements();
      if (orig.length <= 0) {
         return false;
      } else {
         boolean hasRemoved = false;
         int[] rem = new int[orig.length - 1];

         for(int i = 0; i < orig.length; ++i) {
            if (!hasRemoved && orig[i] == id) {
               hasRemoved = true;
            } else {
               if (!hasRemoved && i >= rem.length) {
                  break;
               }

               rem[!hasRemoved ? i : i - 1] = orig[i];
            }
         }

         if (hasRemoved) {
            quest.setRequirements(rem);
         }

         return hasRemoved;
      }
   }

   private boolean addReq(IQuest quest, int id) {
      if (this.containsReq(quest, id)) {
         return false;
      } else {
         int[] orig = quest.getRequirements();
         int[] added = Arrays.copyOf(orig, orig.length + 1);
         added[orig.length] = id;
         quest.setRequirements(added);
         return true;
      }
   }
}
