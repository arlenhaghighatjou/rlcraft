package betterquesting.api.client.toolbox;

import betterquesting.api2.client.gui.controls.PanelButtonQuest;
import betterquesting.api2.client.gui.panels.lists.CanvasQuestLine;
import java.util.List;
import net.minecraft.util.NonNullList;

public interface IToolboxTool {
   void initTool(CanvasQuestLine var1);

   void refresh(CanvasQuestLine var1);

   void disableTool();

   void drawCanvas(int var1, int var2, float var3);

   void drawOverlay(int var1, int var2, float var3);

   void onSelection(NonNullList<PanelButtonQuest> var1);

   boolean onMouseClick(int var1, int var2, int var3);

   boolean onMouseRelease(int var1, int var2, int var3);

   boolean onMouseScroll(int var1, int var2, int var3);

   boolean onKeyPressed(char var1, int var2);

   List<String> getTooltip(int var1, int var2);

   boolean clampScrolling();

   boolean useSelection();
}
