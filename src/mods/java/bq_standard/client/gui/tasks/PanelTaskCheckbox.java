package bq_standard.client.gui.tasks;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.IQuest;
import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.resources.colors.GuiColorStatic;
import betterquesting.api2.client.gui.themes.presets.PresetIcon;
import betterquesting.api2.storage.DBEntry;
import bq_standard.network.handlers.NetTaskCheckbox;
import bq_standard.tasks.TaskCheckbox;
import net.minecraft.client.Minecraft;

public class PanelTaskCheckbox extends CanvasEmpty {
   private final DBEntry<IQuest> quest;
   private final TaskCheckbox task;

   public PanelTaskCheckbox(IGuiRect rect, DBEntry<IQuest> quest, TaskCheckbox task) {
      super(rect);
      this.quest = quest;
      this.task = task;
   }

   public void initPanel() {
      super.initPanel();
      boolean isComplete = this.task.isComplete(QuestingAPI.getQuestingUUID(Minecraft.getMinecraft().player));
      final int questID = this.quest.getID();
      final int taskID = ((IQuest)this.quest.getValue()).getTasks().getID(this.task);
      PanelButton btnCheck = new PanelButton(new GuiTransform(GuiAlign.MID_CENTER, -16, -16, 32, 32, 0), -1, "") {
         public void onButtonClick() {
            this.setIcon(PresetIcon.ICON_TICK.getTexture(), new GuiColorStatic(-16711936), 4);
            this.setActive(false);
            NetTaskCheckbox.requestClick(questID, taskID);
         }
      };
      btnCheck.setIcon(isComplete ? PresetIcon.ICON_TICK.getTexture() : PresetIcon.ICON_CROSS.getTexture(), new GuiColorStatic(isComplete ? -16711936 : -65536), 4);
      btnCheck.setActive(!isComplete);
      this.addPanel(btnCheck);
   }
}
