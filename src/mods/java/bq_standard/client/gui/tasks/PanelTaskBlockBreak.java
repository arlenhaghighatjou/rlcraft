package bq_standard.client.gui.tasks;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.bars.PanelVScrollBar;
import betterquesting.api2.client.gui.panels.content.PanelItemSlot;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.utils.QuestTranslation;
import bq_standard.NbtBlockType;
import bq_standard.tasks.TaskBlockBreak;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

public class PanelTaskBlockBreak extends CanvasEmpty {
   private final TaskBlockBreak task;

   public PanelTaskBlockBreak(IGuiRect rect, TaskBlockBreak task) {
      super(rect);
      this.task = task;
   }

   public void initPanel() {
      super.initPanel();
      EaglercraftUUID uuid = QuestingAPI.getQuestingUUID(Minecraft.getMinecraft().player);
      int[] progress = this.task.getUsersProgress(uuid);
      boolean isComplete = this.task.isComplete(uuid);
      CanvasScrolling cvList = new CanvasScrolling(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 0, 8, 0), 0));
      this.addPanel(cvList);
      PanelVScrollBar scList = new PanelVScrollBar(new GuiTransform(GuiAlign.RIGHT_EDGE, new GuiPadding(-8, 0, 0, 0), 0));
      this.addPanel(scList);
      cvList.setScrollDriverY(scList);
      int listW = cvList.getTransform().getWidth();

      for(int i = 0; i < this.task.blockTypes.size(); ++i) {
         BigItemStack stack = ((NbtBlockType)this.task.blockTypes.get(i)).getItemStack();
         if (stack != null) {
            PanelItemSlot slot = new PanelItemSlot(new GuiRectangle(0, i * 36, 36, 36, 0), -1, stack, true, true);
            cvList.addPanel(slot);
            StringBuilder sb = new StringBuilder();
            sb.append(stack.getBaseStack().getDisplayName());
            if (stack.hasOreDict()) {
               sb.append(" (").append(stack.getOreDict()).append(")");
            }

            sb.append("\n").append(progress[i]).append("/").append(stack.stackSize).append("\n");
            if (progress[i] < stack.stackSize && !isComplete) {
               sb.append(TextFormatting.RED).append(QuestTranslation.translate("betterquesting.tooltip.incomplete", new Object[0]));
            } else {
               sb.append(TextFormatting.GREEN).append(QuestTranslation.translate("betterquesting.tooltip.complete", new Object[0]));
            }

            PanelTextBox text = new PanelTextBox(new GuiRectangle(40, i * 36, listW - 40, 36, 0), sb.toString());
            text.setColor(PresetColor.TEXT_MAIN.getColor());
            cvList.addPanel(text);
         }
      }

   }
}
