package betterquesting.client.toolbox.tools;

import betterquesting.api.client.toolbox.IToolboxTool;
import betterquesting.api.misc.ICallback;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.controls.PanelButtonQuest;
import betterquesting.api2.client.gui.panels.lists.CanvasQuestLine;
import betterquesting.api2.storage.DBEntry;
import betterquesting.client.gui2.editors.designer.PanelToolController;
import betterquesting.client.gui2.editors.nbt.GuiItemSelection;
import betterquesting.network.handlers.NetQuestEdit;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

public class ToolboxToolIcon implements IToolboxTool {
   private CanvasQuestLine gui;

   public ToolboxToolIcon() {
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
         if (btn == null) {
            return false;
         } else if (PanelToolController.selected.size() > 0 && !PanelToolController.selected.contains(btn)) {
            return false;
         } else {
            List<PanelButtonQuest> btnList = (List<PanelButtonQuest>)(PanelToolController.selected.size() > 0 ? PanelToolController.selected : Collections.singletonList(btn));
            this.changeIcon(btnList, (BigItemStack)((IQuest)((DBEntry)btn.getStoredValue()).getValue()).getProperty(NativeProps.ICON));
            return true;
         }
      } else {
         return false;
      }
   }

   private void changeIcon(List<PanelButtonQuest> list, BigItemStack refItem) {
      Minecraft mc = Minecraft.getMinecraft();
      mc.displayGuiScreen(new GuiItemSelection(mc.currentScreen, refItem, (value) -> {
         NBTTagList dataList = new NBTTagList();

         for(PanelButtonQuest b : list) {
            ((IQuest)((DBEntry)b.getStoredValue()).getValue()).setProperty(NativeProps.ICON, value);
            NBTTagCompound entry = new NBTTagCompound();
            entry.setInteger("questID", ((DBEntry)b.getStoredValue()).getID());
            entry.setTag("config", ((IQuest)((DBEntry)b.getStoredValue()).getValue()).writeToNBT(new NBTTagCompound()));
            dataList.appendTag(entry);
         }

         NBTTagCompound payload = new NBTTagCompound();
         payload.setTag("data", dataList);
         payload.setInteger("action", 0);
         NetQuestEdit.sendEdit(payload);
      }));
   }

   public boolean onMouseRelease(int mx, int my, int click) {
      return false;
   }

   public boolean onMouseScroll(int mx, int my, int scroll) {
      return false;
   }

   public boolean onKeyPressed(char c, int key) {
      if (PanelToolController.selected.size() > 0 && key == 28) {
         this.changeIcon(PanelToolController.selected, (BigItemStack)((IQuest)((DBEntry)((PanelButtonQuest)PanelToolController.selected.get(0)).getStoredValue()).getValue()).getProperty(NativeProps.ICON));
         return true;
      } else {
         return false;
      }
   }

   public void drawCanvas(int mx, int my, float partialTick) {
   }

   public void drawOverlay(int mx, int my, float partialTick) {
   }

   public List<String> getTooltip(int mx, int my) {
      return null;
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
