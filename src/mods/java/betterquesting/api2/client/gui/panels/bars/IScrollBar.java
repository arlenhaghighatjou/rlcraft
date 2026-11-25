package betterquesting.api2.client.gui.panels.bars;

import betterquesting.api2.client.gui.controls.IValueIO;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;

public interface IScrollBar extends IValueIO<Float>, IGuiPanel {
   void setActive(boolean var1);

   boolean isActive();

   IScrollBar setHandleSize(int var1, int var2);

   IScrollBar setBarTexture(IGuiTexture var1, IGuiTexture var2, IGuiTexture var3, IGuiTexture var4);

   IScrollBar setScrollSpeed(float var1);
}
