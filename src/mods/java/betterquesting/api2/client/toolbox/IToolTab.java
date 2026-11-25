package betterquesting.api2.client.toolbox;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.panels.lists.CanvasQuestLine;
import betterquesting.client.gui2.editors.designer.PanelToolController;

public interface IToolTab {
   String getUnlocalisedName();

   IGuiPanel getTabGui(IGuiRect var1, CanvasQuestLine var2, PanelToolController var3);
}
