package betterquesting.api2.client.gui;

import betterquesting.api2.client.gui.panels.IGuiCanvas;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import javax.annotation.Nonnull;

public interface IScene extends IGuiCanvas {
   void openPopup(@Nonnull IGuiPanel var1);

   void closePopup();
}
