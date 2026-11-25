package betterquesting.api2.client.gui.panels;

import java.util.List;
import javax.annotation.Nonnull;

public interface IGuiCanvas extends IGuiPanel {
   void addPanel(IGuiPanel var1);

   boolean removePanel(IGuiPanel var1);

   @Nonnull
   List<IGuiPanel> getChildren();

   void resetCanvas();
}
