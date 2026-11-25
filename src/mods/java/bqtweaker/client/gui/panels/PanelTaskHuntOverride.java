package bqtweaker.client.gui.panels;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api2.client.gui.controls.io.ValueFuncIO;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.utils.QuestTranslation;
import bq_standard.tasks.TaskHunt;
import java.util.concurrent.Callable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;

public class PanelTaskHuntOverride extends CanvasEmpty {
   private final TaskHunt task;

   public PanelTaskHuntOverride(IGuiRect rect, TaskHunt task) {
      super(rect);
      this.task = task;
   }

   public void initPanel() {
      super.initPanel();
      ResourceLocation targetRes = new ResourceLocation(this.task.idName);
      Entity target;
      if (EntityList.isRegistered(targetRes)) {
         target = EntityList.createEntityByIDFromName(targetRes, Minecraft.getMinecraft().world);
         if (target != null) {
            target.readFromNBT(this.task.targetTags);
         }
      } else {
         target = null;
      }

      int progress = this.task.getUsersProgress(QuestingAPI.getQuestingUUID(Minecraft.getMinecraft().player));
      String tnm = target != null ? target.getName() : this.task.idName;
      this.addPanel((new PanelTextBox(new GuiTransform(GuiAlign.TOP_EDGE, new GuiPadding(0, 0, 0, -16), 0), QuestTranslation.translate("bq_standard.gui.kill", new Object[]{tnm}) + " " + progress + "/" + this.task.required)).setAlignment(1).setColor(PresetColor.TEXT_MAIN.getColor()));
      if (target != null) {
         this.addPanel((new PanelEntityPreviewOverride(new GuiTransform(GuiAlign.FULL_BOX, new GuiPadding(0, 16, 0, 0), 0), target)).setRotationDriven(new ValueFuncIO(() -> {
            return 15.0F;
         }), new ValueFuncIO(() -> {
            return (float)((double)(Minecraft.getSystemTime() % 30000L) / 30000.0D * 360.0D);
         })));
      }

   }
}
