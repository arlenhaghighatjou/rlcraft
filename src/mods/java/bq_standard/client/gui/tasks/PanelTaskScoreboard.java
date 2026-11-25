package bq_standard.client.gui.tasks;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.content.PanelTextBox;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import bq_standard.ScoreboardBQ;
import bq_standard.tasks.TaskScoreboard;
import java.text.DecimalFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.util.vector.Vector4f;

public class PanelTaskScoreboard extends CanvasEmpty {
   private final TaskScoreboard task;

   public PanelTaskScoreboard(IGuiRect rect, TaskScoreboard task) {
      super(rect);
      this.task = task;
   }

   public void initPanel() {
      super.initPanel();
      int score = ScoreboardBQ.INSTANCE.getScore(QuestingAPI.getQuestingUUID(Minecraft.getMinecraft().player), this.task.scoreName);
      DecimalFormat df = new DecimalFormat("0.##");
      String value = df.format((double)((float)score / this.task.conversion)) + this.task.suffix;
      if (this.task.operation.checkValues(score, this.task.target)) {
         value = TextFormatting.GREEN + value;
      } else {
         value = TextFormatting.RED + value;
      }

      String txt2 = TextFormatting.BOLD + value + " " + TextFormatting.RESET + this.task.operation.GetText() + " " + df.format((double)((float)this.task.target / this.task.conversion)) + this.task.suffix;
      this.addPanel((new PanelTextBox(new GuiTransform(new Vector4f(0.0F, 0.5F, 1.0F, 0.5F), new GuiPadding(0, -16, 0, 0), 0), this.task.scoreDisp)).setAlignment(1).setColor(PresetColor.TEXT_MAIN.getColor()));
      this.addPanel((new PanelTextBox(new GuiTransform(new Vector4f(0.0F, 0.5F, 1.0F, 0.5F), new GuiPadding(0, 0, 0, -16), 0), txt2)).setAlignment(1).setColor(PresetColor.TEXT_MAIN.getColor()));
   }
}
