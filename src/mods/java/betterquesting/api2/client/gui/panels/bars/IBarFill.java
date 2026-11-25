package betterquesting.api2.client.gui.panels.bars;

import betterquesting.api2.client.gui.controls.IValueIO;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;

public interface IBarFill extends IGuiPanel {
   IBarFill setFillDriver(IValueIO<Float> var1);

   IBarFill setFlipped(boolean var1);

   IBarFill setFillColor(IGuiColor var1);

   IBarFill setBarTexture(IGuiTexture var1, IGuiTexture var2);
}
