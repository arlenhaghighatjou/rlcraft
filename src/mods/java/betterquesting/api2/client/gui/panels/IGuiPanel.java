package betterquesting.api2.client.gui.panels;

import betterquesting.api2.client.gui.misc.IGuiRect;
import java.util.List;

public interface IGuiPanel {
   IGuiRect getTransform();

   void initPanel();

   void setEnabled(boolean var1);

   boolean isEnabled();

   void drawPanel(int var1, int var2, float var3);

   boolean onMouseClick(int var1, int var2, int var3);

   boolean onMouseRelease(int var1, int var2, int var3);

   boolean onMouseScroll(int var1, int var2, int var3);

   boolean onKeyTyped(char var1, int var2);

   List<String> getTooltip(int var1, int var2);
}
